package com.inari.firefly.physics.gravity;

public interface GravityFunction {
    
    float calcVelocity( float currentVelocity, float mass, long duration );

}
