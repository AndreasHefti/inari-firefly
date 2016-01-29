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
package com.inari.firefly.physics.movement;

import com.inari.commons.geom.Orientation;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.aspect.AspectBitSet;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.commons.lang.indexed.IndexedTypeAspectBuilder;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityComponent.EntityComponentTypeKey;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;

public final class MovementSystem implements FFSystem, UpdateEventListener {
    
    public static final FFSystemTypeKey<MovementSystem> SYSTEM_KEY = FFSystemTypeKey.create( MovementSystem.class );
    
    private final static AspectBitSet MOVEMENT_ASPECT = IndexedTypeAspectBuilder.build( EntityComponentTypeKey.class, EMovement.class );

    private FFContext context;
    private EntitySystem entitySystem;
    
    private final MoveEvent moveEvent = new MoveEvent();

    MovementSystem() {
    }
    
    @Override
    public IIndexedTypeKey indexedTypeKey() {
        return SYSTEM_KEY;
    }

    @Override
    public FFSystemTypeKey<MovementSystem> systemTypeKey() {
        return SYSTEM_KEY;
    }
    
    @Override
    public void init( FFContext context ) {
        this.context = context;
        
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        
        context.registerListener( UpdateEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( UpdateEvent.class, this );
    }

    @Override
    public final void update( UpdateEvent event ) {
        moveEvent.entityIds.clear();
        IntIterator entities = entitySystem.entities( MOVEMENT_ASPECT );
        while ( entities.hasNext() ) {
            int entityId = entities.next();
            IndexedTypeSet components = entitySystem.getComponents( entityId );
            EMovement movement = components.get( EMovement.TYPE_KEY );
            if ( !movement.active || !movement.needsUpdate( event.timer ) ) {
                continue;
            }

            if ( !calculateMovement( movement ) ) {
                continue;
            }
            
            ETransform transform = components.get( ETransform.TYPE_KEY );
            transform.move( movement.velocity.dx, movement.velocity.dy );

            moveEvent.add( entityId );
        }
        context.notify( moveEvent );
    }
    
    private boolean calculateMovement( final EMovement movement ) {
        if ( ( movement.velocity.dx < 0 && movement.hasContact( Orientation.WEST ) ) || 
             ( movement.velocity.dx > 0 && movement.hasContact( Orientation.EAST ) ) ) {
            movement.velocity.dx = 0;
        } 
        if ( ( movement.velocity.dy < 0 && movement.hasContact( Orientation.NORTH ) ) || 
             ( movement.velocity.dy > 0 && movement.hasContact( Orientation.SOUTH ) ) ) {
            movement.velocity.dy = 0;
        } 
           
        return movement.velocity.dx != 0 || movement.velocity.dy != 0;
    }

}
