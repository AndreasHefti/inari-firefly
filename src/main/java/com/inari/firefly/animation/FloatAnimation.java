package com.inari.firefly.animation;


public abstract class FloatAnimation extends Animation {
    
    protected FloatAnimation( int id ) {
        super( id );
    }

    public abstract float get( int entityId, float currentValue );

}
