package com.inari.firefly.physics.movement;

import com.inari.firefly.graphics.ETransform;

public final class EulerIntegration implements Integrator {
    
    private float gravity = 9.8f;

    public final float getGravity() {
        return gravity;
    }

    public final void setGravity( float gravity ) {
        this.gravity = gravity;
    }

    @Override
    public final void integrate( final EMovement movement, final ETransform transform, final float deltaTimeInSeconds ) {
        gravityIntegration( movement );
        
        movement.setVelocityX( movement.getVelocityX() + movement.getAccelerationX() * deltaTimeInSeconds );
        movement.setVelocityY( movement.getVelocityY() + movement.getAccelerationY() * deltaTimeInSeconds );
    }

    @Override
    public final void step( final EMovement movement, final ETransform transform, final float deltaTimeInSeconds ) {
        transform.move( 
            movement.getVelocityX() * deltaTimeInSeconds, 
            movement.getVelocityY() * deltaTimeInSeconds 
        );
    }
    
    private void gravityIntegration( final EMovement movement ) {
        if ( movement.onGround ) {
            movement.setVelocityY( 0f );
            movement.setAccelerationY( 0f );
        } else {
            final float maxGravityVelocity = movement.getMaxGravityVelocity();
            final float massFactor = movement.getMassFactor();
            if ( movement.getVelocityY() > maxGravityVelocity ) {
                movement.setAccelerationY( 0f );
                movement.setVelocityY( maxGravityVelocity );
                return;
            }
            movement.setAccelerationY( gravity * ( movement.mass * massFactor ) );
        }
    }

}
