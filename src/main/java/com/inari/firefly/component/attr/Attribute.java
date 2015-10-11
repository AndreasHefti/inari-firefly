package com.inari.firefly.component.attr;

public class Attribute {
    
    final AttributeKey<?> key;
    final Object value;
    
    public Attribute( AttributeKey<?> key, Object value ) {
        super();
        this.key = key;
        this.value = value;
    }

    public final AttributeKey<?> getKey() {
        return key;
    }

    public final Object getValue() {
        return value;
    }
    
    

}
