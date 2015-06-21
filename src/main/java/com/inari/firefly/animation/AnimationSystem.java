package com.inari.firefly.animation;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFContext;
import com.inari.firefly.animation.event.AnimationEvent;
import com.inari.firefly.animation.event.AnimationEventListener;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.event.UpdateEvent;
import com.inari.firefly.system.event.UpdateEventListener;

public final class AnimationSystem implements FFSystem, ComponentBuilderFactory, UpdateEventListener, AnimationEventListener {
    
    private IEventDispatcher eventDispatcher;
    
    private final DynArray<Animation> animations;

    AnimationSystem() {
        animations = new DynArray<Animation>();
    }
    
    @Override
    public void init( FFContext context ) {
        eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
        
        eventDispatcher.register( UpdateEvent.class, this );
        eventDispatcher.register( AnimationEvent.class, this );
    }
    
    @Override
    public void dispose( FFContext context ) {
        animations.clear();
        
        eventDispatcher.unregister( UpdateEvent.class, this );
        eventDispatcher.unregister( AnimationEvent.class, this );
    }
    
    @Override
    public void onAnimationEvent( AnimationEvent event ) {
        Animation animation = animations.get( event.animationId );
        if ( animation == null ) {
            return;
        }
        
        switch ( event.type ) {
            case START_ANIMATION: {
                animation.active = true;
                break;
            }
            case STOP_ANIMATION: {
                animation.active = false;
                animation.finished = true;
                break;
            }
        }
    }

    @Override
    public final void update( UpdateEvent event ) {
        long updateTime = event.getUpdate();
        for ( int i = 0; i < animations.capacity(); i++ ) {
            Animation animation = animations.get( i );
            if ( animation != null ) {
                animation.update( updateTime );
                if ( animation.finished ) {
                    animations.remove( animation.indexedId() );
                    animation.dispose();
                }
            }
        }
    }
    
    public final Animation getAnimation( int animationId ) {
        return animations.get( animationId );
    }
    
    public final <A extends Animation> A getAnimation( Class<A> type, int animationId ) {
        Animation animation = animations.get( animationId );
        if ( animation == null ) {
            return null;
        }
        return type.cast( animation );
    }
    
    
    @Override
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public final <C> ComponentBuilder<C> getComponentBuilder( Class<C> type ) {
        if ( !Animation.class.isAssignableFrom( type ) ) {
            throw new IllegalArgumentException( "The IComponentType is not a subtype of Animation." + type );
        }
        
        return new AnimationBuilder( this, type );
    }
    
    public final <A extends Animation> AnimationBuilder<A> getAnimationBuilder( Class<A> animationType ) {
        return new AnimationBuilder<A>( this, animationType );
    }
    
    public final class AnimationBuilder<A extends Animation> extends BaseComponentBuilder<A> {
        
        private final Class<A> animationType;
        
        private AnimationBuilder( AnimationSystem system, Class<A> animationType ) {
            super( system );
            this.animationType = animationType;
        }

        @Override
        public A build( int componentId ) {
            attributes.put( Component.INSTANCE_TYPE_NAME, animationType.getName() );
            A animation = getInstance( componentId );
            
            animation.fromAttributeMap( attributes );
            
            animations.set( animation.indexedId(), animation );
            return animation;
        }
    }

}
