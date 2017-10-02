package com.inari.firefly.physics.collision;

import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.list.DynArray;

public final class ContactScan {

    final DynArray<Contacts> contacts;
    
    ContactScan() {
        contacts = DynArray.create( Contacts.class, 3, 5 );
    }

    public final boolean hasAnyContact() {
        for ( int i = 0; i < contacts.capacity(); i++ ) {
            Contacts c = contacts.get( i );
            if ( c == null ) {
                continue;
            }
            
            if ( c.hasAnyContact() ) {
                return true;
            }
        }
        
        return false;
    }

    public final boolean hasContact( Aspect contact ) {
        for ( int i = 0; i < contacts.capacity(); i++ ) {
            Contacts c = contacts.get( i );
            if ( c == null ) {
                continue;
            }
            
            if ( c.hasContact( contact ) ) {
                return true;
            }
        }
        
        return false;
    }

    public final void clearContacts() {
         for ( int i = 0; i < contacts.capacity(); i++ ) {
            Contacts c = contacts.get( i );
            if ( c == null ) {
                continue;
            }
            
            c.clear();
         }
    }
    
    public final Contacts getContacts( int contactConstraintId ) {
        return contacts.get( contactConstraintId );
    }
    
    final void clear() {
        for ( int i = 0; i < contacts.capacity(); i++ ) {
            Contacts c = contacts.get( i );
            if ( c == null ) {
                continue;
            }
            
            c.clear();
         }
        contacts.clear();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "ContactScan [constraints=" );
        builder.append( contacts );
        builder.append( "]" );
        return builder.toString();
    }

}
