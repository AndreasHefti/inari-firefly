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
import com.inari.commons.lang.aspect.IAspects;
import com.inari.commons.lang.list.DynArrayRO;
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
import com.inari.firefly.system.component.SystemComponentViewLayerMap;

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
    
    final SystemComponentViewLayerMap<TileGrid> tileGrids;
    
    public TileGridSystem() {
        super( SYSTEM_KEY );
        tileGrids = new SystemComponentViewLayerMap<>( this, TileGrid.TYPE_KEY );
    }
    
    @Override
    public void init( FFContext context ) {
        super.init( context );
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );

        context.registerListener( ViewEvent.TYPE_KEY, this );
        context.registerListener( EntityActivationEvent.TYPE_KEY, this );
        context.registerListener( TileSystemEvent.TYPE_KEY, this );
    }
    
    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            tileGrids.getBuilderAdapter()
        );
    }

    final void removeMultiTilePosition( final int tileGridId, final int entityId, final int x, final int y ) {
        entitySystem
            .getComponent( entityId, ETile.TYPE_KEY )
            .removeGridPosition( x, y );
        tileGrids.map.get( tileGridId ).reset( x, y );
    }

    final void addMultiTilePosition( final int tileGridId, final int entityId, final int x, final int y ) {
        entitySystem
            .getComponent( entityId, ETile.TYPE_KEY )
            .addGridPosition( x, y );
        tileGrids.map.get( tileGridId ).set( entityId, x, y );
    }
    
    public final void entityActivated( int entityId, final IAspects aspects ) {
        final ETransform transform = entitySystem.getComponent( entityId, ETransform.TYPE_KEY );
        final ETile tile = entitySystem.getComponent( entityId, ETile.TYPE_KEY );
        final TileGrid tileGrid = getTileGrid( transform.getViewId(), transform.getLayerId() );
        final DynArrayRO<Position> gridPositions = tile.getGridPositions();
        
        for ( int i = 0; i < gridPositions.capacity(); i++ ) {
            if ( !gridPositions.contains( i ) ) {
                continue;
            }

            tileGrid.set( entityId, gridPositions.get( i ) );
        }
    }
    
    public final void entityDeactivated( int entityId, final IAspects aspects ) {
        final ETransform transform = entitySystem.getComponent( entityId, ETransform.TYPE_KEY );
        final ETile tile = entitySystem.getComponent( entityId, ETile.TYPE_KEY );
        final TileGrid tileGrid = getTileGrid( transform.getViewId(), transform.getLayerId() );
        final DynArrayRO<Position> gridPositions = tile.getGridPositions();
        
        for ( int i = 0; i < gridPositions.capacity(); i++ ) {
            if ( !gridPositions.contains( i ) ) {
                continue;
            }
            
            tileGrid.resetIfMatch( entityId, gridPositions.get( i ) );
        }
    }
    
    public final void onViewEvent( ViewEvent event ) {
        if ( event.isOfType( Type.VIEW_DELETED ) ) {
            tileGrids.deleteAll( event.getView().index() );
            return;
        }
    }
    
    public final boolean match( IAspects aspects ) {
        return aspects.contains( ETile.TYPE_KEY );
    }

    public final boolean hasTileGrid( int viewId, int layerId ) {
        return getTileGrid( viewId, layerId ) != null;
    }

    public final TileGrid getTileGrid( int viewId, int layerId ) {
        return tileGrids.get( viewId, layerId );
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
    
    public final void dispose( FFContext context ) {
        context.disposeListener( ViewEvent.TYPE_KEY, this );
        context.disposeListener( EntityActivationEvent.TYPE_KEY, this );
        context.disposeListener( TileSystemEvent.TYPE_KEY, this );
        
        clearSystem();
    }

    public final void clearSystem() {
        tileGrids.clear();
    }

}
