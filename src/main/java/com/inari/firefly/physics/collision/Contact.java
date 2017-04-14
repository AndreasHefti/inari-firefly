package com.inari.firefly.physics.collision;

import java.util.ArrayDeque;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.BitMask;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.Disposable;
import com.inari.commons.lang.aspect.Aspect;

public class Contact implements Disposable {
    
    private static final ArrayDeque<Contact> disposedContacts = new ArrayDeque<Contact>();
    
    private int entityId;
    private final Rectangle worldBounds = new Rectangle();
    private final Rectangle intersectionBounds = new Rectangle();
    private final BitMask intersectionMask = new BitMask( 0, 0 );
    Aspect contactType;
    Aspect materialType;

    public final int entityId() {
        return entityId;
    }

    public final Rectangle worldBounds() {
        return worldBounds;
    }

    public final Rectangle intersectionBounds() {
        return intersectionBounds;
    }

    public final BitMask intersectionMask() {
        return intersectionMask;
    }
    
    public final Aspect contactType() {
        return contactType;
    }
    
    public final Aspect materialType() {
        return materialType;
    }
    
    public final boolean hasContact( int x, int y ) {
        if ( GeomUtils.contains( intersectionBounds, x, y ) ) {
            if ( !intersectionMask.isEmpty() ) {
                return intersectionMask.getBit( x - intersectionBounds.x, y - intersectionBounds.y );
            }
            
            return true;
        }
        
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "Contact [entityId=" );
        builder.append( entityId );
        builder.append( ", worldBounds=" );
        builder.append( worldBounds );
        builder.append( ", intersectionBounds=" );
        builder.append( intersectionBounds );
        builder.append( ", intersectionMask=" );
        builder.append( intersectionMask );
        builder.append( ", contactType=" );
        builder.append( contactType );
        builder.append( ", materialType=" );
        builder.append( materialType );
        builder.append( "]" );
        return builder.toString();
    }

    @Override
    public final void dispose() {
        entityId = -1;
        intersectionMask.clearMask();
        worldBounds.clear();
        contactType = null;
        materialType = null;
        intersectionBounds.clear();
        disposedContacts.add( this );
    }
    
    public final static Contact createContact( int entityId ) {
        Contact contact = disposedContacts.getFirst();
        if ( contact == null ) {
            contact = new Contact();
        }
        
        contact.entityId = entityId;
        return contact;
    }

    public final static Contact createContact( int entityId, Aspect materialType, Aspect contactType, int x, int y, int width, int height ) {
        Contact contact = ( !disposedContacts.isEmpty() )? 
            disposedContacts.pollFirst() :
                new Contact();
        
        contact.entityId = entityId;
        contact.contactType = contactType;
        contact.materialType = materialType;
        contact.worldBounds.x = x;
        contact.worldBounds.y = y;
        contact.worldBounds.width = width;
        contact.worldBounds.height = height;
        return contact;
    }

}

