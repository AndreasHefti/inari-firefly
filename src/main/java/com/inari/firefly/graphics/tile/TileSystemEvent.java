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
    public final void notify( TileGridSystem listener ) {
        listener.tileGridEvent( this );
        
    }

}
