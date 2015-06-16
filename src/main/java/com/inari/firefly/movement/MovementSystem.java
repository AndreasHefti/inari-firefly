package com.inari.firefly.movement;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.indexed.IndexedAspectBuilder;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.firefly.Disposable;
import com.inari.firefly.FFContext;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.Entity;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.entity.IEntitySystem;
import com.inari.firefly.movement.event.MoveEvent;
import com.inari.firefly.system.event.UpdateEvent;
import com.inari.firefly.system.event.UpdateEventListener;

public final class MovementSystem implements UpdateEventListener, Disposable {
    
    private final static Aspect MOVEMENT_ASPECT = IndexedAspectBuilder.build( EntityComponent.class, EMovement.class );

    private IEventDispatcher eventDispatcher;
    private IEntitySystem entitySystem;

    MovementSystem(  FFContext context  ) {
        eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
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
