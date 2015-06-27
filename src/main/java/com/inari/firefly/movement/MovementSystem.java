/*******************************************************************************
 * Copyright (c) 2015, Andreas Hefti, inarisoft@yahoo.de 
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
package com.inari.firefly.movement;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.indexed.IndexedAspectBuilder;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.firefly.FFContext;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.Entity;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.entity.IEntitySystem;
import com.inari.firefly.movement.event.MoveEvent;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.event.UpdateEvent;
import com.inari.firefly.system.event.UpdateEventListener;

public final class MovementSystem implements FFSystem, UpdateEventListener {
    
    private final static Aspect MOVEMENT_ASPECT = IndexedAspectBuilder.build( EntityComponent.class, EMovement.class );

    private IEventDispatcher eventDispatcher;
    private IEntitySystem entitySystem;

    MovementSystem() {
    }
    
    @Override
    public void init( FFContext context ) {
        eventDispatcher = context.get( FFContext.EVENT_DISPATCHER );
        entitySystem = context.get( FFContext.System.ENTITY_SYSTEM );
        
        eventDispatcher.register( UpdateEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        eventDispatcher.unregister( UpdateEvent.class, this );
    }

    @Override
    public final void update( UpdateEvent event ) {
        MoveEvent moveEvent = new MoveEvent();
        for ( Entity entity : entitySystem.entities( MOVEMENT_ASPECT ) ) {
            IndexedTypeSet components = entity.getComponents();
            EMovement movement = components.get( EMovement.COMPONENT_TYPE );
            if ( !movement.isMoving() ) {
                continue;
            }
            
            ETransform transform = components.get( ETransform.COMPONENT_TYPE );
            transform.move( movement.getVelocityVector() );
            
            moveEvent.add( entity.getId() );
        }
        eventDispatcher.notify( moveEvent );
    }

}
