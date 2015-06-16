package com.inari.firefly.component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AttributeMapImpl implements AttributeMap {
    
    private final HashMap<AttributeKey<?>, Object> attributes = new HashMap<AttributeKey<?>, Object>();
    private final Set<Class<? extends Component>> componentTypes = new HashSet<Class<? extends Component>>();

    /* (non-Javadoc)
     * @see com.inari.firefly.component.IAttributeMap#get(com.inari.firefly.component.AttributeKey)
     */
    @Override
    public final <A> A getValue( AttributeKey<A> key ) {
        Object value = attributes.get( key );
        if ( value == null ) {
            return null;
        }
        return key.valueType.cast( value );
    }

    /* (non-Javadoc)
     * @see com.inari.firefly.component.IAttributeMap#put(com.inari.firefly.component.Attribute)
     */
    @Override
    public final <A> AttributeMapImpl put( AttributeKey<A> key, A value ) {
        attributes.put( key, value );
        return this;
    }
    
    /* (non-Javadoc)
     * @see com.inari.firefly.component.IAttributeMap#put(com.inari.firefly.component.Attribute)
     */
    @Override
    public AttributeMapImpl putAll( AttributeMap attributes ) {
        this.attributes.putAll( ( (AttributeMapImpl) attributes ).attributes ); 
        return this;
    }
    
    @Override
    public final AttributeMapImpl putUntyped( AttributeKey<?> key, Object value ) {
        if ( !key.valueType.isAssignableFrom( value.getClass() ) ) {
            throw new IllegalArgumentException( "The type of value does not match with the key valueType: " + key.valueType + " objectType: " + value.getClass() );
        }
        
        attributes.put( key, value );
        return this;
    }

    /* (non-Javadoc)
     * @see com.inari.firefly.component.IAttributeMap#getValue(com.inari.firefly.component.AttributeKey, int)
     */
    @Override
    public final int getValue( AttributeKey<Integer> key, int defaultValue ) {
        Object attributeValue = getValue( key );
        if ( attributeValue == null ) {
            return defaultValue;
        }
        
        return key.valueType.cast( attributeValue );
    }

    /* (non-Javadoc)
     * @see com.inari.firefly.component.IAttributeMap#getValue(com.inari.firefly.component.AttributeKey, float)
     */
    @Override
    public final float getValue( AttributeKey<Float> key, float defaultValue ) {
        Object attributeValue = getValue( key );
        if ( attributeValue == null ) {
            return defaultValue;
        }
        
        return key.valueType.cast( attributeValue );
    }

    /* (non-Javadoc)
     * @see com.inari.firefly.component.IAttributeMap#getValue(com.inari.firefly.component.AttributeKey, boolean)
     */
    @Override
    public final boolean getValue( AttributeKey<Boolean> key, boolean defaultValue ) {
        Object attributeValue = getValue( key );
        if ( attributeValue == null ) {
            return defaultValue;
        }
        
        return key.valueType.cast( attributeValue );
    }

    /* (non-Javadoc)
     * @see com.inari.firefly.component.IAttributeMap#getValue(com.inari.firefly.component.AttributeKey, long)
     */
    @Override
    public final long getValue( AttributeKey<Long> key, long defaultValue ) {
        Object attributeValue = getValue( key );
        if ( attributeValue == null ) {
            return defaultValue;
        }
        
        return key.valueType.cast( attributeValue );
    }

    /* (non-Javadoc)
     * @see com.inari.firefly.component.IAttributeMap#getValue(com.inari.firefly.component.AttributeKey, A)
     */
    @Override
    public final <A> A getValue( AttributeKey<A> key, A defaultValue ) {
        Object attributeValue = getValue( key );
        if ( attributeValue == null ) {
            return defaultValue;
        }
        
        return key.valueType.cast( attributeValue );
    }
    
    @Override
    public Object getUntypedValue( AttributeKey<?> key, Object defaultValue ) {
        Object attributeValue = getValue( key );
        if ( attributeValue == null ) {
            return defaultValue;
        }
        
        return attributeValue;
    }
    
    @Override
    public Set<Class<? extends Component>> getComponentTypes() {
        componentTypes.clear();
        for ( AttributeKey<?> attrKey : attributes.keySet() ) {
            componentTypes.add( attrKey.componentType );
        }
        return componentTypes;
    }
    
    @Override
    public void clear() {
        attributes.clear();
        componentTypes.clear();
    }

    @Override
    public String toString() {
        return String.valueOf( attributes.values() );
    }

}
