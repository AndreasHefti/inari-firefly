/*******************************************************************************
 * Copyright (c) 2015 - 2016, Andreas Hefti, inarisoft@yahoo.de 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/ 
package com.inari.firefly.graphics.tile;

import java.util.Iterator;

import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityActivationEvent;
import com.inari.firefly.entity.EntityActivationListener;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.tile.TileGrid.TileIterator;
import com.inari.firefly.graphics.view.ViewEvent;
import com.inari.firefly.graphics.view.ViewEvent.Type;
import com.inari.firefly.graphics.view.ViewEventListener;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public final class TileGridSystem
    extends 
        ComponentSystem<TileGridSystem>
    implements
        ViewEventListener,
        EntityActivationListener {
    
    public static final FFSystemTypeKey<TileGridSystem> SYSTEM_KEY = FFSystemTypeKey.create( TileGridSystem.class ); 
    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        TileGrid.TYPE_KEY,
        TileGridRenderer.TYPE_KEY
    };

    private EntitySystem entitySystem;
    
    private final DynArray<TileGridRenderer> renderer;
    private final DynArray<TileGrid> tileGrids;
    private final DynArray<DynArray<TileGrid>> tileGridOfViewsPerLayer;
    
    public TileGridSystem() {
        super( SYSTEM_KEY );
        renderer = new DynArray<TileGridRenderer>();
        tileGrids = new DynArray<TileGrid>();
        tileGridOfViewsPerLayer = new DynArray<DynArray<TileGrid>>();
    }
    
    @Override
    public void init( FFContext context ) {
        super.init( context );
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        
        // build and register default tile grid renderer
        getRendererBuilder()
            .set( TileGridRenderer.NAME, NormalFastTileGridRenderer.NAME )
            .buildAndNext( NormalFastTileGridRenderer.class )
            .set( TileGridRenderer.NAME, NormalFullTileGridRenderer.NAME )
            .build( NormalFullTileGridRenderer.class );

        context.registerListener( ViewEvent.TYPE_KEY, this );
        context.registerListener( EntityActivationEvent.TYPE_KEY, this );
        context.registerListener( TileSystemEvent.TYPE_KEY, this );
    }

    public final TileGridRendererBuilder getRendererBuilder() {
        return new TileGridRendererBuilder();
    }

    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( ViewEvent.TYPE_KEY, this );
        context.disposeListener( EntityActivationEvent.TYPE_KEY, this );
        context.disposeListener( TileSystemEvent.TYPE_KEY, this );
        
        for ( TileGridRenderer r : renderer ) {
            context.disposeListener( RenderEvent.TYPE_KEY, r );
            r.dispose();
        }
        
        clear();
    }

    final void removeMultiTilePosition( final int tileGridId, final int entityId, final int x, final int y ) {
        ETile tile = entitySystem.getComponent( entityId, ETile.TYPE_KEY );
        tile.getGridPositions().remove( new Position( x, y ) );
        getTileGrid( tileGridId ).reset( x, y );
    }

    final void addMultiTilePosition( final int tileGridId, final int entityId, final int x, final int y ) {
        ETile tile = entitySystem.getComponent( entityId, ETile.TYPE_KEY );
        tile.getGridPositions().add( new Position( x, y ) );
        getTileGrid( tileGridId ).set( entityId, x, y );
    }

    @Override
    public final void onEntityActivationEvent( EntityActivationEvent event ) {
        switch ( event.eventType ) {
            case ENTITY_ACTIVATED: {
                registerEntity( event.entityId, event.entityComponentAspects );
                break;
            }
            case ENTITY_DEACTIVATED: {
                unregisterEntity( event.entityId, event.entityComponentAspects );
                break;
            }
        }
    }
    
    @Override
    public final void onViewEvent( ViewEvent event ) {
        if ( event.eventType == Type.VIEW_DELETED ) {
            deleteAllTileGrid( event.view.index() );
            return;
        }
    }
    
    @Override
    public final boolean match( Aspects aspects ) {
        return aspects.contains( ETile.TYPE_KEY );
    }
    
    public final TileGridRenderer getRenderer( int id ) {
        if ( renderer.contains( id ) ) {
            return renderer.get( id );
        }
        
        return null;
    }

    public final int getRendererId( String name ) {
        for ( TileGridRenderer r : renderer ) {
            if ( name.equals( r.getName() ) ) {
                return r.index();
            }
        }
        
        return -1;
    }

    public final void deleteRenderer( int id ) {
        TileGridRenderer r = renderer.remove( id );
        if ( r != null ) {
            context.disposeListener( RenderEvent.TYPE_KEY, r );
            r.dispose();
        }
    }
    
    public final boolean hasTileGrid( int viewId, int layerId ) {
        return getTileGrid( viewId, layerId ) != null;
    }
    
    public final TileGrid getTileGrid( String tileGridName ) {
        for ( TileGrid tileGrid : tileGrids ) {
            if ( tileGrid != null && tileGrid.getName().equals(  tileGridName ) ) {
                return tileGrid;
            }
        }
        return null;
    }
    
    public final TileGrid getTileGrid( int tileGridId ) {
        for ( TileGrid tileGrid : tileGrids ) {
            if ( tileGrid != null && tileGrid.index() == tileGridId ) {
                return tileGrid;
            }
        }
        return null;
    }

    public final TileGrid getTileGrid( int viewId, int layerId ) {
        if ( !tileGridOfViewsPerLayer.contains( viewId ) ) {
            return null;
        }
        
        DynArray<TileGrid> tileGridsForView = tileGridOfViewsPerLayer.get( viewId );
        return tileGridsForView.get( layerId );
    }
    
    public final int getTile( int viewId, int layerId, final Position position ) {
        TileGrid tileGrid = getTileGrid( viewId, layerId );
        if ( tileGrid == null ) {
            return -1;
        }
        
        return tileGrid.getTileAt( position );
    }
    
    public final int getTile( int tileGridId, final Position position ) {
        TileGrid tileGrid = getTileGrid( tileGridId );
        if ( tileGrid == null ) {
            return -1;
        }
        
        return tileGrid.getTileAt( position );
    }
    
    public final TileIterator getTiles( int viewId, int layerId, Rectangle bounds ) {
        TileGrid tileGrid = getTileGrid( viewId, layerId );
        if ( tileGrid == null ) {
            return null;
        }
        
        return tileGrid.iterator( bounds );
    }
    
    public final TileIterator getTiles( int tileGridId, Rectangle bounds ) {
        TileGrid tileGrid = getTileGrid( tileGridId );
        if ( tileGrid == null ) {
            return null;
        }
        
        return tileGrid.iterator( bounds );
    }

    public final void deleteAllTileGrid( int viewId ) {
        if ( tileGridOfViewsPerLayer.contains( viewId ) ) {
            DynArray<TileGrid> toRemove = tileGridOfViewsPerLayer.remove( viewId );
            for ( TileGrid tileGrid : toRemove ) {
                tileGrids.remove( tileGrid.index() );
                disposeSystemComponent( tileGrid );
            }
        }
    }
    
    public final void deleteTileGrid( int viewId, int layerId ) {
        if ( !tileGridOfViewsPerLayer.contains( viewId ) ) {
            return;
        }
        DynArray<TileGrid> tileGridsForView = tileGridOfViewsPerLayer.get( viewId );
        if ( tileGridsForView == null ) {
            return;
        }
        
        if ( !tileGridOfViewsPerLayer.contains( layerId ) ) {
            return;
        }
        
        TileGrid removed = tileGridsForView.remove( layerId );
        tileGrids.remove( removed.index() );
        disposeSystemComponent( removed );
    }
    
    public final void deleteTileGrid( int tileGridId ) {
        if ( !tileGrids.contains( tileGridId ) ) {
            return;
        }
        
        TileGrid removed = tileGrids.get( tileGridId );
        tileGridOfViewsPerLayer.get( removed.getViewId() ).remove( removed.getLayerId() );
    };
    
    private final void registerEntity( int entityId, Aspects entityAspects ) {
        ETransform transform = entitySystem.getComponent( entityId, ETransform.TYPE_KEY );
        ETile tile = entitySystem.getComponent( entityId, ETile.TYPE_KEY );
        TileGrid tileGrid = getTileGrid( transform.getViewId(), transform.getLayerId() );
        if ( tile.isMultiPosition() ) {
            for ( Position gridPosition : tile.getGridPositions() ) {
                tileGrid.set( entityId, gridPosition.x, gridPosition.y );
            }
        } else {
            Position gridPosition = tile.getGridPosition();
            tileGrid.set( entityId, gridPosition.x, gridPosition.y );
        }
    }
    
    private final void unregisterEntity( int entityId, Aspects entityAspects ) {
        ETransform transform = entitySystem.getComponent( entityId, ETransform.TYPE_KEY );
        ETile tile = entitySystem.getComponent( entityId, ETile.TYPE_KEY );
        TileGrid tileGrid = getTileGrid( transform.getViewId(), transform.getLayerId() );
        if ( tile.isMultiPosition() ) {
            for ( Position gridPosition : tile.getGridPositions() ) {
                tileGrid.resetIfMatch( entityId, gridPosition.x, gridPosition.y );
            }
        } else {
            Position gridPosition = tile.getGridPosition();
            tileGrid.resetIfMatch( entityId, gridPosition.x, gridPosition.y );
        }
    }

    public final TileGridBuilder getTileGridBuilder() {
        return new TileGridBuilder();
    }
    
    @Override
    public final SystemComponentKey<?>[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
            new TileGridBuilderAdapter( this ),
            new TileGridRendererBuilderAdapter( this )
        };
    }

    @Override
    public final void clear() {
        tileGridOfViewsPerLayer.clear();
        renderer.clear();
    }
    
    public final class TileGridBuilder extends SystemComponentBuilder {
        
        public TileGridBuilder() {
            super( context );
        }
        
        @Override
        public final SystemComponentKey<TileGrid> systemComponentKey() {
            return TileGrid.TYPE_KEY;
        }
        
        @Override
        public int doBuild( int componentId, Class<?> subType, boolean activate ) {
            TileGrid tileGrid = createSystemComponent( componentId, subType, context );

            int viewId = tileGrid.getViewId();
            int layerId = tileGrid.getLayerId();
            
            if ( viewId < 0 ) {
                throw new FFInitException( "ViewId is mandatory for TileGrid" );
            }
            
            if ( layerId < 0 ) {
                throw new FFInitException( "LayerId is mandatory for TileGrid" );
            }
            
            if ( !tileGridOfViewsPerLayer.contains( viewId ) ) {
                tileGridOfViewsPerLayer.set( viewId, new DynArray<TileGrid>() );
            }

            tileGrids.set( tileGrid.index(), tileGrid );
            tileGridOfViewsPerLayer
                .get( viewId )
                .set( layerId, tileGrid );

            return tileGrid.index();
        }
    }
    
    public final class TileGridRendererBuilder extends SystemComponentBuilder {
        
        private TileGridRendererBuilder() {
            super( context );
        }
        
        @Override
        public final SystemComponentKey<TileGridRenderer> systemComponentKey() {
            return TileGridRenderer.TYPE_KEY;
        }

        @Override
        public int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            TileGridRenderer component = createSystemComponent( componentId, componentType, context );
            renderer.set( component.index(), component );
            return component.index();
        }
    }
    
    private final class TileGridBuilderAdapter extends SystemBuilderAdapter<TileGrid> {
        public TileGridBuilderAdapter( TileGridSystem system ) {
            super( system, new TileGridBuilder() );
        }
        @Override
        public final SystemComponentKey<TileGrid> componentTypeKey() {
            return TileGrid.TYPE_KEY;
        }
        @Override
        public final TileGrid getComponent( int id ) {
            return getTileGrid( id );
        }
        @Override
        public final Iterator<TileGrid> getAll() {
            return tileGrids.iterator();
        }
        @Override
        public final void deleteComponent( int id ) {
            deleteTileGrid( id );
        }
        @Override
        public final void deleteComponent( String name ) {
            deleteTileGrid( getTileGrid( name ).index() );
        }
        @Override
        public final TileGrid getComponent( String name ) {
            return getTileGrid( name );
        }
    }
    
    private final class TileGridRendererBuilderAdapter extends SystemBuilderAdapter<TileGridRenderer> {
        public TileGridRendererBuilderAdapter( TileGridSystem system ) {
            super( system, new TileGridRendererBuilder() );
        }
        @Override
        public final SystemComponentKey<TileGridRenderer> componentTypeKey() {
            return TileGridRenderer.TYPE_KEY;
        }
        @Override
        public final TileGridRenderer getComponent( int id ) {
            return getRenderer( id );
        }
        @Override
        public final void deleteComponent( int id ) {
            deleteRenderer( id );
        }
        @Override
        public final Iterator<TileGridRenderer> getAll() {
            return renderer.iterator();
        }
        
        @Override
        public final void deleteComponent( String name ) {
            deleteRenderer( getRendererId( name ) );
        }
        @Override
        public final TileGridRenderer getComponent( String name ) {
            return getRenderer( getRendererId( name ) );
        }
    }

}
