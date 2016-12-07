package com.inari.firefly.component.attr;

/** An Attribute for the Components attribute interface consists of a AttributeKey and a value.
 *  The AttributeKey defines the value type, the name and the Component type of the attribute.
 *  
 *  @param <T> the Type of the Attribute and the value
 */
public final class Attribute<T> {
    
    final AttributeKey<T> key;
    T value;
    
    /** Use this to create a new Attribute with specified AttributeKey and value.
     * 
     * @param key The AttributeKey that defines the value type, name and component type of the Attribute
     * @param value the value of the Attribute
     */
    public Attribute( AttributeKey<T> key, T value ) {
        super();
        this.key = key;
        this.value = value;
    }

    /** Use this to get the AttributeKey of this attribute.
     * @return the AttributeKey of this attribute
     */
    public final AttributeKey<T> getKey() {
        return key;
    }
    
    /** use this to set the value for this Attribute.
     * @param value the value for this Attribute
     */
    public final void setValue( T value ) {
        this.value = value;
    }

    /** Use this to get the value of this Attribute.
     * @return the value of this Attribute
     */
    public final Object getValue() {
        return value;
    }
}
