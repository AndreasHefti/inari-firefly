package com.inari.firefly.physics.collision;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.BitMask;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.list.DynArray; 

public final class ContactConstraint  {
    
    final String name;
    int layerId = -1;
    
    final Rectangle contactScanBounds = new Rectangle();
    final Rectangle normalizedContactScanBounds = new Rectangle();
    final Rectangle worldBounds = new Rectangle();
    private final Aspects materialTypeFilter = CollisionSystem.MATERIAL_ASPECT_GROUP.createAspects();
    private boolean filtering = false;
    
    private final Aspects contactTypes = CollisionSystem.CONTACT_ASPECT_GROUP.createAspects();
    private final Aspects materialTypes = CollisionSystem.MATERIAL_ASPECT_GROUP.createAspects();
    private final BitMask intersectionMask = new BitMask( 0, 0 );
    private final DynArray<Contact> contacts = DynArray.create( Contact.class, 20, 10 );
    
    public ContactConstraint( String name, Rectangle contactScanBounds ) {
        this.name = name;
        this.contactScanBounds.setFrom( contactScanBounds );
        normalizedContactScanBounds.setFrom( contactScanBounds );
        normalizedContactScanBounds.x = 0;
        normalizedContactScanBounds.y = 0;
    }
    
    public final String name() {
        return name;
    }

    public final int layerId() {
        return layerId;
    }
    
    public final int width() {
        return contactScanBounds.width;
    }
    
    public final int height() {
        return contactScanBounds.height;
    }
    
    public final int pivotX() {
        return contactScanBounds.x;
    }
    
    public final int pivotY() {
        return contactScanBounds.y;
    }

    public final ContactConstraint layerId( int layerId ) {
        this.layerId = layerId;
        return this;
    }

    
    public final ContactConstraint addToMaterialFilter( Aspect contact ) {
        materialTypeFilter.set( contact );
        filtering = true;
        return this;
    }
    
    public final ContactConstraint removeFromFilter( Aspect contact ) {
        materialTypeFilter.reset( contact );
        filtering = !materialTypeFilter.getValues().isEmpty();
        return this;
    }

    public final boolean filterAplied() {
        return filtering;
    }
    
    public final void clearFilter() {
        materialTypeFilter.clear();
        filtering = false;
    }
    
    public final boolean hasContact( Position p ) {
        return intersectionMask.getBit( p.x, p.y );
    }
    
    public final boolean hasContact( Position p1, Position p2 ) {
        return intersectionMask.getBit( p1.x, p1.y ) || intersectionMask.getBit( p2.x, p2.y );
    }

    public final boolean hasContact( int x, int y ) {
        return intersectionMask.getBit( x, y );
    }
    
    public final boolean hasContactType( final Aspect contactType, Position p ) {
        return hasContactType( contactType, p.x, p.y );
    }
    
    public final boolean hasContactType( final Aspect contactType, int x, int y ) {
        if ( !contactTypes.contains( contactType ) ) {
            return false;
        }
        
        for ( int i = 0; i < contacts.capacity(); i++ ) {
            Contact contact = contacts.get( i );
            if ( contact == null || contact.contactType.index() != contactType.index() ) {
                continue;
            }
            
            if ( contact.hasContact( x, y ) ) {
                return true;
            }
        }
        
        return false;
    }
    
    public final boolean hasContactTypeExclusive( final Aspect contactType, Position p ) {
        return hasContactTypeExclusive( contactType, p.x, p.y );
    }
    
    public final boolean hasContactTypeExclusive( final Aspect contactType, int x, int y ) {
        if ( contactTypes.contains( contactType ) ) {
            return false;
        }
        
        for ( int i = 0; i < contacts.capacity(); i++ ) {
            Contact contact = contacts.get( i );
            if ( contact == null || contact.contactType.index() == contactType.index() ) {
                continue;
            }
            
            if ( contact.hasContact( x, y ) ) {
                return true;
            }
        }
        
        return false;
    }
    
    public final boolean hasContact( final Aspect material, Position p ) {
        return hasContact( material, p.x, p.y );
    }

    public final boolean hasContact( final Aspect material, int x, int y ) {
        if ( !materialTypes.contains( material ) ) {
            return false;
        }
        
        for ( int i = 0; i < contacts.capacity(); i++ ) {
            Contact contact = contacts.get( i );
            if ( contact == null || contact.materialType.index() != material.index() ) {
                continue;
            }
            
            if ( contact.hasContact( x, y ) ) {
                return true;
            }
        }
        
        return false;
    }
    
    public final boolean hasContactExclusive( final Aspect material, Position p ) {
        return hasContactExclusive( material, p.x, p.y );
    }

