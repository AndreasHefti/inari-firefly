package com.inari.firefly.component;

import com.inari.commons.lang.indexed.IIndexedTypeKey;

public final class ComponentId {
    
    public final IIndexedTypeKey typeKey;
    public final int indexId;
    public final String name;
    private int hash;
    
    public ComponentId( IIndexedTypeKey typeKey, int id ) {
        this( typeKey, id, null );
    }
    
    public ComponentId( IIndexedTypeKey typeKey, int id, String name ) {
        this.typeKey = typeKey;
        this.indexId = id;
        this.name = name;
        
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ( ( typeKey == null ) ? 0 : typeKey.hashCode() );
        hash = result;
    }

    public final IIndexedTypeKey getTypeKey() {
        return typeKey;
    }
    
    public final Class<? extends Component> getType() {
        return typeKey.type();
    }

    public final int getIndexId() {
        return indexId;
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
        ComponentId other = (ComponentId) obj;
        if ( indexId != other.indexId )
            return false;
        if ( typeKey == null ) {
            if ( other.typeKey != null )
                return false;
        } else if ( !typeKey.equals( other.typeKey ) )
            return false;
        return true;
    }

        @Override
        public final String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append( typeKey );
            builder.append( "(" );
            builder.append( indexId );
            builder.append( ")" );
            return builder.toString();
        }
}
