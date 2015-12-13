package com.inari.firefly.entity;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.system.component.SystemComponent;

public final class EntityPrefab extends SystemComponent {
    
    public static final SystemComponentKey<EntityPrefab> TYPE_KEY = SystemComponentKey.create( EntityPrefab.class );

    protected EntityPrefab( int id ) {
        super( id );
    }
    
    @Override
    public final Class<EntityPrefab> componentType() {
        return EntityPrefab.class;
    }
    
    @Override
    public final Class<EntityPrefab> indexedObjectType() {
        return EntityPrefab.class;
    }

    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

}