    public final boolean hasContactExclusive( final Aspect material, int x, int y ) {
        if ( materialTypes.contains( material ) ) {
            return false;
        }
        
        for ( int i = 0; i < contacts.capacity(); i++ ) {
            Contact contact = contacts.get( i );
            if ( contact == null || contact.materialType.index() == material.index() ) {
                continue;
            }
            
            if ( contact.hasContact( x, y ) ) {
                return true;
            }
        }
        
        return false;
    }

    public final BitMask getIntersectionMask() {
        return intersectionMask;
    }

    public final boolean hasAnyContact() {
        return !contacts.isEmpty();
    }
    
    public final boolean hasAnyContacts( final Aspects contact ) {
        return contactTypes.intersects( contact );
    }
    
    public final boolean hasContact( final Aspect contact ) {
        return contactTypes.contains( contact );
    }
    
    public final boolean hasAnyMaterialContact( final Aspects materials ) {
        return materialTypes.intersects( materials );
    }
    
    public final boolean hasMaterialContact( final Aspect material ) {
        return materialTypes.contains( material );
    }
    
    public final DynArray<Contact> allContacts() {
        return contacts;
    }
    
    public final Contact get( int x, int y ) {
        for ( int i = 0; i < contacts.capacity(); i++ ) {
            Contact contact = contacts.get( i );
            if ( contact == null ) {
                continue;
            }
            
            if ( contact.intersects( x, y ) ) {
                return contact;
            }
        }
        
        return null;
    }

    public final Contact getFirstContact( final Aspect contactType ) {
        for ( int i = 0; i < contacts.capacity(); i++ ) {
            Contact contact = contacts.get( i );
            if ( contact == null ) {
                continue;
            }
            
            if ( contact.contactType() == contactType ) {
                return contact;
            }
        }
        
        return null;
    }
    
    final void clear() {
        for ( int i = 0; i < contacts.capacity(); i++ ) {
            Contact contact = contacts.get( i );
            if ( contact == null ) {
                continue;
            }
            
            contact.dispose();
        }
        contacts.clear();
        contactTypes.clear();
        materialTypes.clear();
        intersectionMask.clearMask();
    }
    
    final void update( float x, float y, float vx, float vy ) {
        worldBounds.x = ( ( vx > 0 )? (int) Math.ceil( x ) : (int) Math.floor( x ) ) + contactScanBounds.x;
        worldBounds.y = ( ( vy > 0 )? (int) Math.ceil( y ) : (int) Math.floor( y ) ) + contactScanBounds.y;
        worldBounds.width = contactScanBounds.width;
        worldBounds.height = contactScanBounds.height;
        intersectionMask.reset( 0, 0, contactScanBounds.width, contactScanBounds.height );
    }
    
    final boolean match( ECollision collision ) {
        if ( !filtering ) {
            return true;
        } else {
            final Aspect materialType = collision.getMaterialType();
            return ( materialType != null && materialTypeFilter.contains( materialType ) );
        }
    }
    
    final boolean addContact( final Contact contact ) {
        if ( contact == null ) { 
            return false;
        }

        if ( !GeomUtils.intersect( contact.intersectionBounds(), normalizedContactScanBounds ) ) {
            return false;
        }

        BitMask intersectionMask = contact.intersectionMask();
        if ( intersectionMask != null && !intersectionMask.isEmpty() ) {
            this.intersectionMask.or( intersectionMask );
        } else {
            Rectangle intersectionBounds = contact.intersectionBounds();
            this.intersectionMask.setRegion( intersectionBounds, true );
        }
        
        if ( contact.contactType != null ) {
            contactTypes.set( contact.contactType );
        }
        if ( contact.materialType != null ) {
            materialTypes.set( contact.materialType );
        }
        
        contacts.add( contact );
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "ContactConstraint [name=" );
        builder.append( name );
        builder.append( ", layerId=" );
        builder.append( layerId );
        builder.append( ", contactScanBounds=" );
        builder.append( contactScanBounds );
        builder.append( ", normalizedContactScanBounds=" );
        builder.append( normalizedContactScanBounds );
        builder.append( ", worldBounds=" );
        builder.append( worldBounds );
        builder.append( ", materialTypeFilter=" );
        builder.append( materialTypeFilter );
        builder.append( ", filtering=" );
        builder.append( filtering );
        builder.append( ", contactTypes=" );
        builder.append( contactTypes );
        builder.append( ", intersectionMask=" );
        builder.append( intersectionMask );
        builder.append( ", contacts=" );
        builder.append( contacts );
        builder.append( "]" );
        return builder.toString();
    }

}
