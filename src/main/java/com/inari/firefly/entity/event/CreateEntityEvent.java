package com.inari.firefly.entity.event;

import com.inari.firefly.component.attr.AttributeMap;

public final class CreateEntityEvent extends EntityPrefabActionEvent{

    public CreateEntityEvent( int prefabId ) {
        super( Type.CREATE_ENTITY, prefabId, -1, -1, false );
    }

    public CreateEntityEvent( int prefabId, AttributeMap attributes ) {
        super( Type.CREATE_ENTITY, prefabId, -1, -1, false );
        this.attributes.putAll( attributes );
    }
}
