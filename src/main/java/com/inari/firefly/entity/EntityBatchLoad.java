package com.inari.firefly.entity;

import java.util.ArrayList;
import java.util.List;

import com.inari.firefly.Disposable;
import com.inari.firefly.FFContext;
import com.inari.firefly.Loadable;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntitySystem.EntityBuilder;

public class EntityBatchLoad implements Loadable {
    
    private final IEntitySystem entitySystem;
    private List<AttributeMap> entityAttributes = new ArrayList<AttributeMap>();

    EntityBatchLoad( IEntitySystem entitySystem ) {
        this.entitySystem = entitySystem;
    }

    @Override
    public Disposable load( FFContext context ) {
        EntityBuilder entityBuilder = entitySystem.createEntityBuilder();
        final int[] entityIds = new int[ entityAttributes.size() ];
        int index = 0;
        for ( AttributeMap entityAttributeMap : entityAttributes ) {
            entityBuilder.setAttributes( entityAttributeMap );
            Entity entity = entityBuilder.build();
            entity.setActive( true );
            entityIds[ index ] = entity.indexedId();
            index++;
        }
        
        return new Disposable() {
            @Override
            public void dispose( FFContext context ) {
                for ( int entityId : entityIds ) {
                    entitySystem.deactivate( entityId );
                }
            }
        };
    }

}
