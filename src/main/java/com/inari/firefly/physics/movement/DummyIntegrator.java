package com.inari.firefly.physics.movement;

import com.inari.firefly.graphics.ETransform;

public final class DummyIntegrator implements Integrator {
    
    private float gravity;

    public final float getGravity() {
        return gravity;
    }

    public final void setGravity( float gravity ) {
        this.gravity = gravity;
    }

    @Override
    public final void integrate( final EMovement movement, final ETransform transform, final long deltaTime ) {
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
    public final void step( final EMovement movement, final ETransform transform, final long deltaTime ) {
        transform.move( movement.getVelocityX(), movement.getVelocityY() );
    }

}
