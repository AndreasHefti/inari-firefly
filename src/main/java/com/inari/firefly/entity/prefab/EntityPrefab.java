package com.inari.firefly.entity.prefab;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.system.component.SystemComponent;

public final class EntityPrefab extends SystemComponent {
    
    public static final SystemComponentKey<EntityPrefab> TYPE_KEY = SystemComponentKey.create( EntityPrefab.class );
    
    public static final AttributeKey<Integer> INITIAL_CREATE_NUMBER = new AttributeKey<Integer>( "createPrefab", Integer.class, EntityPrefab.class );

    protected EntityPrefab( int id ) {
        super( id );
    }

    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

}
