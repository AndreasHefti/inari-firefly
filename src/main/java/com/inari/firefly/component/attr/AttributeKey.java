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

import java.util.BitSet;

import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.Component;
import com.inari.firefly.entity.EntityComponent;

/** An AttributeKey defines the value type, the name and the component type of an Attribute and is the identity
 *  of that Attribute.
 *  
 *  An Attribute has a name and a value and belongs to a specific type of component.
 *
 * @param <T> the Type and value type of the Attribute.
 */
public final class AttributeKey<T> {
    
    final String name;
    final Class<? extends Component> componentType;
    final Class<T> valueType;
    
    private final int hashCode;
    
    /** Use this to create a new AttributeKey with specified name, valueType and component type.
     * 
     * @param name the name of the attribute
     * @param valueType the value type of the attribute
     * @param componentType the component type of the attribute.
     */
    public AttributeKey( String name, Class<T> valueType, Class<? extends Component> componentType ) {
        super();
        this.name = name;
        this.componentType = componentType;
        this.valueType = valueType;
        
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( componentType == null ) ? 0 : componentType.hashCode() );
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        hashCode = result;
    }

    /** Use this to get the name of the attribute
     * @return the name of the attribute
     */
    public final String name() {
        return name;
    }

    /** Use this to get the value type class of the attribute.
     * @return the value type class of the attribute.
     */
    public Class<T> valueType() {
        return valueType;
    }

    /** Use this to get the component type class of this attribute
     * @return the component type class of this attribute
     */
    public final Class<? extends Component> componentType() {
        return componentType;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        AttributeKey<?> other = (AttributeKey<?>) obj;
        if ( componentType != other.componentType ) 
            return false;
        if ( name == null ) {
            if ( other.name != null )
                return false;
        } else if ( !name.equals( other.name ) )
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( name );
        builder.append( ":" );
        builder.append( valueType.getSimpleName() );
        if ( EntityComponent.class.isAssignableFrom( componentType )  ) {
            builder.append( ":" );
            builder.append( componentType.getSimpleName() );
        }
        return builder.toString();
    }
    
    /** Utility to create an AttributeKey with specified name, value type and componentType.
     * @param name the name of the AttributeKey
     * @param type the value Type of the AttributeKey
     * @param componentType the componentType of the AttributeKey
     * @return an AttributeKey with specified name, value type and componentType
     */
    public static final <T> AttributeKey<T> create( String name, Class<T> type, Class<? extends Component> componentType ) {
        return new AttributeKey<T>( name, type, componentType );
    }
    
    /** Utility to create an AttributeKey with Integer value type, specified name and componentType.
     * @param name the name of the AttributeKey
     * @param componentType the componentType of the AttributeKey
     * @return an AttributeKey with Integer value type, specified name and componentType
     */
    public static final AttributeKey<Integer> createInt( String name, Class<? extends Component> componentType ) {
        return new AttributeKey<Integer>( name, Integer.class, componentType );
    }
    
    /** Utility to create an AttributeKey with String value type, specified name and componentType.
     * @param name the name of the AttributeKey
     * @param componentType the componentType of the AttributeKey
     * @return an AttributeKey with Integer value type, specified name and componentType
     */
    public static final AttributeKey<String> createString( String name, Class<? extends Component> componentType ) {
        return new AttributeKey<String>( name, String.class, componentType );
    }
    
    /** Utility to create an AttributeKey with Float value type, specified name and componentType.
     * @param name the name of the AttributeKey
     * @param componentType the componentType of the AttributeKey
     * @return an AttributeKey with Integer value type, specified name and componentType
     */
    public static final AttributeKey<Float> createFloat( String name, Class<? extends Component> componentType ) {
        return new AttributeKey<Float>( name, Float.class, componentType );
    }
    
    /** Utility to create an AttributeKey with Boolean value type, specified name and componentType.
     * @param name the name of the AttributeKey
     * @param componentType the componentType of the AttributeKey
     * @return an AttributeKey with Integer value type, specified name and componentType
     */
    public static final AttributeKey<Boolean> createBoolean( String name, Class<? extends Component> componentType ) {
        return new AttributeKey<Boolean>( name, Boolean.class, componentType );
    }
    
    /** Utility to create an AttributeKey with BitSet value type, specified name and componentType.
     * @param name the name of the AttributeKey
     * @param componentType the componentType of the AttributeKey
     * @return an AttributeKey with Integer value type, specified name and componentType
     */
    public static final AttributeKey<BitSet> createBitSet( String name, Class<? extends Component> componentType ) {
        return new AttributeKey<BitSet>( name, BitSet.class, componentType );
    }
    
    /** Utility to create an AttributeKey with Aspects value type, specified name and componentType.
     * @param name the name of the AttributeKey
     * @param componentType the componentType of the AttributeKey
     * @return an AttributeKey with Integer value type, specified name and componentType
     */
    public static final AttributeKey<Aspects> createAspects( String name, Class<? extends Component> componentType ) {
        return new AttributeKey<Aspects>( name, Aspects.class, componentType );
    }
    
    /** Utility to create an AttributeKey with Aspect value type, specified name and componentType.
     * @param name the name of the AttributeKey
     * @param componentType the componentType of the AttributeKey
     * @return an AttributeKey with Integer value type, specified name and componentType
     */
    public static final AttributeKey<Aspect> createAspect( String name, Class<? extends Component> componentType ) {
        return new AttributeKey<Aspect>( name, Aspect.class, componentType );
    }
    
    /** Utility to create an AttributeKey with DynArray value type, specified name and componentType.
     * @param name the name of the AttributeKey
     * @param componentType the componentType of the AttributeKey
     * @return an AttributeKey with Integer value type, specified name and componentType
     */
    @SuppressWarnings( { "rawtypes", "unchecked" } ) 
    public static final <S> AttributeKey<DynArray<S>> createDynArray( String name, Class<? extends Component> componentType ) {
        AttributeKey<?> result = new AttributeKey<DynArray>( name, DynArray.class, componentType );
        return (AttributeKey<DynArray<S>>) result;
    }

    public static final AttributeKey<IntBag> createIntBag( String name, Class<? extends Component> componentType ) {
        return new AttributeKey<IntBag>( name, IntBag.class, componentType );
    }

    public static final AttributeKey<Long> createLong( String name, Class<? extends Component> componentType ) {
        return new AttributeKey<Long>( name, Long.class, componentType );
    }

}
