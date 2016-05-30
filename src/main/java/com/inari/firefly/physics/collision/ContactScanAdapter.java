package com.inari.firefly.physics.collision;

import java.util.BitSet;
import java.util.Iterator;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.system.FFContext;

public abstract class ContactScanAdapter implements ContactScan {
    
    protected final FFContext context;
    
    private int entityId;
    private final Rectangle bounds = new Rectangle();
    private final DynArray<Contact> contacts = new DynArray<Contact>();
    
    protected boolean solidContacts = false;
    protected final BitSet contactAspects = new BitSet();
    protected final BitSet intersectionMask = new BitSet();
    
    public ContactScanAdapter( FFContext context ) {
        this.context = context;
        entityId = -1;
        bounds.x = 0;
        bounds.y = 0;
        bounds.width = 0;
        bounds.height = 0;
    }
    
    public ContactScanAdapter( FFContext context, int entityId ) {
        this.context = context;
        this.entityId = entityId;
        bounds.x = 0;
        bounds.y = 0;
        bounds.width = 0;
        bounds.height = 0;
    }

    public ContactScanAdapter( FFContext context, int entityId, Rectangle bounds ) {
        this.context = context;
        this.entityId = entityId;
        this.bounds.setFrom( bounds );
    }

    @Override
    public final int getEntityId() {
        return entityId;
    }

    public final void setEntityId( int entityId ) {
        this.entityId = entityId;
    }

    @Override
    public final Rectangle getBounds() {
        return bounds;
    }
    
    public final void setBounds( Rectangle bounds ) {
        this.bounds.setFrom( bounds );
    }
    
    @Override
    public final BitSet getIntersectionMask() {
        return intersectionMask;
    }

    @Override
    public boolean hasAnyContact() {
        return !contacts.isEmpty();
    }

    @Override
    public boolean hasSolidContact() {
        return solidContacts;
    }

    @Override
    public boolean hasContact( Aspect contact ) {
        return contactAspects.get( contact.index() );
    }

    @Override
    public final Iterator<Contact> getContacts() {
        return contacts.iterator();
    }

    @Override
    public final Contact getFirstContact( Aspect contactType ) {
        for ( Contact contact : contacts ) {
            if ( contact.contactType() == contactType ) {
                return contact;
            }
        }
        
        return null;
    }

    @Override
    public void clearContacts() {
         contacts.clear();
         contactAspects.clear();
         intersectionMask.clear();
         solidContacts = false;
    }

    @Override
    public final void addContact( int xOffset, int yOffset, int contactEntityId, ECollision contactCollision ) {
        Contact contact = checkContact( xOffset, yOffset, contactEntityId, contactCollision );
        if ( contact != null ) {
            if ( !solidContacts && contact.isSolid() ) {
                solidContacts = true;
            }
            Aspect contactType = contact.contactType();
            if ( contactType != null ) {
                contactAspects.set( contactType.index() );
            }
            
//            BitSet interMask = contact.intersectionMask();
//            Rectangle intersectionBounds = contact.intersectionBounds();
//            if ( interMask != null ) {
//                
//            }
            
            contacts.add( ContactProvider.createContact( contact ) );
        }
    }

    protected abstract Contact checkContact( int xOffset, int yOffset, int contactEntityId, ECollision contactCollision );


}
