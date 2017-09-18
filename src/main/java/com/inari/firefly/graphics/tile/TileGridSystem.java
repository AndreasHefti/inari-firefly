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

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.geom.Position;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.EntityActivationEvent;
import com.inari.firefly.entity.EntityActivationListener;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.view.ViewEvent;
import com.inari.firefly.graphics.view.ViewEvent.Type;
import com.inari.firefly.graphics.view.ViewEventListener;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;
import com.inari.firefly.system.component.SystemComponentMap;
import com.inari.firefly.system.component.SystemComponentMap.BuilderAdapter;
import com.inari.firefly.system.component.SystemComponentNameMap;

public final class TileGridSystem
    extends 
        ComponentSystem<TileGridSystem>
    implements
        ViewEventListener,
        EntityActivationListener {
    
    public static final FFSystemTypeKey<TileGridSystem> SYSTEM_KEY = FFSystemTypeKey.create( TileGridSystem.class ); 
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        TileGrid.TYPE_KEY
    );

    private EntitySystem entitySystem;
    
    private final SystemComponentMap<TileGrid> tileGrids;
    private final DynArray<DynArray<TileGrid>> tileGridOfViewsPerLayer;
    
    public TileGridSystem() {
        super( SYSTEM_KEY );
        tileGrids = SystemComponentNameMap.create( 
            this, TileGrid.TYPE_KEY,
            new BuilderAdapter<TileGrid>() {
                public final void finishBuild( TileGrid component ) { build( component ); }
                public final void finishDeletion( TileGrid component ) { removeTileGrid( component ); }
            }
        );
        tileGridOfViewsPerLayer = DynArray.createTyped( DynArray.class, 10, 10 );
    }
    
    @Override
    public void init( FFContext context ) {
        super.init( context );
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );

        context.registerListener( ViewEvent.TYPE_KEY, this );
        context.registerListener( EntityActivationEvent.TYPE_KEY, this );
        context.registerListener( TileSystemEvent.TYPE_KEY, this );
    }


    public final void dispose( FFContext context ) {
        context.disposeListener( ViewEvent.TYPE_KEY, this );
        context.disposeListener( EntityActivationEvent.TYPE_KEY, this );
        context.disposeListener( TileSystemEvent.TYPE_KEY, this );
        
        clearSystem();
    }

    final void removeMultiTilePosition( final int tileGridId, final int entityId, final int x, final int y ) {
        ETile tile = entitySystem.getComponent( entityId, ETile.TYPE_KEY );
        tile.getGridPositions().remove( new Position( x, y ) );
        tileGrids.map.get( tileGridId ).reset( x, y );
    }

    final void addMultiTilePosition( final int tileGridId, final int entityId, final int x, final int y ) {
        ETile tile = entitySystem.getComponent( entityId, ETile.TYPE_KEY );
        tile.getGridPositions().add( new Position( x, y ) );
        tileGrids.map.get( tileGridId ).set( entityId, x, y );
    }
    
    public final void entityActivated( int entityId, final Aspects aspects ) {
        final ETransform transform = entitySystem.getComponent( entityId, ETransform.TYPE_KEY );
        final ETile tile = entitySystem.getComponent( entityId, ETile.TYPE_KEY );
        final TileGrid tileGrid = getTileGrid( transform.getViewId(), transform.getLayerId() );
        final DynArray<Position> gridPositions = tile.getGridPositions();
        
        for ( int i = 0; i < gridPositions.capacity(); i++ ) {
            if ( !gridPositions.contains( i ) ) {
                continue;
            }

            tileGrid.set( entityId, gridPositions.get( i ) );
        }
    }
    
    public final void entityDeactivated( int entityId, final Aspects aspects ) {
        final ETransform transform = entitySystem.getComponent( entityId, ETransform.TYPE_KEY );
        final ETile tile = entitySystem.getComponent( entityId, ETile.TYPE_KEY );
        final TileGrid tileGrid = getTileGrid( transform.getViewId(), transform.getLayerId() );
        final DynArray<Position> gridPositions = tile.getGridPositions();
        
        for ( int i = 0; i < gridPositions.capacity(); i++ ) {
            if ( !gridPositions.contains( i ) ) {
                continue;
            }
            
            tileGrid.resetIfMatch( entityId, gridPositions.get( i ) );
        }
    }
    
    public final void onViewEvent( ViewEvent event ) {
        if ( event.isOfType( Type.VIEW_DELETED ) ) {
            deleteAllTileGrid( event.getView().index() );
            return;
        }
    }
    
    public final boolean match( Aspects aspects ) {
        return aspects.contains( ETile.TYPE_KEY );
    }

    public final boolean hasTileGrid( int viewId, int layerId ) {
        return getTileGrid( viewId, layerId ) != null;
    }

    public final TileGrid getTileGrid( int viewId, int layerId ) {
        if ( !tileGridOfViewsPerLayer.contains( viewId ) ) {
            return null;
        }

        return tileGridOfViewsPerLayer
            .get( viewId )
            .get( layerId );
    }
    
    public final int getTile( int viewId, int layerId, final Position position ) {
        TileGrid tileGrid = getTileGrid( viewId, layerId );
        if ( tileGrid == null ) {
            return -1;
        }
        
        return tileGrid.getTileAt( position );
    }
    
    public final int getTile( int tileGridId, final Position position ) {
        final TileGrid tileGrid = tileGrids.map.get( tileGridId );
        if ( tileGrid == null ) {
            return -1;
        }
        
        return tileGrid.getTileAt( position );
    }

    public final void deleteAllTileGrid( int viewId ) {
        if ( tileGridOfViewsPerLayer.contains( viewId ) ) {
            DynArray<TileGrid> toDelete = tileGridOfViewsPerLayer.get( viewId );
            for ( TileGrid tileGrid : toDelete ) {
                tileGrids.delete( tileGrid.index() );
            }
        }
    }
    
    public final void deleteTileGrid( int viewId, int layerId ) {
        if ( !tileGridOfViewsPerLayer.contains( viewId ) ) {
            return;
        }
        DynArray<TileGrid> tileGridsForView = tileGridOfViewsPerLayer.get( viewId );
        if ( !tileGridsForView.contains( layerId ) ) {
            return;
        }
        
        TileGrid tileGrid = tileGridsForView.get( layerId );
        tileGrids.delete( tileGrid.index() );
    }

    public final SystemComponentBuilder getTileGridBuilder() {
        return tileGrids.getBuilder();
    }
    
    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            tileGrids.getBuilderAdapter()
        );
    }

    public final void clearSystem() {
        tileGrids.clear();
        tileGridOfViewsPerLayer.clear();
    }
    
    
    private void build( TileGrid tileGrid ) {
        int viewId = tileGrid.getViewId();
        int layerId = tileGrid.getLayerId();
        
        if ( viewId < 0 ) {
            throw new FFInitException( "ViewId is mandatory for TileGrid" );
        }
        
        if ( layerId < 0 ) {
            throw new FFInitException( "LayerId is mandatory for TileGrid" );
        }
        
        if ( !tileGridOfViewsPerLayer.contains( viewId ) ) {
            tileGridOfViewsPerLayer.set( viewId, DynArray.create( TileGrid.class, 20, 10 ) );
        }
        
        tileGridOfViewsPerLayer
            .get( viewId )
            .set( layerId, tileGrid );
    }
    
    private void removeTileGrid( TileGrid component ) {
        tileGridOfViewsPerLayer
            .get( component.getViewId() )
            .remove( component.getLayerId() );
    }

}
