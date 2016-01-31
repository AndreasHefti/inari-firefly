/*******************************************************************************
 * Copyright (c) 2015 - 2016, Andreas Hefti, inarisoft@yahoo.de 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/ 
package com.inari.firefly.physics.collision;

import java.util.BitSet;

import com.inari.commons.GeomUtils;
import com.inari.commons.event.Event;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.list.DynArray;

public final class CollisionEvent extends Event<CollisionEventListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( CollisionEvent.class );
    
    int movedEntityId;
    
    CollisionData maxWithData;
    CollisionData maxHeightData;
    
    int size = 0;
    final DynArray<CollisionData> collisionData = new DynArray<CollisionData>();
    

    CollisionEvent() {
        super( TYPE_KEY );
    }

    public final int getMovedEntityId() {
        return movedEntityId;
    }
    
    public final int size() {
        return size;
    }

    public final CollisionData getCollisionData( int index ) {
        if ( collisionData.contains( index ) ) {
            return collisionData.get( index );
        }
        
        return null;
    }
    
    public final CollisionData getMaxWidthCollisionData() {
        return maxWithData;
    }
    
    public final CollisionData getMaxHeightCollisionData() {
        return maxHeightData;
    }
    
    final Rectangle setIntersection( final Rectangle bounds1, final Rectangle bounds2 ) {
        if ( !collisionData.contains( size ) ) {
            collisionData.set( size, new CollisionData() );
        }
        
        CollisionData cd = collisionData.get( size );
        GeomUtils.intersection( bounds1, bounds2, cd.intersectionBounds );
        cd.intersectionBounds.x -= bounds1.x;
        cd.intersectionBounds.y -= bounds1.y;
        
        if ( maxWithData == null || maxWithData.intersectionBounds.width <= cd.intersectionBounds.width ) {
            maxWithData = cd;
        } 
        if ( maxHeightData == null || maxHeightData.intersectionBounds.height <= cd.intersectionBounds.height ) {
            maxHeightData = cd;
        } 
        
        return cd.intersectionBounds;
    }
    
    
    
    final void add( final int collidingEntityId, final BitSet intersectionMask ) {
        CollisionData cd = collisionData.get( size );
        cd.collidingEntityId = collidingEntityId;
        cd.intersectionMask = intersectionMask;

        size++;
    }
    
    final void clear( int movedEntityId ) {
        this.movedEntityId = movedEntityId;
        size = 0; 
        maxWithData = null;
        maxHeightData = null;
        for ( CollisionData cd : collisionData ) {
            cd.clear();
        }
    }

    @Override
    public final void notify( final CollisionEventListener listener ) {
        listener.onCollisionEvent( this );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "CollisionEvent [movedEntityId=" );
        builder.append( movedEntityId );
        builder.append( ", " );
        builder.append( collisionData );
        builder.append( "]" );
        return builder.toString();
    }
    
    public final class CollisionData {
        
        public int collidingEntityId;
        public Rectangle intersectionBounds = new Rectangle();
        public BitSet intersectionMask;
        
        CollisionData() {
            clear();
        }
        
        final void clear() {
            collidingEntityId = -1;
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
            builder.append( collidingEntityId );
            builder.append( ", intersectionBounds=" );
            builder.append( intersectionBounds );
            builder.append( ", intersectionMask=" );
            builder.append( intersectionMask );
            builder.append( "]" );
            return builder.toString();
        }
    }

}
