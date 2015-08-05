package com.inari.firefly.entity.event;

public final class CachePrefabsEvent extends EntityPrefabActionEvent {

    public CachePrefabsEvent( int prefabId, int number ) {
        super( Type.CACHE_PREFABS, prefabId, -1, number, false );
    }
}
