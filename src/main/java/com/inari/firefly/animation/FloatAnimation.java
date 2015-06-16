package com.inari.firefly.animation;


public abstract class FloatAnimation extends Animation {
    
    protected FloatAnimation( int id ) {
        super( id );
    }
    
    @Override
    public final Class<FloatAnimation> getIndexedObjectType() {
        return FloatAnimation.class;
    }

    public abstract float get( int entityId, float currentValue );

}
