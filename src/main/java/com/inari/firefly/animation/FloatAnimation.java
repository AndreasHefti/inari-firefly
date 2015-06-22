package com.inari.firefly.animation;


public abstract class FloatAnimation extends Animation {
    
    protected FloatAnimation( int id ) {
        super( id );
    }

    public abstract float get( int componentId, float currentValue );

}
