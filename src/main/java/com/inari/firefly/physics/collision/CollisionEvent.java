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

import com.inari.commons.event.Event;
import com.inari.commons.geom.Rectangle;

public final class CollisionEvent extends Event<CollisionEventListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( CollisionEvent.class );
    
    int movedEntityId;
    int collidingEntityId;
    
    Rectangle collisionIntersectionBounds = new Rectangle();
    BitSet collisionIntersectionMask;

    CollisionEvent() {
        super( TYPE_KEY );
    }

    public final int getMovedEntityId() {
        return movedEntityId;
    }

    public final int getCollidingEntityId() {
        return collidingEntityId;
    }

    public final Rectangle getCollisionIntersectionBounds() {
        return collisionIntersectionBounds;
    }

    public final BitSet getCollisionIntersectionMask() {
        return collisionIntersectionMask;
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
        builder.append( ", collidingEntityId=" );
        builder.append( collidingEntityId );
        builder.append( ", collisionIntersectionBounds=" );
        builder.append( collisionIntersectionBounds );
        builder.append( ", collisionIntersectionMask=" );
        builder.append( collisionIntersectionMask );
        builder.append( "]" );
        return builder.toString();
    }

}
