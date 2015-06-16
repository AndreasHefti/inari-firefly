package com.inari.firefly.component;

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

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        AttributeKey<?> other = (AttributeKey<?>) obj;
        if ( componentType == null ) {
            if ( other.componentType != null )
                return false;
        } else if ( componentType != other.componentType )
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
        return "AttributeKey [name=" + name + ", componentType=" + componentType + "]";
    }

}
