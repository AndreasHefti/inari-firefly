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
package com.inari.firefly.component.build;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.inari.commons.StringUtils;
import com.inari.commons.lang.Named;
import com.inari.commons.lang.indexed.Indexed;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.FFInitException;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.NamedComponent;
import com.inari.firefly.component.attr.Attribute;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.SystemComponent;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public abstract class BaseComponentBuilder<C extends Component> implements ComponentBuilder {
    
    protected final FFContext context;
    protected final AttributeMap attributes;
    protected final Class<? extends C> componentType;

    protected BaseComponentBuilder( FFContext context, AttributeMap attributes, Class<? extends C> componentType ) {
        this.context = context;
        this.attributes = attributes;
        this.componentType = componentType;
    }
    
    public final ComponentBuilder clear() {
        attributes.clear();
        return this;
    }

    public final BaseComponentBuilder<C> setAttributes( AttributeMap attributes ) {
        attributes.putAll( attributes );
        return this;
    }

    public final AttributeMap getAttributes() {
        return attributes;
    }

    public final ComponentBuilder set( AttributeKey<Float> key, float value ) {
        attributes.put( key, value );
        return this;
    }
    
    public final ComponentBuilder set( AttributeKey<Integer> key, int value ) {
        attributes.put( key, value );
        return this;
    }
    
    public final ComponentBuilder set( AttributeKey<Integer> key, Indexed indexed ) {
        return set( key, indexed.index() );
    }
    
    public final ComponentBuilder set( AttributeKey<Long> key, long value ) {
        attributes.put( key, value );
        return this;
    }
    
    public final ComponentBuilder set( AttributeKey<Double> key, double value ) {
        attributes.put( key, value );
        return this;
    }
    
    public final ComponentBuilder set( AttributeKey<String> key, Named named ) {
        return set( key, named.name() );
    }
    
    public final <T> ComponentBuilder set( AttributeKey<T> key, T value ) {
        attributes.putUntyped( key, value );
        return this;
    }

    @SuppressWarnings( "unchecked" )
    public final <T, V extends T> ComponentBuilder set( AttributeKey<DynArray<T>> key, V value, int index ) {
        DynArray<T> array;
        if ( ! attributes.contains( key ) ) {
            array = DynArray.create( (Class<T>) key.typedValueType() ); 
        } else {
            array = attributes.getValue( key );
        }
        
        array.set( index, value );
        return this;
    }

    public final ComponentBuilder add( AttributeKey<IntBag> key, int value ) {
        if ( ! attributes.contains( key ) ) {
            attributes.put( key, new IntBag( 1, -1 ) );
        }
        
        IntBag intBag = attributes.getValue( key );
        intBag.add( value );
        
        return this;
    }
    
    public final ComponentBuilder add( AttributeKey<IntBag> key, Indexed indexed ) {
        return add( key, indexed.index() );
    }

    @SuppressWarnings( "unchecked" )
    public final <T, V extends T> ComponentBuilder add( AttributeKey<DynArray<T>> key, V value ) {
        if ( value == null ) {
            return this;
        }
        
        return createAndAdd( key, value, (Class<T>) key.typedValueType() );
    }
    
    private <T, V extends T> ComponentBuilder createAndAdd( AttributeKey<DynArray<T>> key, V value, Class<T> type ) {
        if ( value == null ) {
            return this;
        }
        
        DynArray<T> list;
        if ( ! attributes.contains( key ) ) {
            list = DynArray.create( type, 10, 10 );
            attributes.put( key, list );
        } else {
            list = attributes.getValue( key );
        }
        
        list.add( (T) value );
        return this;
    }
    
    
    
    @SuppressWarnings( "unchecked" )
    @Override
    public final <T, V extends T> ComponentBuilder add( AttributeKey<DynArray<T>> key, V[] values ) {
        if ( values == null ) {
            return this;
        }
        
        return createAndAdd( key, values, (Class<T>) key.typedValueType() );
    }
    
    private <T> ComponentBuilder createAndAdd( AttributeKey<DynArray<T>> key, T[] values, Class<T> type ) {
        if ( values == null ) {
            return this;
        }
        
        DynArray<T> list;
        if ( !attributes.contains( key ) ) {
            list = DynArray.create( type, 10, 10 );
            attributes.put( key, list );
        } else {
            list = attributes.getValue( key );
        }
        
        for ( int i = 0; i < values.length; i++ ) {
            list.add( (T) values[ i ] );
        }
        return this;
    }
    
    @SuppressWarnings( "unchecked" )
    public final <T, V extends T> ComponentBuilder add( AttributeKey<DynArray<T>> key, DynArray<V> values ) {
        return createAndAdd( key, values.getArray(), (Class<T>) key.typedValueType() );
    }
    
    public final ComponentBuilder add( AttributeKey<DynArray<String>> key, Named named ) {
        return add( key, named.name() );
    }

    public final ComponentBuilder add( Attribute<?>... attributes ) {
        for ( Attribute<?> attribute : attributes ) {
            this.attributes.putUntyped( attribute.getKey(), attribute.getValue() );
        }
        return this;
    }
    
    public final FFContext buildToContext() {
        build();
        return context;
    }

    public final ComponentBuilder buildAndNext() {
        build();
        return this;
    }
    
     @Override
    public final <CC extends SystemComponent> ComponentBuilder buildAndNext( SystemComponentKey<CC> key ) {
        build();
        return context.getComponentBuilder( key );
    }
    
    @Override
    public final <CC extends SystemComponent> ComponentBuilder buildAndNext( SystemComponentKey<CC> key, Class<? extends CC> type ) {
        build();
        return context.getComponentBuilder( key, type );
    }

    public final ComponentBuilder buildAndNext( int componentId ) {
        build( componentId );
        return this;
    }
    
    public final FFContext activateToContext() {
        activate();
        return context;
    }

    public final ComponentBuilder activateAndNext() {
        activate();
        return this;
    }
    
    @Override
    public final <CC extends SystemComponent> ComponentBuilder activateAndNext( SystemComponentKey<CC> key ) {
        activate();
        return context.getComponentBuilder( key );
    }
    
    @Override
    public final <CC extends SystemComponent> ComponentBuilder activateAndNext( SystemComponentKey<CC> key, Class<? extends CC> type ) {
        activate();
        return context.getComponentBuilder( key, type );
    }

    public final ComponentBuilder activateAndNext( int componentId ) {
        activate( componentId );
        return this;
    }

    protected abstract int doBuild( int componentId, Class<?> componentType, boolean activate );

    protected <CC extends C> CC getInstance( Integer componentId, Class<CC> typeClass ) {
        if ( componentId == null ) {
            try {
                Constructor<CC> constructor = typeClass.getDeclaredConstructor();
                boolean accessible = constructor.isAccessible();
                if ( !accessible ) {
                    constructor.setAccessible( true );
                }
                CC instance = createInstance( constructor );
                constructor.setAccessible( accessible );
                return instance;
            } catch ( InvocationTargetException ite ) {
                throw new ComponentCreationException( "Error while constructing: " + typeClass, ite.getCause() );
            } catch ( Throwable t ) {
                throw new ComponentCreationException( "No Component: " + typeClass.getName() + " with default constructor found", t );
            }
        } else {
            try {
                Constructor<CC> constructor = typeClass.getDeclaredConstructor( int.class );
                boolean accessible = constructor.isAccessible();
                if ( !accessible ) {
                    constructor.setAccessible( true );
                }
                CC instance = createInstance( constructor, componentId );
                constructor.setAccessible( accessible );
                return instance;
            } catch ( InvocationTargetException ite ) {
                throw new ComponentCreationException( "Error while constructing: " + typeClass, ite.getCause() );
            } catch ( Throwable t ) {
                throw new ComponentCreationException( "No Component: " + typeClass.getName() + " with default constructor found", t );
            }
        }
    }
    
    protected <CC extends C> CC createInstance( Constructor<CC> constructor, Object... paramValues ) throws Exception {
        boolean hasAccess = constructor.isAccessible();
        if ( !hasAccess ) {
            constructor.setAccessible( true );
        }
        
        CC newInstance = constructor.newInstance( paramValues );
        
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

    protected int getId() {
        int id = -1;
        if ( attributes.getComponentId() != null && attributes.getComponentId().indexId >= 0 ) {
            id = attributes.getComponentId().indexId; 
        }
        return id;
    }

}
