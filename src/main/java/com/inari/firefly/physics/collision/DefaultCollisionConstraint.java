package com.inari.firefly.physics.collision;

import java.util.BitSet;
import java.util.Iterator;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.graphics.tile.TileGrid;
import com.inari.firefly.graphics.tile.TileGrid.TileIterator;
import com.inari.firefly.graphics.tile.TileGridSystem;
import com.inari.firefly.physics.collision.DefaultCollisionConstraint.CollisionsImpl.CollisionDataImpl;

public final class DefaultCollisionConstraint extends CollisionConstraint {
    
    private CollisionSystem collisionSystem;
    private TileGridSystem tileGridSystem;
    
    private final Rectangle tmpTileGridBounds = new Rectangle();
    private final CollisionsImpl collisions = new CollisionsImpl( this );

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
    public CollisionsImpl checkCollisions( int entityId ) {
        ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
        ECollision collision = context.getEntityComponent( entityId, ECollision.TYPE_KEY );

        collisions.clear();
        collisions.movingEntityId = entityId;
        collisions.setWorldBounds( transform.getXpos(), transform.getYpos(), collision.bounding, collisions.worldBounds );

        final int viewId = transform.getViewId();

        tmpTileGridBounds.x = collisions.worldBounds.x;
        tmpTileGridBounds.y = collisions.worldBounds.y;
        tmpTileGridBounds.width = collisions.worldBounds.width;
        tmpTileGridBounds.height = collisions.worldBounds.height;

        if ( collision.collisionLayerIds != null ) {
            final IntIterator iterator = collision.collisionLayerIds.iterator();
            while ( iterator.hasNext() ) {
                final int layerId = iterator.next();
                checkTileCollision( viewId, layerId, collision );
                checkSpriteCollision( viewId, layerId, collision );
            }
        }
        
        return collisions;
    }

