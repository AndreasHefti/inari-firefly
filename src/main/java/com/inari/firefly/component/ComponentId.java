package com.inari.firefly.component;

import com.inari.commons.lang.indexed.IIndexedTypeKey;

/** A ComponentId defines the unique identity of a Component instance and consists of the indexed type 
 *  of the Component and an integer number.
 *  
 *  The typing and indexing of Components is done with the indexing API of firefly-commons project behind the scenes
 *  
 *  A ComponentId is automatically created with the creation of a Component. However most of the time there is no 
 *  need to create a new ComponentId to access Components, there are methods that supports both as an attribute, the 
 *  IIndexedTypeKey and the integer id.
 *
 */
public final class ComponentId {
    
    public final IIndexedTypeKey typeKey;
    public final int indexId;
    
    private int hash;
    
    public ComponentId( IIndexedTypeKey typeKey, int id ) {
        this.typeKey = typeKey;
        this.indexId = id;
        
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ( ( typeKey == null ) ? 0 : typeKey.hashCode() );
        hash = result;
    }

    /** Use this to get the IIndexedTypeKey part of the identity of the ComponentId.
     * @return IIndexedTypeKey part the identity of the ComponentId
     */
    public final IIndexedTypeKey getTypeKey() {
        return typeKey;
    }
    
    /** Use this to get the Class that defines the base type of the Component.
     * @return the Class that defines the base type of the Component
     */
    public final Class<? extends Component> getType() {
        return typeKey.type();
    }

    /** Use this to get the index part of the ComponentId.
     *  @return the index part of the ComponentId
     */
    public final int getIndexId() {
        return indexId;
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
