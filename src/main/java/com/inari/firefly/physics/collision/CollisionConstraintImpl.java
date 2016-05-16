package com.inari.firefly.physics.collision;

import java.util.BitSet;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.graphics.tile.TileGrid;
import com.inari.firefly.graphics.tile.TileGrid.TileIterator;
import com.inari.firefly.graphics.tile.TileGridSystem;
import com.inari.firefly.physics.movement.EMovement;

public final class CollisionConstraintImpl extends CollisionConstraint {
    
    private CollisionSystem collisionSystem;
    private TileGridSystem tileGridSystem;
    
    private final Rectangle tmpTileGridBounds = new Rectangle();
    private final TmpContact tmpContact = new TmpContact();

    protected CollisionConstraintImpl( int id ) {
        super( id );
    }

    @Override
    public void init() throws FFInitException {
        super.init();
        collisionSystem = context.getSystem( CollisionSystem.SYSTEM_KEY );
        tileGridSystem = context.getSystem( TileGridSystem.SYSTEM_KEY );
    }
    
    @Override
    public void checkCollisions( int entityId, boolean updte ) {
        ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
        ECollision collision = context.getEntityComponent( entityId, ECollision.TYPE_KEY );
        EMovement movement = context.getEntityComponent( entityId, EMovement.TYPE_KEY );

        tmpContact.dispose();
        tmpContact.movingEntityId = entityId;
        tmpContact.setMovingWorldBounds( transform.getXpos(), transform.getYpos(), collision.bounding, movement.getVelocityX() );

        final int viewId = transform.getViewId();
       
        tmpTileGridBounds.setFrom( tmpContact.movingWorldBounds );
        if ( collision.collisionLayerIds != null ) {
            final IntIterator iterator = collision.collisionLayerIds.iterator();
            while ( iterator.hasNext() ) {
                final int layerId = iterator.next();
                checkTileCollision( viewId, layerId, collision );
                checkSpriteCollision( viewId, layerId, collision );
            }
        }
        
    }

    private void checkSpriteCollision( final int viewId, final int layerId, final ECollision collision1 ) {
        final CollisionQuadTree quadTree = collisionSystem.getCollisionQuadTree( viewId, layerId );
        if ( quadTree == null ) {
            return;
        }
        
        IntIterator entityIterator = quadTree.get( tmpContact.movingEntityId );
        if ( entityIterator == null ) {
            return;
        }
        
        while ( entityIterator.hasNext() ) {
            final int entityId2 = entityIterator.next();
            if ( tmpContact.movingEntityId == entityId2 ) {
                continue;
            }
            
            ETransform transform = context.getEntityComponent( entityId2, ETransform.TYPE_KEY );
            ECollision collision2 = context.getEntityComponent( entityId2, ECollision.TYPE_KEY );
            tmpContact.contactEntityId = entityId2;
            tmpContact.setCollisionWorldBounds( transform.getXpos(), transform.getYpos(), collision2.bounding );
            tmpContact.solid = collision2.solid;
            tmpContact.contactType = collision2.contactType;
            
            if ( check( collision1, collision2 ) ) {
                collision1.addContact( ContactProvider.createContact( tmpContact ) );
            } 
        }
    }

    private final void checkTileCollision( final int viewId, final int layerId, final ECollision collision1 ) {
        TileGrid tileGrid = tileGridSystem.getTileGrid( viewId, layerId );
        if ( tileGrid == null ) {
            return;
        }
        
        TileIterator tileIterator = tileGrid.iterator( tmpTileGridBounds );
        if ( tileIterator == null || !tileIterator.hasNext() ) {
            return;
        }
        
        while ( tileIterator.hasNext() ) {
            int tileId = tileIterator.next();
            if ( !context.getEntityComponentAspects( tileId ).contains( ECollision.TYPE_KEY ) ) {
                continue;
            }
            
            ECollision collision2 = context.getEntityComponent( tileId, ECollision.TYPE_KEY );
            tmpContact.contactEntityId = tileId;
            tmpContact.setCollisionWorldBounds( tileIterator.getWorldXPos(), tileIterator.getWorldYPos(), collision2.bounding );
            tmpContact.solid = collision2.solid;
            tmpContact.contactType = collision2.contactType;
            
            if ( check( collision1, collision2 ) ) {
                collision1.addContact( ContactProvider.createContact( tmpContact ) );
            }
        }
    }

