package com.inari.firefly.entity.event;

import com.inari.commons.event.AspectedEvent;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.firefly.entity.IEntitySystem;

@Deprecated // check if this is really useful
public abstract class EntityChangeEvent extends AspectedEvent<EntityChangeListener> {
    
    public enum Type {
        COMPONENT_ADDED,
        COMPONENT_CHANGED,
        COMPONENT_REMOVED
    }
    
    public final int entityId;
    public final int componentId;
    public final Aspect aspect;
    public final Type type;
    
    public EntityChangeEvent( 
        int entityId, int componentId, Aspect aspect,
        IEntitySystem provider, Type type 
    ) {
        this.entityId = entityId;
        this.componentId = componentId;
        this.aspect = aspect;
        this.type = type;
    }

    @Override
    public Aspect getAspect() {
        return aspect;
    }

}
