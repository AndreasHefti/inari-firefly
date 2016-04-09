package com.inari.firefly.graphics.tile;

import com.inari.commons.event.Event;
import com.inari.commons.geom.Position;

public final class TileSystemEvent extends Event<TileGridSystem> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( TileSystemEvent.class );
    
    public static enum Type {
        MULTIPOSITION_ADD,
        MULTIPOSITION_REMOVE
    }
    
    public final Type type;
    public final int tileGridId;
    public final int entityId;
    public final Position gridPosition;
    
    public TileSystemEvent( Type type, int tileGridId, int entityId, Position gridPosition ) {
        super( TYPE_KEY );
        this.type = type;
        this.tileGridId = tileGridId;
        this.entityId = entityId;
        this.gridPosition = gridPosition;
    }

    @Override
    protected final void notify( final TileGridSystem listener ) {
        switch ( type ) {
            case MULTIPOSITION_ADD: {
                listener.addMultiTilePosition( 
                    tileGridId, entityId,
                    gridPosition.x, gridPosition.y 
                );
                break;
            }
            case MULTIPOSITION_REMOVE: {
                listener.removeMultiTilePosition( 
                    tileGridId, entityId,
                    gridPosition.x, gridPosition.y 
                );
                break;
            }
        }
    }

}
