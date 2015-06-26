package com.inari.firefly.component.attr;


public interface AttributeMap {
    
    ComponentKey getComponentKey();
    
    void setComponentKey( ComponentKey typeKey );

    <A> A getValue( AttributeKey<A> key );

    <A> AttributeMap put( AttributeKey<A> key, A value );
    
    AttributeMap putAll( AttributeMap attributes );
    
    AttributeMap putUntyped( AttributeKey<?> key, Object value );

    int getValue( AttributeKey<Integer> key, int defaultValue );

    float getValue( AttributeKey<Float> key, float defaultValue );

    boolean getValue( AttributeKey<Boolean> key, boolean defaultValue );

    long getValue( AttributeKey<Long> key, long defaultValue );

    <A> A getValue( AttributeKey<A> key, A defaultValue );

    Object getUntypedValue( AttributeKey<?> key, Object defaultValue );

    void clear();

}