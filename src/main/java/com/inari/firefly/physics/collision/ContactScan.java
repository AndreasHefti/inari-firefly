package com.inari.firefly.physics.collision;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspect;

public final class ContactScan implements Iterable<ContactConstraint> {

    Rectangle contactScanBounds;
    private final Rectangle worldBounds = new Rectangle();
    
    private boolean solidContacts = false;
    private final BitSet contactAspects = new BitSet();
    private final HashMap<String, ContactConstraint> constraints = new HashMap<String, ContactConstraint>();

    public final Rectangle getWorldBounds() {
        return worldBounds;
    }

    public final void update( float x, float y, float vx, float vy ) {
        
        worldBounds.x = ( ( vx > 0 )? (int) Math.ceil( x ) : (int) Math.floor( x ) ) + contactScanBounds.x;
        worldBounds.y = ( ( vy > 0 )? (int) Math.ceil( y ) : (int) Math.floor( y ) ) + contactScanBounds.y;
        worldBounds.width = contactScanBounds.width;
        worldBounds.height = contactScanBounds.height;
        
        for ( ContactConstraint constraint : constraints.values() ) {
            constraint.update( x, y, vx, vy, contactScanBounds );
        }
    }

    public boolean hasAnyContact() {
        return !contactAspects.isEmpty();
    }

    public boolean hasSolidContact() {
        return solidContacts;
    }

    public boolean hasContact( Aspect contact ) {
        return contactAspects.get( contact.index() );
    }

    public final void clearContacts() {
         contactAspects.clear();
         for ( ContactConstraint constraint : constraints.values() ) {
             constraint.clear();
         }
    }
    
    public final void addContactContstraint( ContactConstraint constraint ) {
        constraints.put( constraint.name(), constraint );
        if ( contactScanBounds == null ) {
            contactScanBounds = new Rectangle( constraint.contactScanBounds );
        } else {
            contactScanBounds = GeomUtils.union( contactScanBounds, constraint.contactScanBounds );
        }
    }

    public final void addContact( Contact contact ) {
        for ( ContactConstraint constraint : constraints.values() ) {
            if ( constraint.addContact( contact ) ) {
                final Aspect contactType = contact.contactType();
                if ( contactType != null ) {
                    contactAspects.set( contactType.index() );
                }
            }
        }
    }
    
    @Override
    public final Iterator<ContactConstraint> iterator() {
        return constraints.values().iterator();
    }
    
    public final ContactConstraint getContactContstraint( String name ) {
        return constraints.get( name );
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "ContactScan [contactScanBounds=" );
        builder.append( contactScanBounds );
        builder.append( ", worldBounds=" );
        builder.append( worldBounds );
        builder.append( ", solidContacts=" );
        builder.append( solidContacts );
        builder.append( ", contactAspects=" );
        builder.append( contactAspects );
        builder.append( ", contstraints=" );
        builder.append( constraints );
        builder.append( "]" );
        return builder.toString();
    }

}
