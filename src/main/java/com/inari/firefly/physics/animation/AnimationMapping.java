package com.inari.firefly.physics.animation;

import com.inari.firefly.physics.animation.AttributeAnimationAdapter.AttributeAnimationAdapterKey;

public final class AnimationMapping {
    public int animationId;
    public final String animationName;
    public final AttributeAnimationAdapterKey<?> adapterKey;
    
    int entityId;
    
    
    public AnimationMapping( String animationName, AttributeAnimationAdapterKey<?> adapterKey ) {
        super();
        this.animationId = -1;
        this.animationName = animationName;
        this.adapterKey = adapterKey;
    }
    public AnimationMapping( int animationId, AttributeAnimationAdapterKey<?> adapterKey ) {
        super();
        this.animationId = animationId;
        this.animationName = null;
        this.adapterKey = adapterKey;
    }
}