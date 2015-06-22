package com.inari.firefly.animation;


public abstract class IntAnimation extends Animation {

    protected IntAnimation( int id ) {
        super( id );
    }

    public abstract int get( int component, int currentValue );

}
