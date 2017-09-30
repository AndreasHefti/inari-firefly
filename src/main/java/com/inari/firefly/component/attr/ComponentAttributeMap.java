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
package com.inari.firefly.component.attr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.Component;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.IComponentId;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public class ComponentAttributeMap implements AttributeMap {

    protected IComponentId componentId;
    protected final HashMap<AttributeKey<?>, Object> attributes = new LinkedHashMap<AttributeKey<?>, Object>();
    protected final FFContext context;
    
    public ComponentAttributeMap( FFContext context ) {
        this.context = context;
    }
    
    public final IComponentId getComponentId() {
        return componentId;
    }
    
    public void setComponentId( IComponentId componentId ) {
        this.componentId = componentId;
    }

    @Override
    public final boolean isEmpty() {
        return attributes.isEmpty();
    }
    
    @Override
    public final <A> A getValue( AttributeKey<A> key ) {
        Object value = attributes.get( key );
        if ( value == null ) {
            return null;
        }
        return key.valueType.cast( value );
    }

    @Override
    public final <A> AttributeMap put( AttributeKey<A> key, A value ) {
        attributes.put( key, value );
        return this;
    }
    
    @Override
    public AttributeMap putAll( AttributeMap attributes ) {
        this.attributes.putAll( ( (ComponentAttributeMap) attributes ).attributes ); 
        return this;
    }
    
    @Override
    public final AttributeMap putUntyped( AttributeKey<?> key, Object value ) {
        if ( value == null ) {
            attributes.remove( key );
            return this;
        }
        if ( !key.valueType.isAssignableFrom( value.getClass() ) ) {
            throw new IllegalArgumentException( "The type of value does not match with the key valueType: " + key.valueType + " objectType: " + value.getClass() );
        }
        
        attributes.put( key, value );
        return this;
    }

    @Override
    public final int getValue( AttributeKey<Integer> key, int defaultValue ) {
        Object attributeValue = getValue( key );
        if ( attributeValue == null ) {
            return defaultValue;
        }
        
        return key.valueType.cast( attributeValue );
    }

    @Override
    public final float getValue( AttributeKey<Float> key, float defaultValue ) {
        Object attributeValue = getValue( key );
        if ( attributeValue == null ) {
            return defaultValue;
        }
        
        return key.valueType.cast( attributeValue );
    }

    @Override
    public final boolean getValue( AttributeKey<Boolean> key, boolean defaultValue ) {
        Object attributeValue = getValue( key );
        if ( attributeValue == null ) {
            return defaultValue;
        }
        
        return key.valueType.cast( attributeValue );
    }

    @Override
    public final long getValue( AttributeKey<Long> key, long defaultValue ) {
        Object attributeValue = getValue( key );
        if ( attributeValue == null ) {
            return defaultValue;
        }
        
        return key.valueType.cast( attributeValue );
    }

    @Override
    public final <A> A getValue( AttributeKey<A> key, A defaultValue ) {
        Object attributeValue = getValue( key );
        if ( attributeValue == null ) {
            return defaultValue;
        }
        
        return key.valueType.cast( attributeValue );
    }
    
    @Override
    public final Object getUntypedValue( AttributeKey<?> key, Object defaultValue ) {
        Object attributeValue = getValue( key );
        if ( attributeValue == null ) {
            return defaultValue;
        }
        
        return attributeValue;
    }
    
    @Override
    public final int getIdForName( 
        AttributeKey<String> nameAttribute, 
        AttributeKey<Integer> idAttribute, 
        SystemComponentKey<? extends Component> typeKey, 
        int defaultValue 
    ) {
        if ( contains( idAttribute ) ) {
           return getValue( idAttribute, defaultValue );
        } else {
            String name = getValue( nameAttribute, null );
            return ( name != null )? context.getSystemComponentId( typeKey, name ) : defaultValue;
        }
    }
    
    @Override
    public final IntBag getIdsForNames( 
        AttributeKey<DynArray<String>> namesAttribute, 
        AttributeKey<IntBag> idsAttribute,
        SystemComponentKey<? extends Component> typeKey, 
        IntBag defaultValue 
    ) {
        IntBag result = null;
        if ( contains( idsAttribute ) ) {
           result = getValue( idsAttribute, defaultValue );
        } 
        
        if ( contains( namesAttribute ) ) {
            DynArray<String> names = getValue( namesAttribute, null );
            if ( result == null ) {
                result = new IntBag( names.size(), -1 );
            }
            
            for ( String name : names ) {
                int id = context.getSystemComponentId( typeKey, name );
                if ( !result.contains( id ) ) {
                    result.add( id );
                }
            }
        }
        
        return ( result != null )? result : defaultValue;
    }
    
    @Override
    public final int getAssetInstanceId( 
        AttributeKey<String> nameAttribute, 
        AttributeKey<Integer> idAttribute, 
        int defaultValue 
    ) {
        if ( contains( idAttribute ) ) {
           return getValue( idAttribute, defaultValue );
        } else {
            String name = getValue( nameAttribute, null );
            return ( name != null )? context.getAssetInstanceId( name ) : defaultValue;
        }
    }

    @Override
    public final void clear() {
        attributes.clear();
    }
    
    @Override
    public final boolean contains( AttributeKey<?> key ) {
        return attributes.containsKey( key );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<AttributeKey<?>, Object>> it = attributes.entrySet().iterator();
        while ( it.hasNext() ) {
            Map.Entry<AttributeKey<?>, Object> entry = it.next();
            builder.append( entry.getKey().toString() );
            builder.append( "=" );
            builder.append( String.valueOf( entry.getValue() ) );
            if ( it.hasNext() ) {
                builder.append( ", " );
            }
        }
        return builder.toString();
    }
    
}
