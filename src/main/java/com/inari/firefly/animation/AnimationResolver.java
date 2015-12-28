package com.inari.firefly.animation;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.system.component.SystemComponent;

public abstract class AnimationResolver extends SystemComponent {
    
    public static final SystemComponentKey<AnimationResolver> TYPE_KEY = SystemComponentKey.create( AnimationResolver.class );
    
    protected AnimationResolver( int id ) {
        super( id );
    }
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

    abstract int getAnimationId();

}
