package com.inari.firefly.animation;


public abstract class ValueAnimation<V> extends Animation {

    protected ValueAnimation( int id ) {
        super( id );
    }
    
    @Override
    @SuppressWarnings( "rawtypes" )
    public final Class<ValueAnimation> getIndexedObjectType() {
        return ValueAnimation.class;
    }
    
    public abstract V get( int entityId, V currentValue );

}
