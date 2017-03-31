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

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.aspect.AspectGroup;
import com.inari.commons.lang.indexed.BaseIndexedObject;
import com.inari.commons.lang.indexed.IndexedObject;
import com.inari.commons.lang.indexed.IndexedType;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.FFInitException;
import com.inari.firefly.component.ComponentId;
import com.inari.firefly.component.NamedComponent;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFContext;

public abstract class SystemComponent extends BaseIndexedObject implements IndexedType, NamedComponent {
    
    public static final AspectGroup ASPECT_GROUP = new AspectGroup( "SystemComponent" );

    public static final AttributeKey<String> NAME = AttributeKey.createString( "name", SystemComponent.class );
    private static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet( NAME );

    protected FFContext context;
    private ComponentId componentId;
    
    private String name;
    
    protected SystemComponent( int index ) {
        super( index );
        componentId = new ComponentId( indexedTypeKey(), index() );
    }
    
    protected final void injectContext( FFContext context ) {
        this.context = context;
    }
    
    protected void init() throws FFInitException {
        // NOOP for default
    }

    @Override
    public final ComponentId componentId() {
        return componentId;
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
        return ATTRIBUTE_KEYS;
    }

    @Override
    public void fromAttributes( AttributeMap attributes ) {
        setName( attributes.getValue( NAME, getName() ) );
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
        attributes.put( NAME, getName() );
    }
    
    public static final class SystemComponentKey<C extends SystemComponent> extends IndexedTypeKey {
        
        SystemComponentKey( Class<C> indexedType ) {
            super( indexedType );
        }
        
        @SuppressWarnings( "unchecked" )
        public final Class<? extends C> baseComponentType() {
            return (Class<? extends C>) indexedType;
        }

        @Override
        public final Class<SystemComponent> baseType() {
            return SystemComponent.class;
        }

        @Override
        public final String toString() {
            return "SystemComponent:" + type().getSimpleName();
        }
        
        @Override
        public final AspectGroup aspectGroup() {
            return ASPECT_GROUP;
        }

        @SuppressWarnings( "unchecked" )
        public static final <T extends SystemComponent> SystemComponentKey<T> create( Class<T> type ) {
            return Indexer.createIndexedTypeKey( SystemComponentKey.class, type );
        }
    }

}
