package com.inari.firefly.asset;

public final class AssetTypeKey {
    
    public final int id;
    public final Class<? extends Asset> type;
    
    public AssetTypeKey( int id, Class<? extends Asset> type ) {
        this.id = id;
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ( ( type == null ) ? 0 : type.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        AssetTypeKey other = (AssetTypeKey) obj;
        if ( id != other.id )
            return false;
        if ( type == null ) {
            if ( other.type != null )
                return false;
        } else if ( type != other.type )
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "AssetTypeKey [id=" );
        builder.append( id );
        builder.append( ", type=" );
        builder.append( type );
        builder.append( "]" );
        return builder.toString();
    }

}
