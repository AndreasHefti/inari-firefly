package com.inari.firefly.physics.collision;

import com.inari.commons.GeomUtils;
import com.inari.firefly.FFInitException;
import com.inari.firefly.physics.collision.Collisions.CollisionData;
import com.inari.firefly.physics.collision.Collisions.EntityData;

public final class DefaultCollisionConstraint extends CollisionConstraint {
    
    private CollisionSystem collisionSystem;

    protected DefaultCollisionConstraint( int id ) {
        super( id );
    }

    @Override
    public void init() throws FFInitException {
        super.init();
        collisionSystem = context.getSystem( CollisionSystem.SYSTEM_KEY );
    }

    @Override
    public final boolean check( EntityData entityData, CollisionData collisionData ) {
        if ( !entityData.collision.isSolid() || !collisionData.entityData.collision.isSolid() ) {
            return false;
        }
        
        GeomUtils.intersection( 
            entityData.worldBounds, 
            collisionData.entityData.worldBounds, 
            collisionData.intersectionBounds 
        );
        
        if ( collisionData.intersectionBounds.area() <= 0 ) {
            return false;
        }
        
        // normalize the intersection  to origin of coordinate system
        collisionData.intersectionBounds.x -= entityData.worldBounds.x;
        collisionData.intersectionBounds.y -= entityData.worldBounds.y;

        final BitMask bitmask1 = ( entityData.collision.bitmaskId < 0 )? null : collisionSystem.bitmasks.get( entityData.collision.bitmaskId );
        final BitMask bitmask2 = ( collisionData.entityData.collision.bitmaskId < 0 )? null : collisionSystem.bitmasks.get( collisionData.entityData.collision.bitmaskId );
        if ( bitmask1 == null && bitmask2 == null ) {
            return true;
        }
        
        int xOffset = entityData.worldBounds.x - collisionData.entityData.worldBounds.x;
        int yOffset = entityData.worldBounds.y - collisionData.entityData.worldBounds.y;
        
        if ( bitmask1 != null && bitmask2 != null && bitmask2.intersects( xOffset, yOffset, bitmask1 ) ) {
            collisionData.intersectionMask = bitmask2.intersectionMask;
            return true;
        }
        
        if ( bitmask1 == null && bitmask2 != null && bitmask2.intersectsRegion( xOffset, yOffset, entityData.worldBounds.width, entityData.worldBounds.height ) ) {
            collisionData.intersectionMask = bitmask2.intersectionMask;
            return true;
        }
        
        if ( bitmask1 != null && bitmask2 == null && bitmask1.intersects( -xOffset, -yOffset, entityData.worldBounds ) ) {
            collisionData.intersectionMask = bitmask1.intersectionMask;
            return true;
        }
        
        return false;
    }

}
