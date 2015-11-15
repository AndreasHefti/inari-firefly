package com.inari.firefly.action.event;

import com.inari.commons.event.Event;

public final class ActionEvent extends Event<ActionEventListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( ActionEvent.class );
    
    public int actionId;
    public int entityId;
    
    public ActionEvent( int actionId, int entityId ) {
        super( TYPE_KEY );
        this.actionId = actionId;
        this.entityId = entityId;
    }

    @Override
    public final void notify( ActionEventListener listener ) {
        listener.notifyActionEvent( this );
    }

}
