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
import java.lang.reflect.InvocationTargetException;

import com.inari.commons.StringUtils;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.NamedComponent;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.ComponentAttributeMap;
import com.inari.firefly.state.event.StateChangeEvent;
import com.inari.firefly.state.event.StateChangeListener;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.FFInitException;

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
    public final ComponentBuilder<C> set( AttributeKey<?> key, Object value ) {
        attributes.putUntyped( key, value );
        return this;
    }
    
    @Override
    public final ComponentBuilder<C> set( AttributeKey<Float> key, float value ) {
        attributes.put( key, value );
        return this;
    }
    
    @Override
    public final ComponentBuilder<C> set( AttributeKey<Integer> key, int value ) {
        attributes.put( key, value );
        return this;
    }
    
    @Override
    public final ComponentBuilder<C> set( AttributeKey<Long> key, long value ) {
        attributes.put( key, value );
        return this;
    }
    
    @Override
    public final ComponentBuilder<C> set( AttributeKey<Double> key, double value ) {
        attributes.put( key, value );
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
    
    @SuppressWarnings( "unchecked" )
    protected C getInstance( FFContext context, Integer componentId ) {
        String className = attributes.getValue( Component.INSTANCE_TYPE_NAME );
        if ( className == null ) {
            throw new ComponentCreationException( "Missing mandatory attribute " + Component.INSTANCE_TYPE_NAME + " for StateChangeCondition" );
        }
        
        Class<C> typeClass = null;
        try {
            typeClass = (Class<C>) Class.forName( className );
        } catch ( Exception e ) {
            throw new ComponentCreationException( "Failed to getComponent class for name: " + className );
        }
        if ( componentId == null ) {
            try {
                Constructor<C> constructor = typeClass.getDeclaredConstructor();
                return createInstance( constructor );
            } catch ( InvocationTargetException ite ) {
                throw new ComponentCreationException( "Error while constructing: " + typeClass, ite.getCause() );
            } catch ( Throwable t ) {
                if ( context == null ) {
                    throw new ComponentCreationException( "No Component: " + className + " with default constructor found", t );
                }
                try {
                    Constructor<C> constructor = typeClass.getDeclaredConstructor( FFContext.class );
                    return createInstance( constructor, context );
                } catch ( InvocationTargetException ite ) {
                    throw new ComponentCreationException( "Error while constructing: " + typeClass, ite.getCause() );
                } catch ( Throwable tt ) { 
                    throw new ComponentCreationException( "Error:", tt );
                }
            }
        } else {
            try {
                Constructor<C> constructor = typeClass.getDeclaredConstructor( int.class );
                return createInstance( constructor, componentId );
            } catch ( InvocationTargetException ite ) {
                throw new ComponentCreationException( "Error while constructing: " + typeClass, ite.getCause() );
            } catch ( Throwable t ) {
                if ( context == null ) {
                    throw new ComponentCreationException( "No Component: " + className + " with default constructor found", t );
                }
                try {
                    Constructor<C> constructor = typeClass.getDeclaredConstructor( int.class, FFContext.class );
                    return createInstance( constructor, componentId, context );
                } catch ( InvocationTargetException ite ) {
                    throw new ComponentCreationException( "Error while constructing: " + typeClass, ite.getCause() );
                } catch ( Throwable tt ) { 
                    throw new ComponentCreationException( "Error:", tt );
                }
            }
        }
    }
    
    protected C createInstance( Constructor<C> constructor, Object... paramValues ) throws Exception {
        boolean hasAccess = constructor.isAccessible();
        if ( !hasAccess ) {
            constructor.setAccessible( true );
        }
        
        C newInstance = constructor.newInstance( paramValues );
        
        if ( !hasAccess ) {
            constructor.setAccessible( false );
        }
        return newInstance;
    }
    
    protected final void checkName( NamedComponent component ) {
        if ( StringUtils.isBlank( component.getName() ) ) {
            throw new FFInitException( "Name is mandatory for component: " + component );
        }
    }
    
    protected final void postInit( Component component, FFContext context ) {
        if ( component instanceof FFContextInitiable ) {
            ( (FFContextInitiable) component ).init( context );
        }
        if ( component instanceof StateChangeListener ) {
            context.getComponent( FFContext.EVENT_DISPATCHER ).register( StateChangeEvent.class, (StateChangeListener) component );
        }
    }
    
    private int getId() {
        int id = -1;
        if ( attributes.getComponentKey() != null && attributes.getComponentKey().getId() >= 0 ) {
            id = attributes.getComponentKey().getId(); 
        }
        return id;
    }

}
