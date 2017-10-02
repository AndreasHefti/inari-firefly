package com.inari.firefly.physics.collision;

import java.util.HashMap;

import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.list.DynArray;

public final class ContactScan {

    // TODO instead of the ContactConstraint only the ContactInstance is mapped within a complete ContactScan
    private final HashMap<String, ContactConstraint> constraintsNameMapping = new HashMap<String, ContactConstraint>();
    final DynArray<ContactConstraint> constraints = DynArray.create( ContactConstraint.class );
    
    ContactScan() {}

    

    public final boolean hasAnyContact() {
        for ( int i = 0; i < constraints.capacity(); i++ ) {
            ContactConstraint constraint = constraints.get( i );
            if ( constraint == null ) {
                continue;
            }
            
            if ( constraint.hasAnyContact() ) {
                return true;
            }
        }
        
        return false;
    }

    public final boolean hasContact( Aspect contact ) {
        for ( int i = 0; i < constraints.capacity(); i++ ) {
            ContactConstraint constraint = constraints.get( i );
            if ( constraint == null ) {
                continue;
            }
            
            if ( constraint.hasContact( contact ) ) {
                return true;
            }
        }
        
        return false;
    }

    public final void clearContacts() {
         for ( int i = 0; i < constraints.capacity(); i++ ) {
            ContactConstraint constraint = constraints.get( i );
            if ( constraint == null ) {
                continue;
            }
            
            constraint.clear();
         }
    }
    
    public final void addContactContstraint( ContactConstraint constraint ) {
        constraintsNameMapping.put( constraint.name(), constraint );
        constraints.add( constraint );
    }
    
    public final ContactConstraint getContactContstraint( String name ) {
        return constraintsNameMapping.get( name );
    }
    
    public final void clear() {
        constraintsNameMapping.clear();
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
