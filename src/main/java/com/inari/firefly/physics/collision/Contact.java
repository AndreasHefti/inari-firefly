package com.inari.firefly.physics.collision;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.BitMask;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspect;

public class Contact {

    int entityId;
    final Rectangle worldBounds = new Rectangle();
    final Rectangle intersectionBounds = new Rectangle();
    final BitMask intersectionMask = new BitMask( 0, 0 );
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
    
    public final boolean intersects( int x, int y ) {
        return GeomUtils.contains( intersectionBounds, x, y );
    }
    
    public final boolean hasContact( int x, int y ) {
        if ( GeomUtils.contains( intersectionBounds, x, y ) ) {
            if ( !intersectionMask.isEmpty() ) {
                final Rectangle region = intersectionMask.region();
                return intersectionMask.getBit( x - region.x, y - region.y );
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

}

