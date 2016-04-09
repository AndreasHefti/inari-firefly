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
    public final EntityAttributeMap attributes;
    public final int entityId;
    public final int number;
    public final boolean activation;

    protected EntityPrefabSystemEvent( Type type, int prefabId, int entityId, int number, boolean activation, EntityAttributeMap attributes ) {
        super( TYPE_KEY );
        this.attributes = attributes;
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
    protected final void notify( final EntityPrefabSystem listener ) {
        switch ( type ) {
            case CREATE_ENTITY: {
                listener.activateOne( prefabId, attributes );
                break;
            }
            case CACHE_PREFABS: {
                listener.cacheComponents( prefabId, number );
                break;
            }
            case REBUILD_ENTITY: {
                listener.rebuildEntity( prefabId, entityId, attributes, activation );
                break;
            }
        }
    }
}
