package com.inari.firefly.physics.collision;

import java.util.ArrayDeque;
import java.util.BitSet;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspect;

public abstract class ContactProvider {
    
    private static final ArrayDeque<ContactImpl> disposedContacts = new ArrayDeque<ContactImpl>();
    
    public final static Contact createContact( 
        int movingEntityId,
        int contactEntityId,
        final Rectangle movingWorldBounds,
        final Rectangle contactWorldBounds,
        final Rectangle intersectionBounds,
        BitSet intersectionMask,
        Aspect contactType,
        boolean solid
    ) {
        ContactImpl contact = disposedContacts.getFirst();
        if ( contact == null ) {
            contact = new ContactImpl();
        }
        
        contact.movingEntityId = movingEntityId;
        contact.contactEntityId = contactEntityId;
        contact.movingWorldBounds.setFrom( movingWorldBounds );
        contact.contactWorldBounds.setFrom( contactWorldBounds );
        contact.intersectionBounds.setFrom( intersectionBounds );
        contact.intersectionMask = intersectionMask;
        contact.contactType = contactType;
        contact.solid = solid;
        
        return contact;
    }
    
    public final static Contact createContact( Contact other ) {
        ContactImpl contact = ( !disposedContacts.isEmpty() )?  disposedContacts.pollFirst() : new ContactImpl();
        
        contact.movingEntityId = other.movingEntityId();
        contact.contactEntityId = other.contactEntityId();
        contact.movingWorldBounds.setFrom( other.movingWorldBounds() );
        contact.contactWorldBounds.setFrom( other.contactWorldBounds() );
        contact.intersectionBounds.setFrom( other.intersectionBounds() );
        contact.intersectionMask = other.intersectionMask();
        contact.contactType = other.contactType();
        contact.solid = other.isSolid();
        
        return contact;
    }
    
    
    final static class ContactImpl implements Contact {
        
        int movingEntityId;
        int contactEntityId;
        final Rectangle movingWorldBounds = new Rectangle();
        final Rectangle contactWorldBounds = new Rectangle();
        final Rectangle intersectionBounds = new Rectangle();
        BitSet intersectionMask;
        Aspect contactType;
        boolean solid = false;

        @Override
        public final int movingEntityId() {
            return movingEntityId;
        }

        @Override
        public final int contactEntityId() {
            return contactEntityId;
        }

        @Override
        public final Rectangle movingWorldBounds() {
            return movingWorldBounds;
        }

        @Override
        public final Rectangle contactWorldBounds() {
            return contactWorldBounds;
        }

        @Override
        public final Rectangle intersectionBounds() {
            return intersectionBounds;
        }

        @Override
        public final BitSet intersectionMask() {
            return intersectionMask;
        }
        
        @Override
        public final Aspect contactType() {
            return contactType;
        }

        @Override
        public final boolean isSolid() {
            return solid;
        }
        
        @Override
        public final boolean valid() {
            return movingEntityId >= 0;
        }

        @Override
        public final String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append( "ContactImpl [movingEntityId=" );
            builder.append( movingEntityId );
            builder.append( ", contactEntityId=" );
            builder.append( contactEntityId );
            builder.append( ", movingWorldBounds=" );
            builder.append( movingWorldBounds );
            builder.append( ", contactWorldBounds=" );
            builder.append( contactWorldBounds );
            builder.append( ", intersectionBounds=" );
            builder.append( intersectionBounds );
            builder.append( ", intersectionMask=" );
            builder.append( intersectionMask );
            builder.append( ", contactType=" );
            builder.append( contactType );
            builder.append( ", solid=" );
            builder.append( solid );
            builder.append( "]" );
            return builder.toString();
        }

        @Override
        public final void dispose() {
            movingEntityId = -1;
            contactEntityId = -1;
            intersectionMask = null;
            solid = false;
            
            disposedContacts.add( this );
        }
    }
}
