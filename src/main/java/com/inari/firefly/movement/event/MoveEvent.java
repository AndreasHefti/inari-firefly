package com.inari.firefly.movement.event;

import com.inari.commons.event.IEvent;
import com.inari.commons.lang.list.IntBag;

public final class MoveEvent extends IEvent<MoveEventListener> {
    
    public final IntBag entityIds = new IntBag( 100, -1 );
    
    public void add( int entityId ) {
        entityIds.add( entityId );
    }

    @Override
    public final void notify( MoveEventListener listener ) {
        listener.onMoveEvent( this );
    }

}
