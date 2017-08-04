package com.inari.firefly.physics.movement;

import com.inari.firefly.graphics.ETransform;

public final class EulerIntegration implements Integrator {
    
    private float gravity = 9.8f;
    private float massFactor = 0.5f;
    private float maxGravityVelocity = 100f;

    public final float getGravity() {
        return gravity;
    }

    public final void setGravity( float gravity ) {
        this.gravity = gravity;
    }

    public final float getMaxGravityVelocity() {
        return maxGravityVelocity;
    }

    public final void setMaxGravityVelocity( float maxGravityVelocity ) {
        this.maxGravityVelocity = maxGravityVelocity;
    }

    public final float getMassFactor() {
        return massFactor;
    }

    public final void setMassFactor( float massFactor ) {
        this.massFactor = massFactor;
    }

    @Override
    public final void integrate( final EMovement movement, final ETransform transform, final float deltaTimeInSeconds ) {
        gravityIntegration( movement );
        
        movement.setVelocityX( movement.getVelocityX() +  movement.getAccelerationX() * deltaTimeInSeconds );
        movement.setVelocityY( movement.getVelocityY() +  movement.getAccelerationY() * deltaTimeInSeconds );
    }

    private void gravityIntegration( final EMovement movement ) {
        if ( movement.getVelocityY() > maxGravityVelocity ) {
            movement.setAccelerationY( 0f );
            return;
        }
        
        if ( movement.onGround ) {
            if ( movement.getVelocityY() != 0f ) {
                movement.setVelocityY( 0f );
            }
            if ( movement.getAccelerationY() != 0f ) {
                movement.setAccelerationY( 0f );
            }
        } else {
            movement.setAccelerationY( gravity * ( movement.mass * massFactor ) );
        }
    }

    @Override
    public final void step( final EMovement movement, final ETransform transform, final float deltaTimeInSeconds ) {
        transform.move( 
            movement.getVelocityX() * deltaTimeInSeconds, 
            movement.getVelocityY() * deltaTimeInSeconds 
        );
    }

}
