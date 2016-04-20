package com.inari.firefly.component.attr;

public class Attribute {
    
    final AttributeKey<?> key;
    Object value;
    
    public Attribute( AttributeKey<?> key, Object value ) {
        super();
        this.key = key;
        this.value = value;
    }

    public final AttributeKey<?> getKey() {
        return key;
    }
    
    public final void setValue( Object value ) {
        this.value = value;
    }

    public final Object getValue() {
        return value;
    }
    
    

}
