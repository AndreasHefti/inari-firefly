package com.inari.firefly.animation;

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.Disposable;
import com.inari.firefly.FFContext;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.system.event.UpdateEvent;
import com.inari.firefly.system.event.UpdateEventListener;

public final class AnimationSystem implements ComponentBuilderFactory, UpdateEventListener, Disposable {
    
    private final DynArray<FloatAnimation> floatAnimations = new DynArray<FloatAnimation>();
    private final DynArray<IntAnimation> intAnimations = new DynArray<IntAnimation>();
    private final DynArray<ValueAnimation<?>> valueAnimations = new DynArray<ValueAnimation<?>>();

    AnimationSystem( FFContext context ) {
    }
    
    @Override
    public void dispose( FFContext context ) {
        floatAnimations.clear();
    }

    @Override
    public final void update( UpdateEvent event ) {
        long updateTime = event.getUpdate();
        for ( Animation animation : floatAnimations ) {
            updateAnimation( updateTime, animation );
        }
        for ( Animation animation : intAnimations ) {
            updateAnimation( updateTime, animation );
        }
        for ( Animation animation : valueAnimations ) {
            updateAnimation( updateTime, animation );
        }
    }
    
    public final FloatAnimation getFloatAnimation( int animationId ) {
        return floatAnimations.get( animationId );
    }
    
    public final IntAnimation getIntAnimation( int animationId ) {
        return intAnimations.get( animationId );
    }
    
    @SuppressWarnings( "unchecked" )
    public final <V> ValueAnimation<V> getValueAnimation( int animationId ) {
        return (ValueAnimation<V>) valueAnimations.get( animationId );
    }
    
    private final void updateAnimation( long updateTime, Animation animation ) {
        animation.update( updateTime );
        if ( animation.finished ) {
            floatAnimations.remove( animation.indexedId() );
            animation.dispose();
        }
    }

}
