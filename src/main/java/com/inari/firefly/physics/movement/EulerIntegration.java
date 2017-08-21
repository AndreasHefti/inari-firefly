package com.inari.firefly.physics.movement;

import com.inari.firefly.graphics.ETransform;

public final class EulerIntegration implements Integrator {
    
    private float gravity = 9.8f;
    private float shift = (float) Math.pow( 10, 0 );
    private int scale = -1;

    public final float getGravity() {
        return gravity;
    }

    public final void setGravity( float gravity ) {
        this.gravity = gravity;
    }

    public final int getScale() {
        return scale;
    }

    public final void setScale( int scale ) {
        this.scale = scale;
        shift = ( scale < 0 )? 
            (float) Math.pow( 10, 0 ) : 
                (float) Math.pow( 10, scale );
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
            Math.round( movement.getVelocityX() * deltaTimeInSeconds * shift ) / shift, 
            Math.round( movement.getVelocityY() * deltaTimeInSeconds * shift ) / shift 
        );
    }
    
    private void gravityIntegration( final EMovement movement ) {
        if ( movement.onGround ) {
            movement.setVelocityY( 0f );
            movement.setAccelerationY( 0f );
        } else {
            final float maxGravityVelocity = movement.getMaxGravityVelocity();
            final float massFactor = movement.getMassFactor();
            if ( movement.getVelocityY() >= maxGravityVelocity ) {
                movement.setAccelerationY( 0f );
                movement.setVelocityY( maxGravityVelocity );
                return;
            }
            movement.setAccelerationY( gravity * ( movement.mass * massFactor ) );
        }
    }

}
