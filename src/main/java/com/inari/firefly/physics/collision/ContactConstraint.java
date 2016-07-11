package com.inari.firefly.physics.collision;

import java.util.BitSet;
import java.util.Iterator;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.BitMask;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.list.DynArray; 

public final class ContactConstraint implements Iterable<Contact> {
    
    final String name;
    final Rectangle contactScanBounds = new Rectangle();
    
    private boolean solidOnly = true;
    private boolean allTypes = true;
    private final BitSet contactFilter = new BitSet();
    
    private final Rectangle worldBounds = new Rectangle();
    private final BitSet contactAspects = new BitSet();
    private final BitMask intersectionMask = new BitMask( 0, 0 );
    private final DynArray<Contact> contacts = new DynArray<Contact>();
    
    public ContactConstraint( String name, Rectangle contactScanBounds ) {
        this.name = name;
        this.contactScanBounds.setFrom( contactScanBounds );
    }
    
    public final String name() {
        return name;
    }
    
    public final ContactConstraint solidOnly( boolean solidOnly ) {
        this.solidOnly = solidOnly;
        return this;
    }
    
    public final ContactConstraint allTypes( boolean allTypes ) {
        this.allTypes = allTypes;
        return this;
    }
    
    public final ContactConstraint addToFilter( Aspect contact ) {
        contactFilter.set( contact.index() );
        return this;
    }
    
    public final ContactConstraint removeFromFilter( Aspect contact ) {
        contactFilter.set( contact.index(), false );
        return this;
    }
    
    public final boolean solidOnly() {
        return solidOnly;
    }
    
    public final boolean allTypes() {
        return allTypes;
    }
    
    public final void clearFilter() {
        contactFilter.clear();
    }
    
    public final BitMask getIntersectionMask() {
        return intersectionMask;
    }

    public final boolean hasAnyContact() {
        return !contacts.isEmpty();
    }
    
    public final boolean hasContact( Aspect contact ) {
        return contactAspects.get( contact.index() );
    }
    
    @Override
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
    
    final void clear() {
         contacts.clear();
         contactAspects.clear();
         intersectionMask.clearMask();
    }
    
    final void update( float x, float y, float vx, float vy ) {
        worldBounds.x = ( ( vx > 0 )? (int) Math.ceil( x ) : (int) Math.floor( x ) ) + contactScanBounds.x;
        worldBounds.y = ( ( vy > 0 )? (int) Math.ceil( y ) : (int) Math.floor( y ) ) + contactScanBounds.y;
        worldBounds.width = contactScanBounds.width;
        worldBounds.height = contactScanBounds.height;
        intersectionMask.reset( 0, 0, worldBounds.width, worldBounds.height );
    }
    
    final boolean addContact( final Contact contact ) {
        if ( contact == null ) { 
            return false;
        }
        
        if ( solidOnly && !contact.isSolid() ) {
            return false;
        }
        
        final Aspect contactType = contact.contactType();
        if ( !allTypes ) {
            if ( contactType == null || !contactFilter.get( contactType.index() ) ) {
                return false;
            }
        }
        
        if ( !GeomUtils.intersect( contact.intersectionBounds(), contactScanBounds ) ) {
            return false;
        }

        BitMask intersectionMask = contact.intersectionMask();
        if ( intersectionMask != null && !intersectionMask.isEmpty() ) {
            this.intersectionMask.or( intersectionMask );
        } else {
            Rectangle intersectionBounds = contact.intersectionBounds();
            this.intersectionMask.setRegion( intersectionBounds, false );
        }
        
        if ( contactType != null ) {
            contactAspects.set( contactType.index() );
        }
        contacts.add( contact );
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "ContactContstraint [name=" );
        builder.append( name );
        builder.append( ", contactScanBounds=" );
        builder.append( contactScanBounds );
        builder.append( ", solidOnly=" );
        builder.append( solidOnly );
        builder.append( ", allTypes=" );
        builder.append( allTypes );
        builder.append( ", contactFilter=" );
        builder.append( contactFilter );
        builder.append( ", worldBounds=" );
        builder.append( worldBounds );
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
