package com.inari.firefly.sprite.tile;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.Vector2f;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.Disposable;
import com.inari.firefly.FFContext;
import com.inari.firefly.entity.IEntitySystem;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.entity.event.EntityActivationListener;
import com.inari.firefly.sprite.tile.TileGrid.TileGridIterator;
import com.inari.firefly.system.ILowerSystemFacade;
import com.inari.firefly.system.event.RenderEvent;
import com.inari.firefly.system.event.RenderEventListener;

public final class TileGridRenderer implements RenderEventListener, EntityActivationListener, Disposable {
    
    private final IEventDispatcher eventDispatcher;
    private final TileGridSystem tileGridSystem;
    private final ILowerSystemFacade lowerSystemFacade;
    private final IEntitySystem entityProvider;
    
    private final DynArray<ETile> tiles = new DynArray<ETile>();

    public TileGridRenderer( FFContext context ) {
        eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
        tileGridSystem = context.get( FFContext.System.TILE_GRID_SYSTEM );
        lowerSystemFacade = context.get( FFContext.System.LOWER_SYSTEM_FACADE );
        entityProvider = context.get( FFContext.System.ENTITY_SYSTEM );
        
        eventDispatcher.register( EntityActivationEvent.class, this );
    }
    
    @Override
    public void dispose( FFContext context ) {
        eventDispatcher.unregister( EntityActivationEvent.class, this );
    }

    @Override
    public final void render( RenderEvent event ) {
        TileGridIterator iterator = tileGridSystem.iterator( event.getViewId(), event.getLayerId(), event.getClip() );
        if ( iterator == null ) {
            return;
        }
        
        Vector2f actualWorldPosition  = iterator.getWorldPosition();
        while( iterator.hasNext() ) {
            ETile cTile = tiles.get( iterator.next() );
            lowerSystemFacade.renderSprite( cTile, actualWorldPosition.dx, actualWorldPosition.dy );
        }
    }

    @Override
    public final void onEntityActivationEvent( EntityActivationEvent event ) {
        switch ( event.type ) {
            case ENTITY_ACTIVATED: {
                ETile tile;
                if ( event.aspect.contains( ESingleTile.COMPONENT_TYPE ) ) {
                    tile = entityProvider.getComponent( event.entityId, ESingleTile.COMPONENT_TYPE );
                } else {
                    tile = entityProvider.getComponent( event.entityId, EMultiTile.COMPONENT_TYPE );
                }
                tiles.set( event.entityId, tile );
                break;
            }
            case ENTITY_DEACTIVATED: {
                tiles.remove( event.entityId );
                break;
            }
        }
        
    }

}
