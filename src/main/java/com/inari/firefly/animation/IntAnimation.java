package com.inari.firefly.animation;


public abstract class IntAnimation extends Animation {

    protected IntAnimation( int id ) {
        super( id );
    }
    
    @Override
    public final Class<IntAnimation> getIndexedObjectType() {
        return IntAnimation.class;
    }

    public abstract long get( int entityId, long currentValue );

}
