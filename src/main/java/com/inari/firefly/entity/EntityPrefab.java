package com.inari.firefly.entity;

import com.inari.firefly.component.NamedIndexedComponent;

public final class EntityPrefab extends NamedIndexedComponent {

    protected EntityPrefab( int id ) {
        super( id );
    }
    
    @Override
    public final Class<EntityPrefab> getComponentType() {
        return EntityPrefab.class;
    }
    
    @Override
    public final Class<EntityPrefab> indexedObjectType() {
        return EntityPrefab.class;
    }

}
