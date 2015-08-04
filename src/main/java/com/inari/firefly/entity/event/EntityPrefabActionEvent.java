package com.inari.firefly.entity.event;

import com.inari.commons.event.Event;
import com.inari.firefly.entity.EntityAttributeMap;

public class EntityPrefabActionEvent extends Event<EntityPrefabActionListener> {

    public enum Type {
        CREATE_ENTITY,
        CACHE_PREFABS
    }

    public final Type type;
    public final int prefabId;
    public final EntityAttributeMap attributes = new EntityAttributeMap();
    public int number;

    public EntityPrefabActionEvent( Type type, int prefabId ) {
        this.type = type;
        this.prefabId = prefabId;
    }

    public EntityPrefabActionEvent( int number, int prefabId, Type type ) {
        this.number = number;
        this.prefabId = prefabId;
        this.type = type;
    }

    @Override
    public final void notify( EntityPrefabActionListener listener ) {
        listener.onPrefabAction( this );
    }
}
