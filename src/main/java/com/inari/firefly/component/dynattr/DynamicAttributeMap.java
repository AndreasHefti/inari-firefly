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
package com.inari.firefly.component.dynattr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;


public class DynamicAttributeMap {
    
    protected Map<AttributeKey<?>, Object> dynamicAttributes = new HashMap<AttributeKey<?>, Object>();

    public final Set<AttributeKey<?>> attributeKeys( DynamicAttributedComponent component, Set<AttributeKey<?>> attributeKeys ) {
        attributeKeys.addAll( component.attributeKeys() );
        Set<AttributeKey<?>> dynAttributeSetForType = DynamicAttribueMapper.getDynAttributeSetForType( component.componentType() );
        if ( dynAttributeSetForType != null && !attributeKeys.containsAll( dynAttributeSetForType ) ) {
            attributeKeys.addAll( dynAttributeSetForType );
        } 
        
        return attributeKeys; 
    }

    public final void fromAttributeMap( AttributeMap attributes, DynamicAttributedComponent component ) {
        Set<AttributeKey<?>> dynAttributeSetForType = DynamicAttribueMapper.getDynAttributeSetForType( component.componentType() );
        if ( dynAttributeSetForType == null ) {
            return;
        }
        
        for ( AttributeKey<?> key : dynAttributeSetForType ) {
            Object defaultValue = dynamicAttributes.get( key );
            dynamicAttributes.put( key, attributes.getUntypedValue( key, defaultValue ) );
        }
    }

    public final void toAttributeMap( AttributeMap attributes, DynamicAttributedComponent component ) {
        Set<AttributeKey<?>> dynAttributeSetForType = DynamicAttribueMapper.getDynAttributeSetForType( component.componentType() );
        if ( dynAttributeSetForType == null ) {
            return;
        }
    }
    
    public final <A> void setDynamicAttribute( AttributeKey<A> key, A value, Class<? extends DynamicAttributedComponent> type ) {
        Set<AttributeKey<?>> dynAttributeSetForType = DynamicAttribueMapper.getDynAttributeSetForType( type );
        if ( dynAttributeSetForType == null || !dynAttributeSetForType.contains( key ) ) {
            throw new IllegalArgumentException( "Unknown AttributeKey " + key + " for DynamicAttributedAsset " + type );
        }
        
        dynamicAttributes.put( key, value );
    }
    
    public final <A> A getDynamicAttribute( AttributeKey<A> key ) {
        Object value = dynamicAttributes.get( key );
        if ( value == null ) {
            return null;
        }
        
        return key.valueType().cast( value );
    }

}
