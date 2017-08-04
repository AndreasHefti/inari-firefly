package com.inari.firefly.physics.movement;

import com.inari.firefly.graphics.ETransform;

public class EulerIntegration implements Integrator {
    
    private float gravity = 9.8f;

    public final float getGravity() {
        return gravity;
    }

    public final void setGravity( float gravity ) {
        this.gravity = gravity;
    }

    @Override
    public void integrate( final EMovement movement, final ETransform transform, final long deltaTimeInSeconds ) {
        gravityIntegration( movement );
        
        movement.setVelocityX( movement.getVelocityX() +  movement.getAccelerationX() * deltaTimeInSeconds );
        movement.setVelocityY( movement.getVelocityY() +  movement.getAccelerationY() * deltaTimeInSeconds );
        
    }

    private void gravityIntegration( final EMovement movement ) {
        if ( movement.onGround ) {
            if ( movement.getVelocityY() != 0f ) {
                movement.setVelocityY( 0f );
            }
            if ( movement.getAccelerationY() != 0f ) {
                movement.setAccelerationY( 0f );
            }
        } else {
            movement.setAccelerationY( gravity * movement.mass / 100 );
        }
    }

    @Override
    public void step( final EMovement movement, final ETransform transform, final long deltaTimeInSeconds ) {
        System.out.println( "deltaTimeInSeconds: " + deltaTimeInSeconds + " velx: " + movement.getVelocityX() + " vely: " + movement.getVelocityY()  );
        transform.move( 
            movement.getVelocityX() * deltaTimeInSeconds, 
            movement.getVelocityY() * deltaTimeInSeconds 
        );
    }

}
