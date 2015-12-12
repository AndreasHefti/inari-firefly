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
package com.inari.firefly.renderer.tile;

import java.util.Iterator;

import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectBitSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.entity.event.EntityActivationListener;
import com.inari.firefly.renderer.tile.TileGrid.TileIterator;
import com.inari.firefly.renderer.tile.TileGrid.TileRenderMode;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.RenderEventListener;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;
import com.inari.firefly.system.view.event.ViewEvent;
import com.inari.firefly.system.view.event.ViewEvent.Type;
import com.inari.firefly.system.view.event.ViewEventListener;

public final class TileGridSystem
    extends 
        ComponentSystem<TileGridSystem>
    implements
        ViewEventListener,
        EntityActivationListener,
        RenderEventListener {
    
    public static final FFSystemTypeKey<TileGridSystem> SYSTEM_KEY = FFSystemTypeKey.create( TileGridSystem.class ); 
    
    private static final SystemComponentKey[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        TileGrid.TYPE_KEY
    };

    private EntitySystem entitySystem;
    private TileGridRenderer renderer;
    
    private final DynArray<TileGrid> tileGrids;
    private final DynArray<DynArray<TileGrid>> tileGridOfViewsPerLayer;
    
    public TileGridSystem() {
        super( SYSTEM_KEY );
        tileGrids = new DynArray<TileGrid>();
        tileGridOfViewsPerLayer = new DynArray<DynArray<TileGrid>>();
    }
    
    @Override
    public void init( FFContext context ) {
        super.init( context );
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        
        renderer = new TileGridRenderer( context );

        context.registerListener( ViewEvent.class, this );
        context.registerListener( EntityActivationEvent.class, this );
        context.registerListener( RenderEvent.class, this );
    }

    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( ViewEvent.class, this );
        context.disposeListener( EntityActivationEvent.class, this );
        context.disposeListener( RenderEvent.class, this );
        
        clear();
    }

    @Override
    public final void onEntityActivationEvent( EntityActivationEvent event ) {
        switch ( event.eventType ) {
            case ENTITY_ACTIVATED: {
                registerEntity( event.entityId, event.aspect );
                break;
            }
            case ENTITY_DEACTIVATED: {
                unregisterEntity( event.entityId, event.aspect );
                break;
            }
        }
    }
    
    @Override
    public final void onViewEvent( ViewEvent event ) {
        if ( event.eventType == Type.VIEW_DELETED ) {
            deleteAllTileGrid( event.view.index() );
        }
    }
    
    @Override
    public void render( RenderEvent event ) {
        int viewId = event.getViewId();
        int layerId = event.getLayerId();
        TileIterator iterator = iterator( viewId, layerId, event.getClip() );
        if ( iterator == null ) {
            return;
        }
        
        switch ( getRenderMode( viewId, layerId ) ) {
            case FAST_RENDERING: {
                renderer.renderTileGrid( iterator );
                break;
            }
            case FULL_RENDERING: {
                renderer.renderTileGridAllData( iterator );
                break;
            }
            default: {}
        }
    }

    @Override
    public final boolean match( AspectBitSet aspect ) {
        return aspect.contains( ETile.TYPE_KEY );
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

    public final void deleteAllTileGrid( int viewId ) {
        if ( tileGridOfViewsPerLayer.contains( viewId ) ) {
            DynArray<TileGrid> toRemove = tileGridOfViewsPerLayer.remove( viewId );
            for ( TileGrid tileGrid : toRemove ) {
                tileGrids.remove( tileGrid.index() );
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
    }
    
    public final void deleteTileGrid( int tileGridId ) {
        if ( !tileGrids.contains( tileGridId ) ) {
            return;
        }
        
        TileGrid removed = tileGrids.get( tileGridId );
        tileGridOfViewsPerLayer.get( removed.getViewId() ).remove( removed.getLayerId() );
    };
    
    private TileRenderMode getRenderMode( int viewId, int layerId ) {
        TileGrid tileGrid = getTileGrid( viewId, layerId );
        if ( tileGrid == null ) {
            return TileRenderMode.FAST_RENDERING;
        }
        
        return tileGrid.getRenderMode();
    }
    
    private TileIterator iterator( int viewId, int layerId, Rectangle clip ) {
        TileGrid tileGrid = getTileGrid( viewId, layerId );
        if ( tileGrid == null ) {
            return null;
        }
        return tileGrid.iterator( clip );
    }
    
    private final void registerEntity( int entityId, AspectBitSet entityAspect ) {
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
    
    private final void unregisterEntity( int entityId, AspectBitSet entityAspect ) {
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

    
    public final BaseComponentBuilder getTileGridBuilder() {
        return new TileGridBuilder();
    }
    
    @Override
    public final SystemComponentKey[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
            new TileGridBuilderAdapter( this )
        };
    }

    @Override
    public final void clear() {
        tileGridOfViewsPerLayer.clear();
    }
    
    private final class TileGridBuilder extends SystemComponentBuilder {
        
        @Override
        public final SystemComponentKey systemComponentKey() {
            return TileGrid.TYPE_KEY;
        }
        
        @Override
        public int doBuild( int componentId, Class<?> subType, boolean activate ) {
            TileGrid tileGrid = new TileGrid( componentId );
            tileGrid.fromAttributes( attributes );
            int viewId = tileGrid.getViewId();
            int layerId = tileGrid.getLayerId();
            if ( viewId < 0 ) {
                viewId = 0;
            } 
            if ( layerId < 0 ) {
                layerId = 0;
            }
            
            DynArray<TileGrid> perLayer;
            if ( !tileGridOfViewsPerLayer.contains( viewId ) ) {
                perLayer = new DynArray<TileGrid>();
                tileGridOfViewsPerLayer.set( viewId, perLayer );
            } else {
                perLayer = tileGridOfViewsPerLayer.get( viewId );
            }
            perLayer.set( layerId, tileGrid );
            
            tileGrids.set( tileGrid.getId(), tileGrid );
            
            return tileGrid.getId();
        }
    }
    
    private final class TileGridBuilderAdapter extends SystemBuilderAdapter<TileGrid> {
        public TileGridBuilderAdapter( TileGridSystem system ) {
            super( system, new TileGridBuilder() );
        }
        @Override
        public SystemComponentKey componentTypeKey() {
            return TileGrid.TYPE_KEY;
        }
        @Override
        public TileGrid get( int id, Class<? extends TileGrid> subtype ) {
            return getTileGrid( id );
        }
        @Override
        public Iterator<TileGrid> getAll() {
            return tileGrids.iterator();
        }

        @Override
        public void delete( int id, Class<? extends TileGrid> subtype ) {
            deleteTileGrid( id );
        }
        
    }

}
