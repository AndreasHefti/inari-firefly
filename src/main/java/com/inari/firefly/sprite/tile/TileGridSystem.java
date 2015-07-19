/*******************************************************************************
 * Copyright (c) 2015, Andreas Hefti, inarisoft@yahoo.de 
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
package com.inari.firefly.sprite.tile;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.IndexedAspect;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFContext;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.entity.event.EntityActivationListener;
import com.inari.firefly.sprite.tile.TileGrid.TileGridIterator;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.ViewSystem;
import com.inari.firefly.system.event.ViewEvent;
import com.inari.firefly.system.event.ViewEvent.Type;
import com.inari.firefly.system.event.ViewEventListener;

public final class TileGridSystem 
    implements 
        FFSystem,
        ComponentBuilderFactory, 
        ViewEventListener,
        EntityActivationListener {
    
    public static final int VOID_ENTITY_ID = -1;
    
    private IEventDispatcher eventDispatcher;
    private EntitySystem entitySystem;
    private ViewSystem viewSystem;
    
    private final DynArray<DynArray<TileGrid>> tileGridOfViewsPerLayer;
    private final DynArray<TileGrid> tileGridOfViews;
    
    public TileGridSystem() {
        tileGridOfViewsPerLayer = new DynArray<DynArray<TileGrid>>();
        tileGridOfViews = new DynArray<TileGrid>();
    }
    
    @Override
    public void init( FFContext context ) {
        eventDispatcher = context.get( FFContext.EVENT_DISPATCHER );
        entitySystem = context.get( FFContext.System.ENTITY_SYSTEM );
        viewSystem = context.get( FFContext.System.VIEW_SYSTEM );
        
        eventDispatcher.register( EntityActivationEvent.class, this );
        eventDispatcher.register( ViewEvent.class, this );
    }

    @Override
    public final void dispose( FFContext context ) {
        eventDispatcher.unregister( EntityActivationEvent.class, this );
        eventDispatcher.unregister( ViewEvent.class, this );
        
        tileGridOfViewsPerLayer.clear();
        tileGridOfViews.clear();
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
    public final boolean match( IndexedAspect aspect ) {
        return aspect.contains( ETile.COMPONENT_TYPE );
    }
    
    public final boolean hasTileGrid( int viewId, int layerId ) {
        return getTileGrid( viewId, layerId ) != null;
    }
    
    public final TileGrid getTileGrid( int viewId, int layerId ) {
        if ( layerId < 0 ) {
            return tileGridOfViews.get( viewId );
        }
        
        DynArray<TileGrid> tileGridsForView = tileGridOfViewsPerLayer.get( viewId );
        if ( tileGridsForView == null ) {
            return null;
        }
        return tileGridsForView.get( layerId );
    }
    
    public final void deleteAllTileGrid( int viewId ) {
        tileGridOfViews.remove( viewId );
        tileGridOfViewsPerLayer.remove( viewId );
    }
    
    public final void deleteTileGrid( int viewId, int layerId ) {
        if ( layerId < 0 ) {
            tileGridOfViews.remove( viewId );
            return;
        }
        
        DynArray<TileGrid> tileGridsForView = tileGridOfViewsPerLayer.get( viewId );
        if ( tileGridsForView == null ) {
            return;
        }
        tileGridsForView.remove( layerId );
    }
    
    public final TileGridIterator iterator( int viewId, int layerId, Rectangle clip ) {
        TileGrid tileGrid = getTileGrid( viewId, layerId );
        if ( tileGrid == null ) {
            return null;
        }
        return tileGrid.iterator( clip );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public final <C> ComponentBuilder<C> getComponentBuilder( Class<C> type ) {
        if ( type != TileGrid.class ) {
            throw new IllegalArgumentException( "Unsupported IComponent type for this IComponentBuilderFactory: " + type );
        }
        
        return (ComponentBuilder<C>) getTileGridBuilder();
    }

    
    public final BaseComponentBuilder<TileGrid> getTileGridBuilder() {
        return new BaseComponentBuilder<TileGrid>( this ) {
            @Override
            public TileGrid build( int componentId ) {
                TileGrid tileGrid = new TileGrid();
                tileGrid.fromAttributes( attributes );
                int viewId = tileGrid.getViewId();
                int layerId = tileGrid.getLayerId();
                if ( viewId < 0 ) {
                    throw new ComponentCreationException( "Missing viewId for TileGrid component. TileGrid must have a viewId" );
                } 
                
                if ( viewSystem.getView( viewId ) == null ) {
                    throw new ComponentCreationException( "The viewId: " + viewId + " doesn't exist as a View in ViewSystem. Create the view first" );
                }
                
                if ( layerId >= 0 ) {
                    if ( !viewSystem.isLayeringEnabled( viewId ) ) {
                        throw new ComponentCreationException( "Layering is not enabled for the View: " + viewId );
                    }
                    
                    if ( !viewSystem.hasLayer( viewId, layerId ) ) {
                        throw new ComponentCreationException( "The Layer with id: " + layerId + " doesn't exists. Create the Layer first." );
                    }
                    
                    DynArray<TileGrid> perLayer = tileGridOfViewsPerLayer.get( viewId );
                    if ( perLayer == null ) {
                        perLayer = new DynArray<TileGrid>();
                        tileGridOfViewsPerLayer.set( viewId, perLayer );
                    }
                    perLayer.set( layerId, tileGrid );
                    return tileGrid;
                } 
                
                tileGridOfViews.set( viewId, tileGrid );
                return tileGrid;
            }
        };
    }
    
    
    private final void registerEntity( int entityId, IndexedAspect entityAspect ) {
        ETile tile = entitySystem.getComponent( entityId, ETile.COMPONENT_TYPE );
        TileGrid tileGrid = getTileGrid( tile.getViewId(), tile.getLayerId() );
        if ( tile.isMultiPosition() ) {
            Position gridPosition = tile.getGridPosition();
            tileGrid.set( entityId, gridPosition.x, gridPosition.y );
        } else {
            for ( Position gridPosition : tile.getGridPositions() ) {
                tileGrid.set( entityId, gridPosition.x, gridPosition.y );
            }
        }
    }
    
    private final void unregisterEntity( int entityId, IndexedAspect entityAspect ) {
        ETile tile = entitySystem.getComponent( entityId, ETile.COMPONENT_TYPE );
        TileGrid tileGrid = getTileGrid( tile.getViewId(), tile.getLayerId() );
        if ( tile.isMultiPosition() ) {
            Position gridPosition = tile.getGridPosition();
            tileGrid.reset( gridPosition.x, gridPosition.y );
        } else {
            for ( Position gridPosition : tile.getGridPositions() ) {
                tileGrid.reset( gridPosition.x, gridPosition.y );
            }
        }
    }

    
}
