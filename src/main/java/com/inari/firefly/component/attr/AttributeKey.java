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

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.Component;
import com.inari.firefly.entity.EntityComponent;

public final class AttributeKey<T> {
    
    final String name;
    final Class<? extends Component> componentType;
    final Class<T> valueType;
    
    private final int hashCode;
    
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

    public final String name() {
        return name;
    }

    public Class<T> valueType() {
        return valueType;
    }

    public final Class<? extends Component> componentType() {
        return componentType;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
    
    public final Attribute value( T value ) {
        return new Attribute( this, value );
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
    
    @SuppressWarnings( { "rawtypes", "unchecked" } ) 
    public static final <S> AttributeKey<DynArray<S>> createForDynArray( String name, Class<? extends Component> componentType ) {
        AttributeKey<?> result = new AttributeKey<DynArray>( name, DynArray.class, componentType );
        return (AttributeKey<DynArray<S>>) result;
    }

}
