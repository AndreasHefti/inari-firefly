/*******************************************************************************
 * Copyright (c) 2015, Andreas Hefti, inarisoft@yahoo.de 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/ 
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
