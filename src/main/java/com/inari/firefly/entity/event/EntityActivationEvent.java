package com.inari.firefly.entity.event;

import com.inari.commons.event.IAspectedEvent;
import com.inari.commons.lang.aspect.Aspect;

public final class EntityActivationEvent extends IAspectedEvent<EntityActivationListener> {
    
    public static enum Type {
        ENTITY_ACTIVATED,
        ENTITY_DEACTIVATED
    }
    
    public final int entityId;
    public final Aspect aspect;
    public final Type type;

    public EntityActivationEvent( int entityId, Aspect aspect, Type type ) {
        super();
        this.entityId = entityId;
        this.aspect = aspect;
        this.type = type;
    }

    @Override
    public final Aspect getAspect() {
        return aspect;
    }

    @Override
    public final void notify( EntityActivationListener listener ) {
        listener.onEntityActivationEvent( this );
    }
}
