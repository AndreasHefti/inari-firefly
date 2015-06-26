package com.inari.firefly.component.attr;

import com.inari.firefly.component.Component;
import com.inari.firefly.entity.EntityComponent;

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
        if ( componentType != other.componentType ) 
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
        StringBuilder builder = new StringBuilder();
        builder.append( name );
        builder.append( ":" );
        builder.append( valueType.getSimpleName() );
        if ( EntityComponent.class.isAssignableFrom( componentType )  ) {
            builder.append( ":" );
            builder.append( componentType.getSimpleName() );
        }
        return builder.toString();
    }

}
