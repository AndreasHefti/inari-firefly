package com.inari.firefly.animation;


public abstract class ValueAnimation<V> extends Animation {

    protected ValueAnimation( int id ) {
        super( id );
    }
    
    public abstract V get( int entityId, V currentValue );

}