    private final boolean check( ECollision collision1, ECollision collision2 ) {
        tmpContact.intersectionMask = null;
        GeomUtils.intersection( 
            tmpContact.movingWorldBounds, 
            tmpContact.contactWorldBounds, 
            tmpContact.intersectionBounds 
        );
        
        if ( tmpContact.intersectionBounds.area() <= 0 ) {
            return false;
        }
        
        // normalize the intersection  to origin of coordinate system
        tmpContact.intersectionBounds.x -= tmpContact.movingWorldBounds.x;
        tmpContact.intersectionBounds.y -= tmpContact.movingWorldBounds.y;

        final BitMask bitmask1 = ( collision1.bitmaskId < 0 )? null : collisionSystem.bitmasks.get( collision1.bitmaskId );
        final BitMask bitmask2 = ( collision2.bitmaskId < 0 )? null : collisionSystem.bitmasks.get( collision2.bitmaskId );
        if ( bitmask1 == null && bitmask2 == null ) {
            return true;
        }
        
        int xOffset = tmpContact.movingWorldBounds.x - tmpContact.contactWorldBounds.x;
        int yOffset = tmpContact.movingWorldBounds.y - tmpContact.contactWorldBounds.y;
        
        if ( bitmask1 != null && bitmask2 != null && bitmask2.intersects( xOffset, yOffset, bitmask1 ) ) {
            tmpContact.intersectionMask = bitmask2.intersectionMask;
            return true;
        }
        
        if ( bitmask1 == null && bitmask2 != null && bitmask2.intersectsRegion( xOffset, yOffset, tmpContact.movingWorldBounds.width, tmpContact.movingWorldBounds.height ) ) {
            tmpContact.intersectionMask = bitmask2.intersectionMask;
            return true;
        }
        
        if ( bitmask1 != null && bitmask2 == null && bitmask1.intersects( -xOffset, -yOffset, tmpContact.movingWorldBounds ) ) {
            tmpContact.intersectionMask = bitmask1.intersectionMask;
            return true;
        }
        
        return false;
    }
    
    
     static final class TmpContact implements Contact {
        int movingEntityId;
        int contactEntityId;
        final Rectangle movingWorldBounds = new Rectangle();
        final Rectangle contactWorldBounds = new Rectangle();
        final Rectangle intersectionBounds = new Rectangle();
        BitSet intersectionMask;
        Aspect contactType;
        boolean solid = false;
        
        @Override public final int movingEntityId() { return movingEntityId; }
        @Override public final int contactEntityId() { return contactEntityId; }
        @Override public final Rectangle movingWorldBounds() { return movingWorldBounds; }
        @Override public final Rectangle contactWorldBounds() { return contactWorldBounds; }
        @Override public final Rectangle intersectionBounds() { return intersectionBounds; }
        @Override public final BitSet intersectionMask() { return intersectionMask; }
        @Override public final Aspect contactType() { return contactType; }
        @Override public final boolean isSolid() { return solid; }
        @Override public final boolean valid() { return false; }
        
        @Override public final void dispose() { 
            movingEntityId = -1; 
            contactEntityId = -1; 
            intersectionMask = null; 
            contactType = null; 
            solid = false; 
        }
        
        public final void setMovingWorldBounds( final float worldPosX, final float worldPosY, final Rectangle collisionBounding, final float xVelocity ) {
            movingWorldBounds.x = ( xVelocity > 0 )? (int) Math.ceil( worldPosX ) : (int) Math.floor( worldPosX ) + collisionBounding.x;
            movingWorldBounds.y = (int) Math.floor( worldPosY ) + collisionBounding.y;
            movingWorldBounds.width = collisionBounding.width;
            movingWorldBounds.height = collisionBounding.height;
        }
        
        public final void setCollisionWorldBounds( final float worldPosX, final float worldPosY, final Rectangle collisionBounding ) {
            contactWorldBounds.x = (int) Math.floor( worldPosX ) + collisionBounding.x;
            contactWorldBounds.y = (int) Math.floor( worldPosY ) + collisionBounding.y;
            contactWorldBounds.width = collisionBounding.width;
            contactWorldBounds.height = collisionBounding.height;
        }   
        
    };

}
