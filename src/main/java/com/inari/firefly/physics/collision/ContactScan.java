package com.inari.firefly.physics.collision;

import java.util.BitSet;
import java.util.Iterator;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspect;

public interface ContactScan {
    
    int getEntityId();
    
    Rectangle getBounds();
    
    BitSet getIntersectionMask();
    
    void addContact( final int xOffset, final int yOffset, final int contactEntityId, final ECollision contactCollision );
    
    public boolean hasAnyContact();
    
    public boolean hasSolidContact();

    public boolean hasContact( Aspect contact );
    
    public Iterator<Contact> getContacts();
    
    public Contact getFirstContact( Aspect contactType );
    
    public void clearContacts();

}
