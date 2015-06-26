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
        Set<AttributeKey<?>> attributeSet = getOrCreateDynAttributeSetForType( componentType );
        attributeSet.add( dynAttributeKey );
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
