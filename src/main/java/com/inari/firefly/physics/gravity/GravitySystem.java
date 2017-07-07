package com.inari.firefly.physics.gravity;

import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.EntityActivationEvent;
import com.inari.firefly.entity.EntityActivationListener;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.physics.movement.EMovement;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;

public final class GravitySystem implements FFSystem, UpdateEventListener, EntityActivationListener {
    
    public static final FFSystemTypeKey<GravitySystem> SYSTEM_KEY = FFSystemTypeKey.create( GravitySystem.class );
    
    private static Aspects MATCHING_ENTITES = EntityComponent.ASPECT_GROUP.createAspects( EEntity.TYPE_KEY, EMovement.TYPE_KEY, EMass.TYPE_KEY );
    
    private GravityFunction gravityFunction;
    private IntBag entities;
    
    private FFContext context;

    GravitySystem() {
        super();
        entities = new IntBag( 10, -1, 20 );
    }

    @Override
    public final IIndexedTypeKey indexedTypeKey() {
        return SYSTEM_KEY;
    }
    
    @Override
    public final FFSystemTypeKey<?> systemTypeKey() {
        return SYSTEM_KEY;
    }

    public final GravityFunction getGravityFunction() {
        return gravityFunction;
    }

    public final void setGravityFunction( final GravityFunction gravityFunction ) {
        this.gravityFunction = gravityFunction;
    }

    @Override
    public final void init( final FFContext context ) throws FFInitException {
        this.context = context;
        context.registerListener( UpdateEvent.TYPE_KEY, this );
        context.registerListener( EntityActivationEvent.TYPE_KEY, this );
    }
    
    @Override
    public final boolean match( final Aspects aspects ) {
        return aspects.include( MATCHING_ENTITES );
    }

    @Override
    public final void entityActivated( int entityId, final Aspects aspects ) {
        entities.add(  entityId );
    }

    @Override
    public final void entityDeactivated( int entityId, final Aspects aspects ) {
        entities.remove( entityId );
    }
    
    @Override
    public final void update( final UpdateEvent event ) {
        final long currentTime = context.getTime();
        for ( int i = 0; i < entities.length(); i++ ) {
            int entityId = entities.get( i );
            if ( entityId < 0 ) {
                continue;
            }
            
            final EEntity entity = context.getEntityComponent( entityId, EEntity.TYPE_KEY );
            final EMass mass = context.getEntityComponent( entityId, EMass.TYPE_KEY );
            final EMovement movement = context.getEntityComponent( entityId, EMovement.TYPE_KEY );
            
            if ( mass.noGravityAspects != null && entity.getAspects().intersects( mass.noGravityAspects ) ) {
                continue;
            }
            
            if ( entity.hasAspect( mass.onGroundAspect ) ) {
                if ( movement.getVelocityY() != 0f ) {
                    mass.fallingStartTime = -1;
                    movement.setVelocityY( 0f );
                }
                continue;
            }
            
            if ( mass.fallingStartTime < 0 ) {
                mass.fallingStartTime = currentTime;
            }

            movement.setVelocityY( 
                gravityFunction.calcVelocity( 
                        movement.getVelocityY(), 
                        mass.mass, 
                        currentTime - mass.fallingStartTime 
                ) 
            );
        }
    }

    @Override
    public final void dispose( final FFContext context ) {
        context.disposeListener( UpdateEvent.TYPE_KEY, this );
        context.disposeListener( EntityActivationEvent.TYPE_KEY, this );
    }

}
