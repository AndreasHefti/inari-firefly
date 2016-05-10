package com.inari.firefly.physics.collision;

import com.inari.commons.event.Event;
import com.inari.commons.lang.list.IntBag;

public final class ContactEvent extends Event<ContactEventListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( ContactEvent.class );
    
    public enum Type {
        REGISTER_CONTACT,
        DELETE_CONTACT,
        CLEAR_ALL
    }
    
    Type type;
    int entityId;
    IntBag contacts;
    Collisions collisions;

    ContactEvent() {
        super( TYPE_KEY );
        // TODO Auto-generated constructor stub
    }

    @Override
    protected final void notify( ContactEventListener listener ) {
        listener.onContact( this );
    }

}
