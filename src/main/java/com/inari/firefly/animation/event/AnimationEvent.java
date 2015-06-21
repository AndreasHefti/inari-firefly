package com.inari.firefly.animation.event;

import com.inari.commons.event.Event;

public final class AnimationEvent extends Event<AnimationEventListener> {
    
    public enum Type {
        START_ANIMATION,
        STOP_ANIMATION
    }
    
    public final int animationId;
    public final Type type;

    public AnimationEvent( Type type, int animationId ) {
        this.animationId = animationId;
        this.type = type;
    }

    @Override
    public final void notify( AnimationEventListener listener ) {
        listener.onAnimationEvent( this );
    }

}
