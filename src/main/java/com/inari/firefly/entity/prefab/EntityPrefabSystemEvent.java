package com.inari.firefly.entity.prefab;

import com.inari.commons.event.Event;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityAttributeMap;

public final class EntityPrefabSystemEvent extends Event<EntityPrefabSystem> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( EntityPrefabSystemEvent.class );

    public enum Type {
        CREATE_ENTITY,
        CACHE_PREFABS,
        REBUILD_ENTITY
    }

    public final Type type;
    public final int prefabId;
    public final EntityAttributeMap attributes = new EntityAttributeMap();
    public final int entityId;
    public final int number;
    public final boolean activation;

    protected EntityPrefabSystemEvent( Type type, int prefabId, int entityId, int number, boolean activation ) {
        super( TYPE_KEY );
        this.type = type;
        this.prefabId = prefabId;
        this.entityId = entityId;
        this.number = number;
        this.activation = activation;
    }

    public final AttributeMap getAttributes() {
        return attributes;
    }

    @Override
    public final void notify( EntityPrefabSystem listener ) {
        listener.onPrefabAction( this );
    }
}
