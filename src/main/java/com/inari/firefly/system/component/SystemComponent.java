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
package com.inari.firefly.system.component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.lang.indexed.BaseIndexedObject;
import com.inari.commons.lang.indexed.IndexedObject;
import com.inari.commons.lang.indexed.IndexedType;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.component.NamedComponent;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public abstract class SystemComponent extends BaseIndexedObject implements IndexedType, NamedComponent {

    public static final AttributeKey<String> NAME = new AttributeKey<String>( "name", String.class, SystemComponent.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        NAME
    };
    
    private final ComponentKey componentKey;
    protected String name;
    
    protected SystemComponent( int id ) {
        super( id );
        componentKey = new ComponentKey( indexedTypeKey(), getId() );
        name = null;
    }
    
    public final int getId() {
        return index;
    }

    @Override
    public final ComponentKey componentKey() {
        return componentKey;
    }

    @Override
    public final Class<? extends IndexedObject> indexedObjectType() {
        return indexedTypeKey().type();
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final void setName( String name ) {
        this.name = name;
    }

    @Override
    public Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public void fromAttributes( AttributeMap attributes ) {
        name = attributes.getValue( NAME, name );
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
        attributes.put( NAME, name );
    }
    
    
    
    public static final class SystemComponentKey<C extends SystemComponent> extends IndexedTypeKey {

        SystemComponentKey( Class<C> indexedType ) {
            super( indexedType );
        }

        @Override
        protected final Class<SystemComponent> baseIndexedType() {
            return SystemComponent.class;
        }

        @Override
        public String toString() {
            return "SystemComponent:" + type().getSimpleName() + "[" + index + "]";
        }

        @SuppressWarnings( "unchecked" )
        public static final <T extends SystemComponent> SystemComponentKey<T> create( Class<T> type ) {
            return Indexer.getIndexedTypeKey( SystemComponentKey.class, type );
        }
    }

}
