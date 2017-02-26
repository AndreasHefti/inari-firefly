package com.inari.firefly.physics.collision;

import java.util.HashMap;
import java.util.Iterator;

import com.inari.commons.lang.aspect.Aspect;

public final class ContactScan implements Iterable<ContactConstraint> {

    private final HashMap<String, ContactConstraint> constraints = new HashMap<String, ContactConstraint>();
    
    ContactScan() {}

    public final void update( float x, float y, float vx, float vy ) {
        for ( ContactConstraint constraint : constraints.values() ) {
            constraint.update( x, y, vx, vy );
        }
    }

    public final boolean hasAnyContact() {
        for ( ContactConstraint constraint : this ) {
            if ( constraint.hasAnyContact() ) {
                return true;
            }
        }
        
        return false;
    }

    public final boolean hasContact( Aspect contact ) {
        for ( ContactConstraint constraint : this ) {
            if ( constraint.hasContact( contact ) ) {
                return true;
            }
        }
        
        return false;
    }

    public final void clearContacts() {
         for ( ContactConstraint constraint : constraints.values() ) {
             constraint.clear();
         }
    }
    
    public final void addContactContstraint( ContactConstraint constraint ) {
        constraints.put( constraint.name(), constraint );
    }
    
    @Override
    public final Iterator<ContactConstraint> iterator() {
        return constraints.values().iterator();
    }
    
    public final ContactConstraint getContactContstraint( String name ) {
        return constraints.get( name );
    }
    
    public final void clear() {
        constraints.clear();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "ContactScan [constraints=" );
        builder.append( constraints );
        builder.append( "]" );
        return builder.toString();
    }

}
