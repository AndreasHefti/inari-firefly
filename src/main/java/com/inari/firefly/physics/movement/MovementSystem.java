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

import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.entity.EntitySystem.EntityIterator;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;

public final class MovementSystem implements FFSystem, UpdateEventListener {
    
    public static final FFSystemTypeKey<MovementSystem> SYSTEM_KEY = FFSystemTypeKey.create( MovementSystem.class );
    
    private final static Aspects MOVEMENT_ASPECT = EntityComponent.ASPECT_GROUP.createAspects( EMovement.TYPE_KEY );

    private FFContext context;
    private EntitySystem entitySystem;
    private EntityIterator entityIterator;
    private Integrator integrator;
    
    private final MoveEvent moveEvent = new MoveEvent();
    
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
        entityIterator = entitySystem.entities( MOVEMENT_ASPECT );
        integrator = new DummyIntegrator();
        
        context.registerListener( UpdateEvent.TYPE_KEY, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( UpdateEvent.TYPE_KEY, this );
    }

    public final Integrator getIntegrator() {
        return integrator;
    }

    public final void setIntegrator( Integrator integrator ) {
        if ( integrator == null ) {
            this.integrator = new DummyIntegrator();
            return;
        }
        this.integrator = integrator;
    }

    @Override
    public final void update( UpdateEvent event ) {
        moveEvent.entityIds.clear();
        entityIterator.reset();
        while ( entityIterator.hasNext() ) {
            final int entityId = entityIterator.next();
            final EMovement movement = context.getEntityComponent( entityId, EMovement.TYPE_KEY );
            final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
            
            if ( !movement.active || !movement.needsUpdate( event.timer ) ) {
                continue;
            }
            
            final long timeElapsed = context.getTimeElapsed();

            if ( movement.velocity.dx != 0f || movement.velocity.dy != 0f ) {
                integrator.step( movement, transform, timeElapsed );
                moveEvent.add( entityId );
            }

            integrator.integrate( movement, transform, timeElapsed );
        }
        
        context.notify( moveEvent );
    }

}
