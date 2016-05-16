package com.inari.firefly.physics.collision;

import com.inari.commons.event.Event;

public final class ContactEvent extends Event<ContactEventListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( ContactEvent.class );

    int entityId;

    ContactEvent() {
        super( TYPE_KEY );
    }

    @Override
    protected final void notify( ContactEventListener listener ) {
        listener.onContact( this );
    }

}
