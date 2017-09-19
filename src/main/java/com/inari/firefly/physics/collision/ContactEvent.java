package com.inari.firefly.physics.collision;

import com.inari.commons.event.Event;
import com.inari.firefly.system.FFContext;

public final class ContactEvent extends Event<ContactEventListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( ContactEvent.class );
    private static final ContactEvent SINGLETON_EVENT = new ContactEvent();

    private int entityId;

    private ContactEvent() {
        super( TYPE_KEY );
        restore();
    }

    @Override
    protected void restore() {
        entityId = -1;
    }

    @Override
    protected final void notify( ContactEventListener listener ) {
        listener.onContact( entityId );
    }
    
    // NOTE: this is not thread save and blocking
    public final static FFContext notify( final FFContext context, int entityId ) {
        SINGLETON_EVENT.entityId = entityId;
        return context.notify( SINGLETON_EVENT );
    }

}
