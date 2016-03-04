package com.inari.firefly.physics.collision;

import java.util.BitSet;
import java.util.Iterator;

import com.inari.commons.StringUtils;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.physics.movement.EMovement;

public final class Collisions implements Iterable<Collisions.CollisionData> {
    
    private final CollisionSystem collisionSystem;

    public final EntityData entityData = new EntityData();
    private final DynArray<CollisionData> collisions = new DynArray<CollisionData>();
    
    int size = 0;
    
    Collisions( CollisionSystem collisionSystem ) {
        this.collisionSystem = collisionSystem;
    }
    
    public final void update() {
        if ( entityData.entityId >= 0 ) {
            collisionSystem.checkCollisionOnEntity( entityData.entityId );
        }
    }

    public final int size() {
        return size;
    }

    @Override
    public Iterator<CollisionData> iterator() {
        return new CollisionDataIterator();
    }
    
    CollisionData get() {
        if ( !collisions.contains( size ) ) {
            collisions.set( size, new CollisionData() );
        }
        
        return collisions.get( size );
    }

    void next() {
        size++;
    }

    final void clear() {
        size = 0; 
        entityData.clear();
        for ( CollisionData collision : collisions ) {
            collision.clear();
        }
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
    
    public final class EntityData {
        
        public int entityId;
        public ETransform transform;
        public ECollision collision;
        public EMovement movement;
        
        public final Rectangle worldBounds = new Rectangle();
        
        EntityData() {
        }
        
        final void set( int entityId, ETransform transform, ECollision collision, EMovement movement ) {
            this.entityId = entityId;
            this.transform = transform;
            this.collision = collision;
            this.movement = movement;
            setBounds( 
                transform.getXpos(), 
                transform.getYpos(), 
                ( collision.outerBounding != null )? collision.outerBounding : collision.bounding 
            );
        }
        
        final void set( int entityId, float woldPosX, float worldPosY, ETransform transform, ECollision collision, EMovement movement ) {
            this.entityId = entityId;
            this.transform = transform;
            this.collision = collision;
            this.movement = movement;
            setBounds( 
                woldPosX, 
                worldPosY, 
                ( collision.outerBounding != null )? collision.outerBounding : collision.bounding 
            );
        }

        private final void setBounds( final float worldPosX, final float worldPosY, final Rectangle collisionBounding ) {
            worldBounds.x = (int) Math.ceil( worldPosX + collisionBounding.x );
            worldBounds.y = (int) Math.ceil( worldPosY + collisionBounding.y );
            worldBounds.width = collisionBounding.width;
            worldBounds.height = collisionBounding.height;
        }
        
        final void clear() {
            entityId = -1;
            transform = null;
            collision = null;
            movement = null;
            worldBounds.x = 0;
            worldBounds.y = 0;
            worldBounds.width = 0;
            worldBounds.height = 0;
        }
    }

    public final class CollisionData {
        
        public final EntityData entityData = new EntityData();
        public final Rectangle intersectionBounds = new Rectangle();
        public BitSet intersectionMask;
        
        CollisionData() {
            clear();
        }
        
        final void clear() {
            entityData.clear();
            intersectionBounds.x = 0;
            intersectionBounds.y = 0;
            intersectionBounds.width = 0;
            intersectionBounds.height = 0;
            intersectionMask = null;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append( "CollisionData [collidingEntityId=" );
            builder.append( entityData.entityId );
            builder.append( ", intersectionBounds=" );
            builder.append( intersectionBounds );
            if ( intersectionMask != null ) {
                builder.append( ", intersectionMask=" );
                builder.append( StringUtils.bitsetToString( intersectionMask, intersectionBounds.width, intersectionBounds.height ) );
            }
            builder.append( "]" );
            return builder.toString();
        }

    }
    
    private final class CollisionDataIterator implements Iterator<Collisions.CollisionData> {
        
        int index = 0;
        Iterator<Collisions.CollisionData> delegate = collisions.iterator();

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
