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
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.FFInitException;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.NamedComponent;
import com.inari.firefly.component.attr.Attribute;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public abstract class BaseComponentBuilder<C extends Component> implements ComponentBuilder {
    
    protected final AttributeMap attributes;
    protected final Class<? extends C> componentType;
    
    protected BaseComponentBuilder( AttributeMap attributes, Class<? extends C> componentType ) {
        this.attributes = attributes;
        this.componentType = componentType;
    }
    
    @Override
    public final ComponentBuilder clear() {
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
    public final ComponentBuilder set( AttributeKey<Float> key, float value ) {
        attributes.put( key, value );
        return this;
    }
    
    @Override
    public final ComponentBuilder set( AttributeKey<Integer> key, int value ) {
        attributes.put( key, value );
        return this;
    }
    
    @Override
    public final ComponentBuilder set( AttributeKey<Long> key, long value ) {
        attributes.put( key, value );
        return this;
    }
    
    @Override
    public final ComponentBuilder set( AttributeKey<Double> key, double value ) {
        attributes.put( key, value );
        return this;
    }
    
    @Override
    public final <T> ComponentBuilder set( AttributeKey<T> key, T value ) {
        attributes.putUntyped( key, value );
        return this;
    }
    
    @SuppressWarnings( "unchecked" )
    @Override
    public final <T> ComponentBuilder set( AttributeKey<DynArray<T>> key, T value, int index ) {
        DynArray<T> array;
        if ( ! attributes.contains( key ) ) {
            array = DynArray.create( (Class<T>) value.getClass() ); 
        } else {
            array = attributes.getValue( key );
        }
        
        array.set( index, value );
        return this;
    }

    @Override
    public final ComponentBuilder add( AttributeKey<IntBag> key, int value ) {
        if ( ! attributes.contains( key ) ) {
            attributes.put( key, new IntBag( 1, -1 ) );
        }
        
        IntBag intBag = attributes.getValue( key );
        intBag.add( value );
        
        return this;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public final <T> ComponentBuilder add( AttributeKey<DynArray<T>> key, T value ) {
        if ( value == null ) {
            return this;
        }
        
        return add( key, value, (Class<T>) value.getClass() );
    }
    
    @Override
    public final <T> ComponentBuilder add( AttributeKey<DynArray<T>> key, T value, Class<T> type ) {
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
        
        list.add( value );
        return this;
    }
    
    
    
    @SuppressWarnings( "unchecked" )
    @Override
    public final <T> ComponentBuilder add( AttributeKey<DynArray<T>> key, T[] values ) {
        if ( values == null ) {
            return this;
        }
        
        return add( key, values, (Class<T>) key.getClass() );
    }
    
    @Override
    public final <T> ComponentBuilder add( AttributeKey<DynArray<T>> key, T[] values, Class<T> type ) {
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
            list.add( values[ i ] );
        }
        return this;
    }
    
    @Override
    public final <T> ComponentBuilder add( AttributeKey<DynArray<T>> key, DynArray<T> values ) {
        return add( key, values.getArray(), values.getTypeClass() );
    }

    @Override
    public final ComponentBuilder add( Attribute<?>... attributes ) {
        for ( Attribute<?> attribute : attributes ) {
            this.attributes.putUntyped( attribute.getKey(), attribute.getValue() );
        }
        return this;
    }

    @Override
    public final ComponentBuilder buildAndNext() {
        build();
        return this;
    }

    @Override
    public final ComponentBuilder buildAndNext( int componentId ) {
        build( componentId );
        return this;
    }

    @Override
    public final ComponentBuilder activateAndNext() {
        activate();
        return this;
    }

    @Override
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
