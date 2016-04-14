package com.inari.firefly.control.action;

import com.inari.commons.event.Event;

public final class ActionSystemEvent extends Event<ActionSystem> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( ActionSystemEvent.class );
    
    public int actionId;
    public int entityId;
    
    public ActionSystemEvent( int actionId, int entityId ) {
        super( TYPE_KEY );
        this.actionId = actionId;
        this.entityId = entityId;
    }

    @Override
    protected final void notify( ActionSystem listener ) {
        listener.performAction( actionId, entityId );
    }

}
