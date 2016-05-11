/*******************************************************************************
 * Copyright (c) 2015 - 2016, Andreas Hefti, inarisoft@yahoo.de 
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

import com.inari.commons.lang.aspect.AspectGroup;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.commons.lang.indexed.IndexedType;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.ComponentId;

public abstract class EntityComponent implements Component, IndexedType {
    
    public final static AspectGroup ASPECT_TYPE = new AspectGroup( "EntityComponentTypeKey" );
    
    public final ComponentId componentId;
    
    protected EntityComponent( EntityComponentTypeKey<?> indexedTypeKey ) {
        componentId = new ComponentId( indexedTypeKey, -1 );
    }

    public final IIndexedTypeKey indexedTypeKey() {
        return componentId.typeKey;
    }

    @Override
    public final ComponentId componentId() {
        return componentId;
    }

    public abstract void resetAttributes();
    
    public static final class EntityComponentTypeKey<C extends EntityComponent> extends IndexedTypeKey {

        EntityComponentTypeKey( Class<C> indexedType ) {
            super( indexedType );
        }

        @Override
        public final Class<EntityComponent> baseType() {
            return EntityComponent.class;
        }
        
        @Override
        public final String toString() {
            return "EntityComponentTypeKey:" + type().getSimpleName();
        }
        
        @Override
        public final AspectGroup aspectGroup() {
            return ASPECT_TYPE;
        }
        
        @SuppressWarnings( "unchecked" )
        public static final <C extends EntityComponent> EntityComponentTypeKey<C> create( Class<C> type ) {
            return Indexer.createIndexedTypeKey( EntityComponentTypeKey.class, type );
        }
    }
    
}