    private void checkSpriteCollision( final int viewId, final int layerId, final ECollision collision1 ) {
        final CollisionQuadTree quadTree = collisionSystem.getCollisionQuadTree( viewId, layerId );
        if ( quadTree == null ) {
            return;
        }
        
        IntIterator entityIterator = quadTree.get( collisions.movingEntityId );
        if ( entityIterator == null ) {
            return;
        }
        
        while ( entityIterator.hasNext() ) {
            final int entityId2 = entityIterator.next();
            if ( collisions.movingEntityId == entityId2 ) {
                continue;
            }
            
            ETransform transform = context.getEntityComponent( entityId2, ETransform.TYPE_KEY );
            ECollision collision2 = context.getEntityComponent( entityId2, ECollision.TYPE_KEY );
            final CollisionDataImpl collisionData = collisions.create( 
                entityId2, 
                transform.getXpos(), 
                transform.getYpos(), 
                collision2.bounding 
            );
            
            if ( check( collision1, collision2, collisionData ) ) {
                collisions.next();
            } else {
                collisionData.clear();
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
            if ( !context.getEntityAspects( tileId ).contains( ECollision.TYPE_KEY ) ) {
                continue;
            }
            
            ECollision collision2 = context.getEntityComponent( tileId, ECollision.TYPE_KEY );
            final CollisionDataImpl collisionData = collisions.create( 
                tileId, 
                tileIterator.getWorldXPos(), 
                tileIterator.getWorldYPos(), 
                collision2.bounding 
            );

            if ( check( collision1, collision2, collisionData ) ) {
                collisions.next();
            } else {
                collisionData.clear();
            }
        }
    }

    private final boolean check( ECollision collision1, ECollision collision2, CollisionDataImpl collisionData ) {
        if ( !collision1.isSolid() || !collision2.isSolid() ) {
            return false;
        }
        
        GeomUtils.intersection( 
            collisions.worldBounds, 
            collisionData.worldBounds, 
            collisionData.intersectionBounds 
        );
        
        if ( collisionData.intersectionBounds.area() <= 0 ) {
            return false;
        }
        
        // normalize the intersection  to origin of coordinate system
        collisionData.intersectionBounds.x -= collisions.worldBounds.x;
        collisionData.intersectionBounds.y -= collisions.worldBounds.y;

        final BitMask bitmask1 = ( collision1.bitmaskId < 0 )? null : collisionSystem.bitmasks.get( collision1.bitmaskId );
        final BitMask bitmask2 = ( collision2.bitmaskId < 0 )? null : collisionSystem.bitmasks.get( collision2.bitmaskId );
        if ( bitmask1 == null && bitmask2 == null ) {
            return true;
        }
        
        int xOffset = collisions.worldBounds.x - collisionData.worldBounds.x;
        int yOffset = collisions.worldBounds.y - collisionData.worldBounds.y;
        
        if ( bitmask1 != null && bitmask2 != null && bitmask2.intersects( xOffset, yOffset, bitmask1 ) ) {
            collisionData.intersectionMask = bitmask2.intersectionMask;
            return true;
        }
        
        if ( bitmask1 == null && bitmask2 != null && bitmask2.intersectsRegion( xOffset, yOffset, collisions.worldBounds.width, collisions.worldBounds.height ) ) {
            collisionData.intersectionMask = bitmask2.intersectionMask;
            return true;
        }
        
        if ( bitmask1 != null && bitmask2 == null && bitmask1.intersects( -xOffset, -yOffset, collisions.worldBounds ) ) {
            collisionData.intersectionMask = bitmask1.intersectionMask;
            return true;
        }
        
        return false;
    }
    
    
    public final class CollisionsImpl implements Collisions {
    
        private final CollisionConstraint collisionConstraint;
    
        int movingEntityId;
        final Rectangle worldBounds = new Rectangle();
        
        private final DynArray<CollisionDataImpl> collisions = new DynArray<CollisionDataImpl>();
        
        int size = 0;
        
        public CollisionsImpl( CollisionConstraint collisionConstraint ) {
            this.collisionConstraint = collisionConstraint;
        }
        
        @Override
        public final int movingEntityId() {
            return movingEntityId;
        }
        
        @Override
        public final Rectangle worldBounds() {
            return worldBounds;
        }
        
        @Override
        public final void update() {
            if ( movingEntityId >= 0 ) {
                collisionConstraint.checkCollisions( movingEntityId );
            }
        }
    
        @Override
        public final int size() {
            return size;
        }
    
        @Override
        public final Iterator<CollisionData> iterator() {
            return new CollisionDataIterator();
        }
        
        final CollisionDataImpl create( int entityId, float woldPosX, float worldPosY, Rectangle collisionBounding ) {
            if ( !collisions.contains( size ) ) {
                collisions.set( size, new CollisionDataImpl() );
            }
            
            CollisionDataImpl collisionData = collisions.get( size );
            collisionData.entityId = entityId;
            setWorldBounds( woldPosX, worldPosY, collisionBounding, collisionData.worldBounds );
            
            return collisionData;
        }
    
        final void next() {
            size++;
        }
    
        final void clear() {
            size = 0; 
            movingEntityId = -1;
            for ( CollisionDataImpl collision : collisions ) {
                collision.clear();
            }
        }
        
        final void setWorldBounds( final float worldPosX, final float worldPosY, final Rectangle collisionBounding, Rectangle worldBounds ) {
            worldBounds.x = (int) Math.ceil( worldPosX ) + collisionBounding.x;
            worldBounds.y = (int) Math.floor( worldPosY ) + collisionBounding.y;
            worldBounds.width = collisionBounding.width;
            worldBounds.height = collisionBounding.height;
        }    
        
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append( "CollisionData [size=" );
            builder.append( size );
            builder.append( ", collisionData=" );
            builder.append( collisions );
            builder.append( "]" );
            return builder.toString();
        }
    
        public final class CollisionDataImpl implements CollisionData {
            
            int entityId;
            final Rectangle worldBounds = new Rectangle();
            final Rectangle intersectionBounds = new Rectangle();
            BitSet intersectionMask;
            
            CollisionDataImpl() {
                clear();
            }
            
            @Override
            public final int entityId() {
                return entityId;
            }
            
            @Override
            public final Rectangle worldBounds() {
                return worldBounds;
            }
    
            @Override
            public final Rectangle intersectionBounds() {
                return intersectionBounds;
            }
    
            @Override
            public final BitSet intersectionMask() {
                return intersectionMask;
            }
            
            final void clear() {
                entityId = -1;
                intersectionBounds.x = 0;
                intersectionBounds.y = 0;
                intersectionBounds.width = 0;
                intersectionBounds.height = 0;
                intersectionMask = null;
            }
    
            @Override
            public String toString() {
                StringBuilder builder = new StringBuilder();
                builder.append( "CollisionData [entityId=" );
                builder.append( entityId );
                builder.append( ", intersectionBounds=" );
                builder.append( intersectionBounds );
                builder.append( ", intersectionMask=" );
                builder.append( intersectionMask );
                builder.append( "]" );
                return builder.toString();
            }
        }
        
        private final class CollisionDataIterator implements Iterator<CollisionData> {
            
            int index = 0;
            Iterator<CollisionDataImpl> delegate = collisions.iterator();
    
            @Override
            public boolean hasNext() {
                return index < size;
            }
    
            @Override
            public CollisionData next() {
                index++;
                return delegate.next();
            }
    
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
        }
    }

}
