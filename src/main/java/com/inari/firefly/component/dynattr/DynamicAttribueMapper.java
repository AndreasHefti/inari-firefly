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
package com.inari.firefly.component.dynattr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.inari.firefly.component.attr.AttributeKey;

public abstract class DynamicAttribueMapper {

    private static final Map<Class<? extends DynamicAttributedComponent>, Set<AttributeKey<?>>> mapping = new HashMap<Class<? extends DynamicAttributedComponent>, Set<AttributeKey<?>>>();

    public static <T> void addDynamicAttribute( String name, Class<T> valueType, Class<? extends DynamicAttributedComponent> componentType ) {
        AttributeKey<T> dynAttributeKey = new AttributeKey<T>( name, valueType, componentType );
        addDynamicAttribute( dynAttributeKey );
    }

    public static <T> void addDynamicAttribute( AttributeKey<T> attributeKey ) {
        @SuppressWarnings( "unchecked" )
        Set<AttributeKey<?>> attributeSet = getOrCreateDynAttributeSetForType( (Class<? extends DynamicAttributedComponent>) attributeKey.componentType() );
        attributeSet.add( attributeKey );
    }
    
    public static boolean hasDynamicAttributes( Class<? extends DynamicAttributedComponent> componentType ) {
        return mapping.containsKey( componentType );
    }
    
    public static Set<AttributeKey<?>> getDynAttributeSetForType( Class<? extends DynamicAttributedComponent> componentType ) {
        return mapping.get( componentType );
    }

    private static Set<AttributeKey<?>> getOrCreateDynAttributeSetForType( Class<? extends DynamicAttributedComponent> componentType ) {
        Set<AttributeKey<?>> set = mapping.get( componentType );
        if ( set == null ) {
            set = new HashSet<AttributeKey<?>>();
            mapping.put( componentType, set );
        }
        return set;
    }
    
}
