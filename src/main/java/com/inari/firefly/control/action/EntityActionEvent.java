package com.inari.firefly.control.action;

import com.inari.commons.event.Event;
import com.inari.firefly.system.FFContext;

public final class EntityActionEvent extends Event<EntityActionEventListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( EntityActionEvent.class );
    private static final EntityActionEvent SINGLETON_EVENT = new EntityActionEvent();
    
    private int actionId;
    private int entityId;

    private EntityActionEvent() {
        super( TYPE_KEY );
    }

    protected final void notify( final EntityActionEventListener listener ) {
        listener.performAction( actionId, entityId );
    }
    
    @Override
    protected final void restore() {
        actionId = -1;
        entityId = -1;
    }
    
    // NOTE: this is not thread save
    public static FFContext notify( FFContext context, int actionId, int entityId ) {
        SINGLETON_EVENT.actionId = actionId;
        SINGLETON_EVENT.entityId = entityId;
        return context.notify( SINGLETON_EVENT );
    }
    
    public static FFContext notifyThreadSave( FFContext context, int actionId, int entityId ) {
        EntityActionEvent event = new EntityActionEvent();
        event.actionId = actionId;
        event.entityId = entityId;
        return context.notify( SINGLETON_EVENT );
    }

}
