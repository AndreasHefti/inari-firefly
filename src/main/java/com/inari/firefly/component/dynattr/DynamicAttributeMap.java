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
        Set<AttributeKey<?>> dynAttributeSetForType = DynamicAttribueMapper.getDynAttributeSetForType( component.getComponentType() );
        if ( dynAttributeSetForType != null && !attributeKeys.containsAll( dynAttributeSetForType ) ) {
            attributeKeys.addAll( dynAttributeSetForType );
        } 
        
        return attributeKeys; 
    }

    public final void fromAttributeMap( AttributeMap attributes, DynamicAttributedComponent component ) {
        Set<AttributeKey<?>> dynAttributeSetForType = DynamicAttribueMapper.getDynAttributeSetForType( component.getComponentType() );
        if ( dynAttributeSetForType == null ) {
            return;
        }
        
        for ( AttributeKey<?> key : dynAttributeSetForType ) {
            Object defaultValue = dynamicAttributes.get( key );
            dynamicAttributes.put( key, attributes.getUntypedValue( key, defaultValue ) );
        }
    }

    public final void toAttributeMap( AttributeMap attributes, DynamicAttributedComponent component ) {
        Set<AttributeKey<?>> dynAttributeSetForType = DynamicAttribueMapper.getDynAttributeSetForType( component.getComponentType() );
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
