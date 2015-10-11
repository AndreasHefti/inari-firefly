package com.inari.firefly.action.event;

import com.inari.commons.event.Event;

public final class ActionEvent extends Event<ActionEventListener> {
    
    public int actionId;
    public int entityId;
    
    public ActionEvent( int actionId, int entityId ) {
        super();
        this.actionId = actionId;
        this.entityId = entityId;
    }

    @Override
    public final void notify( ActionEventListener listener ) {
        listener.notifyActionEvent( this );
    }

}
