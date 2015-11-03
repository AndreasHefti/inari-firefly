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
package com.inari.firefly.renderer.tile;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.aspect.AspectBitSet;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.entity.event.EntityActivationListener;
import com.inari.firefly.renderer.tile.TileGrid.TileGridIterator;
import com.inari.firefly.renderer.tile.TileGrid.TileRenderMode;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.view.ViewSystem;
import com.inari.firefly.system.view.event.ViewEvent;
import com.inari.firefly.system.view.event.ViewEvent.Type;
import com.inari.firefly.system.view.event.ViewEventListener;

public final class TileGridSystem 
    implements
        FFContextInitiable,
        ComponentBuilderFactory, 
        ViewEventListener,
        EntityActivationListener {
    
    private final int COMPONENT_ID_ETRANSFORM = Indexer.getIndexForType( ETransform.class, EntityComponent.class );
    private final int COMPONENT_ID_ETILE = Indexer.getIndexForType( ETile.class, EntityComponent.class );
    
    public static final TypedKey<TileGridSystem> CONTEXT_KEY = TypedKey.create( "FF_TILE_GRID_SYSTEM", TileGridSystem.class ); 
    
    public static final int VOID_ENTITY_ID = -1;
    
    private IEventDispatcher eventDispatcher;
    private EntitySystem entitySystem;
    private ViewSystem viewSystem;
    
    private final DynArray<DynArray<TileGrid>> tileGridOfViewsPerLayer;
    
    public TileGridSystem() {
        tileGridOfViewsPerLayer = new DynArray<DynArray<TileGrid>>();
    }
    
    @Override
    public void init( FFContext context ) {
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        viewSystem = context.getComponent( ViewSystem.CONTEXT_KEY );
        
        eventDispatcher.register( EntityActivationEvent.class, this );
        eventDispatcher.register( ViewEvent.class, this );
    }

    @Override
    public final void dispose( FFContext context ) {
        eventDispatcher.unregister( EntityActivationEvent.class, this );
        eventDispatcher.unregister( ViewEvent.class, this );
        
        tileGridOfViewsPerLayer.clear();
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
    public final boolean match( AspectBitSet aspect ) {
        return aspect.contains( COMPONENT_ID_ETILE );
    }
    
    public final boolean hasTileGrid( int viewId, int layerId ) {
        return getTileGrid( viewId, layerId ) != null;
    }
    
    public final TileGrid getTileGrid( String viewName ) {
        int viewId = viewSystem.getViewId( viewName );
        if ( viewId < 0 ) {
            return null;
        }
        
        return getTileGrid( viewId );
    }
    
    public final TileGrid getTileGrid( String viewName, String layerName ) {
        int viewId = viewSystem.getViewId( viewName );
        int layerId = viewSystem.getLayerId( layerName );
        if ( viewId < 0 || layerId < 0 ) {
            return null;
        }
        
        
        return getTileGrid( viewId, layerId );
    }
    
    public final TileGrid getTileGrid( int viewId ) {
        return getTileGrid( viewId, 0 );
    }
    
    public final TileGrid getTileGrid( int viewId, int layerId ) {
        if ( !tileGridOfViewsPerLayer.contains( viewId ) ) {
            return null;
        }
        DynArray<TileGrid> tileGridsForView = tileGridOfViewsPerLayer.get( viewId );
        return tileGridsForView.get( layerId );
    }
    
    public final TileRenderMode getRenderMode( int viewId, int layerId ) {
        TileGrid tileGrid = getTileGrid( viewId, layerId );
        if ( tileGrid == null ) {
            return TileRenderMode.FAST_RENDERING;
        }
        
        return tileGrid.getRenderMode();
    }
    
    public final void deleteAllTileGrid( int viewId ) {
        if ( tileGridOfViewsPerLayer.contains( viewId ) ) {
            tileGridOfViewsPerLayer.remove( viewId );
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
                
                if ( viewSystem.getView( viewId ) == null ) {
                    throw new ComponentCreationException( "The viewId: " + viewId + " doesn't exist as a View in ViewSystem. Create the view first" );
                }

                if ( layerId > 0 && !viewSystem.hasLayer( viewId, layerId ) ) {
                    throw new ComponentCreationException( "The Layer with id: " + layerId + " doesn't exists. Create the Layer first." );
                }
                
                DynArray<TileGrid> perLayer;
                if ( !tileGridOfViewsPerLayer.contains( viewId ) ) {
                    perLayer = new DynArray<TileGrid>();
                    tileGridOfViewsPerLayer.set( viewId, perLayer );
                } else {
                    perLayer = tileGridOfViewsPerLayer.get( viewId );
                }
                
                perLayer.set( layerId, tileGrid );
                return tileGrid;
            }
        };
    }
    
    
    private final void registerEntity( int entityId, AspectBitSet entityAspect ) {
        ETransform transform = entitySystem.getComponent( entityId, COMPONENT_ID_ETRANSFORM );
        ETile tile = entitySystem.getComponent( entityId, COMPONENT_ID_ETILE );
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
        ETransform transform = entitySystem.getComponent( entityId, COMPONENT_ID_ETRANSFORM );
        ETile tile = entitySystem.getComponent( entityId, COMPONENT_ID_ETILE );
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

}
