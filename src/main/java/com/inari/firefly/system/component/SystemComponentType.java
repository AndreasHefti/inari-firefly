package com.inari.firefly.system.component;

import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public final class SystemComponentType {
    
    public final SystemComponentKey<?> typeKey;
    private final Class<? extends SystemComponent> subType;
    private final int hash;
    
    public SystemComponentType( SystemComponentKey<?> typeKey ) {
        this( typeKey, null );
    }
    
    public SystemComponentType( SystemComponentKey<?> typeKey, Class<? extends SystemComponent> subType ) {
        this.typeKey = typeKey;
        this.subType = subType;
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( subType == null ) ? 0 : subType.hashCode() );
        result = prime * result + ( ( typeKey == null ) ? 0 : typeKey.hashCode() );
        hash = result;
    }
    
    
    public final IIndexedTypeKey getTypeKey() {
        return typeKey;
    }

    public final Class<? extends SystemComponent> getSubType() {
        if ( subType != null ) {
            return subType;
        }
        
        return typeKey.type();
    }

    @Override
    public final int hashCode() {
        return hash;
    }

    @Override
    public final boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        SystemComponentType other = (SystemComponentType) obj;
        if ( subType == null ) {
            if ( other.subType != null )
                return false;
        } else if ( !subType.getName().equals( other.subType.getName() ) )
            return false;
        if ( typeKey == null ) {
            if ( other.typeKey != null )
                return false;
        } else if ( !typeKey.aspectGroup().equals( other.typeKey.aspectGroup() ) || typeKey.index() != other.typeKey.index() )
            return false;
        return true;
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "ComponentType [typeKey=" );
        builder.append( typeKey );
        builder.append( ", subType=" );
        builder.append( getSubType() );
        builder.append( "]" );
        return builder.toString();
    }

}
