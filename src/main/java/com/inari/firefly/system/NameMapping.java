package com.inari.firefly.system;

public final class NameMapping {
    
    public final String name1;
    public final String name2;
    
    private final int hashCode;
    
    public NameMapping( String name1, String name2 ) {
        this.name1 = name1;
        this.name2 = name2;
        
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( name1 == null ) ? 0 : name1.hashCode() );
        result = prime * result + ( ( name2 == null ) ? 0 : name2.hashCode() );
        hashCode = result;
    }

    public final String getName1() {
        return name1;
    }

    public final String getName2() {
        return name2;
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
        NameMapping other = (NameMapping) obj;
        if ( name1 == null ) {
            if ( other.name1 != null )
                return false;
        } else if ( !name1.equals( other.name1 ) )
            return false;
        if ( name2 == null ) {
            if ( other.name2 != null )
                return false;
        } else if ( !name2.equals( other.name2 ) )
            return false;
        return true;
    }

}
