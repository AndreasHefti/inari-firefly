package com.inari.firefly.physics.collision;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.IntIterator;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.graphics.tile.TileGrid;
import com.inari.firefly.graphics.tile.TileGrid.TileIterator;
import com.inari.firefly.graphics.tile.TileGridSystem;
import com.inari.firefly.physics.collision.Collisions.CollisionData;
import com.inari.firefly.physics.collision.Collisions.EntityData;
import com.inari.firefly.physics.movement.EMovement;

public final class DefaultCollisionConstraint extends CollisionConstraint {
    
    private CollisionSystem collisionSystem;
    private TileGridSystem tileGridSystem;
    
    private final Rectangle tmpTileGridBounds = new Rectangle();
    private final Collisions collisions = new Collisions( this );

    protected DefaultCollisionConstraint( int id ) {
        super( id );
    }

    @Override
    public void init() throws FFInitException {
        super.init();
        collisionSystem = context.getSystem( CollisionSystem.SYSTEM_KEY );
        tileGridSystem = context.getSystem( TileGridSystem.SYSTEM_KEY );
    }
    
    @Override
    public Collisions checkCollisions( int entityId ) {
        collisions.clear();
        collisions.entityData.entityId = entityId;
        collisions.entityData.set( 
            entityId,
            context.getEntityComponent( entityId, ETransform.TYPE_KEY ), 
            context.getEntityComponent( entityId, ECollision.TYPE_KEY ),
            context.getEntityComponent( entityId, EMovement.TYPE_KEY )
        );

        final int viewId = collisions.entityData.transform.getViewId();

        tmpTileGridBounds.x = collisions.entityData.worldBounds.x;
        tmpTileGridBounds.y = collisions.entityData.worldBounds.y;
        tmpTileGridBounds.width = collisions.entityData.worldBounds.width;
        tmpTileGridBounds.height = collisions.entityData.worldBounds.height;

        if ( collisions.entityData.collision.collisionLayerIds != null ) {
            final IntIterator iterator = collisions.entityData.collision.collisionLayerIds.iterator();
            while ( iterator.hasNext() ) {
                final int layerId = iterator.next();
                checkTileCollision( viewId, layerId );
                checkSpriteCollision( viewId, layerId );
            }
        }
        
        return collisions;
    }

    private void checkSpriteCollision( final int viewId, final int layerId ) {
        final CollisionQuadTree quadTree = collisionSystem.getCollisionQuadTree( viewId, layerId );
        if ( quadTree == null ) {
            return;
        }
        
        IntIterator entityIterator = quadTree.get( collisions.entityData.entityId );
        if ( entityIterator == null ) {
            return;
        }
        
        while ( entityIterator.hasNext() ) {
            final int entityId2 = entityIterator.next();
            if ( collisions.entityData.entityId == entityId2 ) {
                continue;
            }
            
            final CollisionData collisionData = collisions.get();
            collisionData.clear();
            collisionData.entityData.set( 
                entityId2,
                context.getEntityComponent( entityId2, ETransform.TYPE_KEY ), 
                context.getEntityComponent( entityId2, ECollision.TYPE_KEY ),
                context.getEntityComponent( entityId2, EMovement.TYPE_KEY )
            );
            
            if ( check( collisions.entityData, collisionData ) ) {
                collisions.next();
            }
        }
    }

    private final void checkTileCollision( final int viewId, final int layerId ) {
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
            if ( !context.getEntityAspect( tileId ).contains( ECollision.TYPE_KEY ) ) {
                continue;
            }
            
            final CollisionData collisionData = collisions.get();
            collisionData.clear();
            collisionData.entityData.set( 
                tileId,
                tileIterator.getWorldXPos(),
                tileIterator.getWorldYPos(),
                context.getEntityComponent( tileId, ETransform.TYPE_KEY ), 
                context.getEntityComponent( tileId, ECollision.TYPE_KEY ),
                context.getEntityComponent( tileId, EMovement.TYPE_KEY ) 
            );

            if ( check( collisions.entityData, collisionData ) ) {
                collisions.next();
            } else {
                collisionData.clear();
            }
        }
    }

    private final boolean check( EntityData entityData, CollisionData collisionData ) {
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
