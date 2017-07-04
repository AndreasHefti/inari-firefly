package com.inari.firefly.control.action;

import java.util.ArrayDeque;

import com.inari.commons.event.Event;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.system.FFContext;

public final class EntityActionEvent extends Event<EntityActionEventListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( EntityActionEvent.class );
    private static final ArrayDeque<EntityActionEvent> POOL = new ArrayDeque<EntityActionEvent>( 2 );
    
    int actionId;
    int entityId;

    protected EntityActionEvent() {
        super( TYPE_KEY );
    }

    @Override
    protected final void notify( final EntityActionEventListener listener ) {
        listener.performAction( actionId, entityId );
    }
    
    @Override
    protected final void restore() {
        actionId = -1;
        entityId = -1;
        
        POOL.addLast( this );
    }
    
    static final EntityActionEvent create( String actionName, int entityId, FFContext context ) {
        return create( 
            context.getSystemComponentId( Action.TYPE_KEY, actionName ), entityId
        );
    }
    
    static final EntityActionEvent create( String actionName, String entityName, FFContext context ) {
        return create( 
            context.getSystemComponentId( Action.TYPE_KEY, actionName ),
            context.getSystem( EntitySystem.SYSTEM_KEY ).getEntityId( entityName )
        );
    }
    
    static final EntityActionEvent create( int actionId, int entityId ) {
        final EntityActionEvent result;
        if ( POOL.isEmpty() ) {
            result = new EntityActionEvent();
            POOL.addLast( result );
        } else {
            result = POOL.removeLast();
        }
        
        result.actionId = actionId;
        result.entityId = entityId;
        
        return result;
    }

}
