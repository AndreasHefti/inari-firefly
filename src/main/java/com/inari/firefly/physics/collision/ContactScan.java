package com.inari.firefly.physics.collision;

import java.util.BitSet;
import java.util.Iterator;

import com.inari.commons.geom.BitMask;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.list.DynArray;

public final class ContactScan implements Iterable<Contact> {

    private final Rectangle worldBounds = new Rectangle();
    
    private boolean solidContacts = false;
    private final BitSet contactAspects = new BitSet();
    private final BitMask intersectionMask = new BitMask( 0, 0 );
    private final DynArray<Contact> contacts = new DynArray<Contact>();

    public final Rectangle getWorldBounds() {
        return worldBounds;
    }

    public final void updateWorldBounds( float x, float y, float vx, float vy, Rectangle contactScanBounds ) {
        worldBounds.x = ( ( vx > 0 )? (int) Math.ceil( x ) : (int) Math.floor( x ) ) + contactScanBounds.x;
        worldBounds.y = ( ( vy > 0 )? (int) Math.ceil( y ) : (int) Math.floor( y ) ) + contactScanBounds.y;
        worldBounds.width = contactScanBounds.width;
        worldBounds.height = contactScanBounds.height;
        intersectionMask.reset( 0, 0, worldBounds.width, worldBounds.height );
    }

    public final BitMask getIntersectionMask() {
        return intersectionMask;
    }

    public boolean hasAnyContact() {
        return !contacts.isEmpty();
    }

    public boolean hasSolidContact() {
        return solidContacts;
    }

    public boolean hasContact( Aspect contact ) {
        return contactAspects.get( contact.index() );
    }

    public final Iterator<Contact> iterator() {
        return contacts.iterator();
    }

    public final Contact getFirstContact( Aspect contactType ) {
        for ( Contact contact : contacts ) {
            if ( contact.contactType() == contactType ) {
                return contact;
            }
        }
        
        return null;
    }

    public final void clearContacts() {
         contacts.clear();
         contactAspects.clear();
         intersectionMask.clearMask();
         solidContacts = false;
    }

    public final void addContact( Contact contact ) {
        if ( contact != null ) {
            if ( !solidContacts && contact.isSolid() ) {
                solidContacts = true;
            }
            Aspect contactType = contact.contactType();
            if ( contactType != null ) {
                contactAspects.set( contactType.index() );
            }
            
            if ( contact.isSolid() ) {
                BitMask intersectionMask = contact.intersectionMask();
                if ( intersectionMask != null && !intersectionMask.isEmpty() ) {
                    this.intersectionMask.or( intersectionMask );
                } else {
                    Rectangle intersectionBounds = contact.intersectionBounds();
                    this.intersectionMask.setRegion( intersectionBounds, false );
                }
            }
            
            contacts.add( contact );
        }
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "ContactScan [worldBounds=" );
        builder.append( worldBounds );
        builder.append( ", solidContacts=" );
        builder.append( solidContacts );
        builder.append( ", contactAspects=" );
        builder.append( contactAspects );
        builder.append( ", intersectionMask=" );
        builder.append( intersectionMask );
        builder.append( ", contacts=" );
        builder.append( contacts );
        builder.append( "]" );
        return builder.toString();
    }

}
