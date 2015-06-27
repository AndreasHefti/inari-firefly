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
package com.inari.firefly.component.build;

import java.lang.reflect.Constructor;

import com.inari.firefly.FFContext;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.ComponentAttributeMap;

public abstract class BaseComponentBuilder<C> implements ComponentBuilder<C>{
    
    protected final AttributeMap attributes;
    private final ComponentBuilderFactory componentFactory;
    
    protected BaseComponentBuilder( ComponentBuilderFactory componentFactory ) {
            this.attributes = new ComponentAttributeMap();
            this.componentFactory = componentFactory;
        }
    
    protected BaseComponentBuilder( 
        ComponentBuilderFactory componentFactory,
        AttributeMap attributes
    ) {
        this.attributes = attributes;
        this.componentFactory = componentFactory;
    }
    
    @Override
    public final ComponentBuilder<C> clear() {
        attributes.clear();
        return this;
    }

    @Override
    public final BaseComponentBuilder<C> setAttributes( AttributeMap attributes ) {
        attributes.putAll( attributes );
        return this;
    }

    @Override
    public final AttributeMap getAttributes() {
        return attributes;
    }

    @Override
    public final ComponentBuilder<C> setAttribute( AttributeKey<?> key, Object value ) {
        attributes.putUntyped( key, value );
        return this;
    }

    @Override
    public C build() {
        return build( getId() );
    }

    @Override
    public final ComponentBuilder<C> buildAndNext() {
        build( getId() );
        return this;
    }

    @Override
    public final <CC> ComponentBuilder<CC> buildAndNext( Class<CC> componentType ) {
        build( getId() );
        return componentFactory.getComponentBuilder( componentType );
    }

    @Override
    public final ComponentBuilder<C> buildAndNext( int componentId ) {
        build( componentId );
        return this;
    }

    @Override
    public final <CC> ComponentBuilder<CC> buildAndNext( int componentId, Class<CC> componentType ) {
        build( componentId );
        return componentFactory.getComponentBuilder( componentType );
    }
    
    protected C getInstance( int componentId ) {
        return getInstance( null, componentId );
    }
    
    protected C getInstance( FFContext context, Integer componentId ) {
        try {
            String className = attributes.getValue( Component.INSTANCE_TYPE_NAME );
            if ( className == null ) {
                throw new ComponentCreationException( "Missing mandatory attribute " + Component.INSTANCE_TYPE_NAME + " for StateChangeCondition" );
            }
            @SuppressWarnings( "unchecked" )
            Class<C> typeClass = (Class<C>) Class.forName( className );
            if ( componentId == null ) {
                try {
                    Constructor<C> constructor = typeClass.getDeclaredConstructor();
                    return createInstance( constructor );
                } catch ( Throwable t ) {
                    if ( context == null ) {
                        throw new ComponentCreationException( "No Component: " + className + " with default constructor found", t );
                    }
                    Constructor<C> constructor = typeClass.getDeclaredConstructor( FFContext.class );
                    return createInstance( constructor, context );
                }
            } else {
                try {
                    Constructor<C> constructor = typeClass.getDeclaredConstructor( int.class );
                    return createInstance( constructor, componentId );
                } catch ( Throwable t ) {
                    if ( context == null ) {
                        throw new ComponentCreationException( "No Component: " + className + " with default constructor found", t );
                    }
                    Constructor<C> constructor = typeClass.getDeclaredConstructor( int.class, FFContext.class );
                    return createInstance( constructor, componentId, context );
                }
            }
        } catch ( Exception e ) {
            throw new ComponentCreationException( "Unexpected Exception while trying to instantiate Component", e );
        }
    }
    
    protected C createInstance( Constructor<C> constructor, Object... paramValues ) throws Exception {
        return constructor.newInstance( paramValues );
    }
    
    private int getId() {
        int id = -1;
        if ( attributes.getComponentKey() != null && attributes.getComponentKey().getId() >= 0 ) {
            id = attributes.getComponentKey().getId(); 
        }
        return id;
    }

}
