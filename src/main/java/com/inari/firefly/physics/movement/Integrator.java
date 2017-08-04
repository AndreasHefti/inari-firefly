package com.inari.firefly.physics.movement;

import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.system.FFContext;

public interface Integrator {
    
    void integrate( FFContext context, EMovement movement, ETransform transform );
    
    void step( FFContext context, EMovement movement, ETransform transform );

}
