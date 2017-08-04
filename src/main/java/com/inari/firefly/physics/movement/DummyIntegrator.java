package com.inari.firefly.physics.movement;

import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.system.FFContext;

public final class DummyIntegrator implements Integrator {
    
    private float gravity;

    public final float getGravity() {
        return gravity;
    }

    public final void setGravity( float gravity ) {
        this.gravity = gravity;
    }

    @Override
    public final void integrate( FFContext context, EMovement movement, ETransform transform ) {
        if ( movement.onGround ) {
            if ( movement.getVelocityY() != 0f ) {
                movement.setVelocityY( 0f );
            }
            return;
        }
        final float vy = movement.getVelocityY();
        movement.setVelocityY( vy + Math.abs( ( vy / movement.mass - 1f ) * 0.2f ) );
    }

    @Override
    public final void step( FFContext context, EMovement movement, ETransform transform ) {
        System.out.println( "deltaTime: " + context.getTimeElapsed() );
        transform.move( movement.getVelocityX(), movement.getVelocityY() );
    }

}
