package com.inari.firefly.entity.event;

public final class RebuildEntityEvent extends EntityPrefabActionEvent {

    public RebuildEntityEvent( int prefabId, int entityId ) {
        super( Type.REBUILD_ENTITY, prefabId, entityId, -1, true );
    }

    public RebuildEntityEvent( int prefabId, int entityId, boolean activation ) {
        super( Type.REBUILD_ENTITY, prefabId, entityId, -1, activation );
    }
}
