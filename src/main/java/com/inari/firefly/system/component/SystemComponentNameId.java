package com.inari.firefly.system.component;

public final class SystemComponentNameId {
    
    public final SystemComponentType type;
    public final String name;

    private final int hash;
    
    public SystemComponentNameId( SystemComponentType type, String name ) {
        this.type = type;
        this.name = name;
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        result = prime * result + ( ( type == null ) ? 0 : type.hashCode() );
        hash = result;
    }

    public final SystemComponentType getType() {
        return type;
    }

    public final String getName() {
        return name;
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
        SystemComponentNameId other = (SystemComponentNameId) obj;
        if ( name == null ) {
            if ( other.name != null )
                return false;
        } else if ( !name.equals( other.name ) )
            return false;
        if ( type == null ) {
            if ( other.type != null )
                return false;
        } else if ( !type.equals( other.type ) )
            return false;
        return true;
    }

}
