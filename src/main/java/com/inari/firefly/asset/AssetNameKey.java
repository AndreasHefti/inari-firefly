package com.inari.firefly.asset;

public final class AssetNameKey {
    
    public final String group;
    public final String name;
    private final int hash;
    
    public AssetNameKey( String group, String name ) {
        this.group = group;
        this.name = name;
        
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( group == null ) ? 0 : group.hashCode() );
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        hash = result;
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
        AssetNameKey other = (AssetNameKey) obj;
        if ( group == null ) {
            if ( other.group != null )
                return false;
        } else if ( !group.equals( other.group ) )
            return false;
        if ( name == null ) {
            if ( other.name != null )
                return false;
        } else if ( !name.equals( other.name ) )
            return false;
        return true;
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "AssetKey [group=" );
        builder.append( group );
        builder.append( ", name=" );
        builder.append( name );
        builder.append( "]" );
        return builder.toString();
    }

}
