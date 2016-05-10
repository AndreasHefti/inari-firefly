package com.inari.firefly.physics.collision;

import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;

public final class ContactType implements Aspect {
    
    private final static DynArray<ContactType> CONTACT_TYPES = new DynArray<ContactType>( 10, 10 );
    
    private final int id;
    private final String name;
    private final int hashCode;
    
    public ContactType( int id, String name ) {
        if ( CONTACT_TYPES.contains( id ) ) {
            throw new FFInitException( "There is already a ContactType with id: " + id + " ContactType: " + CONTACT_TYPES.get( id ) );
        }
        this.id = id;
        this.name = name;
        
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        hashCode = result;
        
    }

    @Override
    public final int aspectId() {
        return id;
    }
    
    public final String name() {
        return name;
    }

    @Override
    public final int hashCode() {
        return hashCode;
    }

    @Override
    public final boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ContactType other = (ContactType) obj;
        if ( id != other.id )
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "ContactType [id=" );
        builder.append( id );
        builder.append( ", name=" );
        builder.append( name );
        builder.append( "]" );
        return builder.toString();
    }
    
    public static final ContactType byId( int id ) {
        return CONTACT_TYPES.get( id );
    }
    
    public static final ContactType byName( String name ) {
        if ( name == null ) {
            return null;
        }
        
        for ( ContactType contactType : CONTACT_TYPES ) {
            if ( name.equals( contactType.name ) ) {
                return contactType;
            }
        }
        
        return null;
    }
}
