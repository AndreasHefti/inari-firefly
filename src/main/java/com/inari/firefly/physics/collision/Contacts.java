package com.inari.firefly.physics.collision;

import com.inari.commons.geom.BitMask;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.DynArrayRO; 

public final class Contacts  {
    
    final int constraintId;
    
    final Rectangle normalizedContactBounds;
    final Rectangle worldBounds;
    
    final Aspects contactTypes;
    final Aspects materialTypes;
    final BitMask intersectionMask;
    
    final DynArray<Contact> contacts;
    
    Contacts( int constraintId ) {
        this.constraintId = constraintId;
        normalizedContactBounds = new Rectangle( 0, 0, 0, 0 );
        worldBounds = new Rectangle();
        contactTypes = CollisionSystem.CONTACT_ASPECT_GROUP.createAspects();
        materialTypes = CollisionSystem.MATERIAL_ASPECT_GROUP.createAspects();
        intersectionMask = new BitMask( 0, 0 );
        contacts = DynArray.create( Contact.class, 5, 5 );
    }
    
    final void update( final Rectangle contactBounds, float x, float y, float vx, float vy ) {
        clear();
        
        normalizedContactBounds.width = contactBounds.width;
        normalizedContactBounds.height = contactBounds.height;
        
        worldBounds.x = ( ( vx > 0 )? (int) Math.ceil( x ) : (int) Math.floor( x ) ) + contactBounds.x;
        worldBounds.y = ( ( vy > 0 )? (int) Math.ceil( y ) : (int) Math.floor( y ) ) + contactBounds.y;
        worldBounds.width = contactBounds.width;
        worldBounds.height = contactBounds.height;
        intersectionMask.reset( 0, 0, contactBounds.width, contactBounds.height );
    }
    
    public final int width() {
        return normalizedContactBounds.width;
    }
    
    public final int height() {
        return normalizedContactBounds.height;
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
    
    public final DynArrayRO<Contact> allContacts() {
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

    public final Contact getFirstContactOfType( final Aspect contactType ) {
        for ( int i = 0; i < contacts.capacity(); i++ ) {
            Contact contact = contacts.get( i );
            if ( contact == null ) {
                continue;
            }
            
            if ( contact.contactType == contactType ) {
                return contact;
            }
        }
        
        return null;
    }
    
    public final Contact getFirstContactOfMaterial( final Aspect materialType ) {
        for ( int i = 0; i < contacts.capacity(); i++ ) {
            Contact contact = contacts.get( i );
            if ( contact == null ) {
                continue;
            }
            
            if ( contact.materialType == materialType ) {
                return contact;
            }
        }
        
        return null;
    }

    public final void clear() {
        for ( int i = 0; i < contacts.capacity(); i++ ) {
            Contact contact = contacts.get( i );
            if ( contact == null ) {
                continue;
            }
            
            CollisionSystem.disposeContact( contact );
        }
        contacts.clear();
        contactTypes.clear();
        materialTypes.clear();
        intersectionMask.clearMask();
    }

    public String toString() {
        return "Contacts [normalizedContactBounds=" + normalizedContactBounds + ", worldBounds=" + worldBounds
                + ", contactTypes=" + contactTypes + ", materialTypes=" + materialTypes + ", intersectionMask="
                + intersectionMask + ", contacts=" + contacts + "]";
    }

}
