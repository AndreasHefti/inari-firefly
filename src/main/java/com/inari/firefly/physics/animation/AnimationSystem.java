/*******************************************************************************
 * Copyright (c) 2015 - 2016, Andreas Hefti, inarisoft@yahoo.de 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/ 
package com.inari.firefly.physics.animation;

import java.util.Iterator;

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public final class AnimationSystem 
    extends 
        ComponentSystem<AnimationSystem>
    implements
        UpdateEventListener {
    
    public static final FFSystemTypeKey<AnimationSystem> SYSTEM_KEY = FFSystemTypeKey.create( AnimationSystem.class );
    
    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        Animation.TYPE_KEY,
        AnimationResolver.TYPE_KEY
    };

    private final DynArray<Animation> animations;
    private final DynArray<AnimationResolver> animationResolver;

    AnimationSystem() {
        super( SYSTEM_KEY );
        animations = new DynArray<Animation>();
        animationResolver = new DynArray<AnimationResolver>();
    }
    
    @Override
    public void init( FFContext context ) {
        super.init( context );
        
        context.registerListener( UpdateEvent.TYPE_KEY, this );
        context.registerListener( AnimationSystemEvent.TYPE_KEY, this );
    }
    
    @Override
    public void dispose( FFContext context ) {
        clear();
        
        context.disposeListener( UpdateEvent.TYPE_KEY, this );
        context.disposeListener( AnimationSystemEvent.TYPE_KEY, this );
    }
    
    public final void clear() {
        for ( Animation animation : animations ) {
            disposeSystemComponent( animation );
        }
        animations.clear();
    }

    final void onAnimationEvent( AnimationSystemEvent event ) {
        if ( !animations.contains( event.animationId ) ) {
            return;
        }
        
        Animation animation = animations.get( event.animationId );
        switch ( event.type ) {
            case START_ANIMATION: {
                animation.active = true;
                break;
            }
            case STOP_ANIMATION: {
                animation.active = false;
                break;
            }
            case FINISH_ANIMATION: {
                animation.active = false;
                animation.finished = true;
                break;
            }
        }
    }

    public final boolean exists( int animationId ) {
        if ( animationId < 0 ) {
            return false;
        }
        return animations.contains( animationId );
    }

    public final boolean isActive( int animationId ) {
        if ( animationId < 0 || !animations.contains( animationId ) ) {
            return false;
        }

        Animation animation = animations.get( animationId );
        return animation.isActive();
    }
    
    public final void activateAnimation( int animationId ) {
        animations.get( animationId ).activate();
    }
    
    public final void resetAnimation( int animationId ) {
        animations.get( animationId ).reset();
    }

    @Override
    public final void update( UpdateEvent event ) {
        for ( int i = 0; i < animations.capacity(); i++ ) {
            Animation animation = animations.get( i );
            if ( animation != null ) {
                if ( animation.active ) {
                    animation.systemUpdate();
                    continue;
                }
                
                if ( animation.finished ) {
                    animations.remove( animation.index() );
                    animation.dispose();
                    continue;
                }
                
                if ( animation.startTime > 0 && event.timer.getTime() >= animation.startTime ) {
                    animation.activate();
                    continue;
                }
            }
        }
    }
    
    public final Animation getAnimation( int animationId ) {
        if ( animationId < 0 ) {
            return null;
        }
        return animations.get( animationId );
    }
    
    public final <T extends Animation> T getAnimationAs( String animationName, Class<T> subType ) {
        return getAnimationAs( getAnimationId( animationName ), subType );
    }
    
    public final <T extends Animation> T getAnimationAs( int animationId, Class<T> subType ) {
        if ( !animations.contains( animationId ) ) {
            return null;
        }
        
        return subType.cast( animations.get( animationId ) );
    }
    
    public final <T extends AnimationResolver> T getAnimationResolverAs( int animationResolverId, Class<T> subType ) {
        if ( !animationResolver.contains( animationResolverId ) ) {
            return null;
        }
        
        return subType.cast( animationResolver.get( animationResolverId ) );
    }
    
    public final int getAnimationId( String animationName ) {
        for ( int i = 0; i < animations.capacity(); i++ ) {
            if ( !animations.contains( i ) ) {
                continue;
            }
            
            Animation anim = animations.get( i );
            if ( animationName.equals( anim.getName() ) ) {
                return anim.index();
            }
        }
        
        return -1;
    }
    
    public final <A extends Animation> A getAnimation( Class<A> type, int animationId ) {
        Animation animation = animations.get( animationId );
        if ( animation == null ) {
            return null;
        }
        return type.cast( animation );
    }
    
    public final AnimationResolver getAnimationResolver( int id ) {
        if ( !animationResolver.contains( id ) ) {
            return null;
        }
        
        return animationResolver.get( id );
    }

    public final int getAnimationResolverId( String name ) {
        for ( AnimationResolver resolver : animationResolver ) {
            if ( name.equals( resolver.getName() ) ) {
                return resolver.index();
            }
        }
        
        return -1;
    }

    public final void deleteAnimationResolver( int id ) {
        if ( !animationResolver.contains( id ) ) {
            return;
        }
        
        AnimationResolver resolver = animationResolver.remove( id );
        resolver.dispose();
    }

    public final float getValue( int animationId, int componentId, float currentValue ) {
        if ( !isActive( animationId ) ) {
            return currentValue;
        }

        FloatAnimation animation = getAnimation( FloatAnimation.class, animationId );
        return animation.getValue( componentId, currentValue );
    }

    public final int getValue( int animationId, int componentId, int currentValue ) {
        if ( !isActive( animationId ) ) {
            return currentValue;
        }

        IntAnimation animation = getAnimation( IntAnimation.class, animationId );
        return animation.getValue( componentId, currentValue );
    }

    public final <V> V getValue( int animationId, int componentId, V currentValue ) {
        if ( !isActive( animationId ) ) {
            return currentValue;
        }

        @SuppressWarnings( "unchecked" )
        ValueAnimation<V> animation = getAnimation( ValueAnimation.class, animationId );
        return animation.getValue( componentId, currentValue );
    }
    
    public final void deleteAnimation( int animationId ) {
        if ( !animations.contains( animationId ) ) {
            return;
        }
        
        disposeSystemComponent( animations.remove( animationId ) );
    }
    
    public final int getAnimationId( int animationResolverId, int defaultValue ) {
        if ( animationResolverId < 0 || !animationResolver.contains( animationResolverId ) ) {
            return defaultValue;
        }
        
        return animationResolver.get( animationResolverId ).getAnimationId();
    }
    
    public final AnimationBuilder getAnimationBuilder() {
        return new AnimationBuilder();
    }
    
    public final AnimationResolverBuilder getAnimationResolverBuilder() {
        return new AnimationResolverBuilder();
    }
    
    @Override
    public final SystemComponentKey<?>[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }
    

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter[] {
            new AnimationBuilderAdapter( this ),
            new AnimationResolverBuilderAdapter( this )
        };
    };
    

    public final class AnimationBuilder extends SystemComponentBuilder {
        
        private AnimationBuilder() { 
            super( context ); 
        }
        
        @Override
        public final SystemComponentKey<Animation> systemComponentKey() {
            return Animation.TYPE_KEY;
        }

        @Override
        public int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            Animation animation = createSystemComponent( componentId, componentType, context );
            animations.set( animation.index(), animation );

            if ( activate ) {
                animation.active = true;
            }
            
            return animation.index();
        }
    }
    
    public final class AnimationResolverBuilder extends SystemComponentBuilder {
        
        private AnimationResolverBuilder() {
            super( context );
        }
        
        @Override
        public final SystemComponentKey<AnimationResolver> systemComponentKey() {
            return AnimationResolver.TYPE_KEY;
        }

        @Override
        public int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            AnimationResolver resolver = createSystemComponent( componentId, componentType, context );
            animationResolver.set( resolver.index(), resolver );
            
            return resolver.index();
        }
        
    }

    private final class AnimationBuilderAdapter extends SystemBuilderAdapter<Animation> {
        public AnimationBuilderAdapter( AnimationSystem system ) {
            super( system, new AnimationBuilder() );
        }
        @Override
        public final SystemComponentKey<Animation> componentTypeKey() {
            return Animation.TYPE_KEY;
        }
        @Override
        public final Animation get( int id ) {
            return getAnimation( id );
        }
        @Override
        public final void delete( int id ) {
            deleteAnimation( id );
        }
        @Override
        public final Iterator<Animation> getAll() {
            return animations.iterator();
        }
        @Override
        public final int getId( String name ) {
            return getAnimationId( name );
        }
        @Override
        public final void activate( int id ) {
            activateAnimation( id );
        }
        @Override
        public final void deactivate( int id ) {
            resetAnimation( id );
        }
    }
    
    private final class AnimationResolverBuilderAdapter extends SystemBuilderAdapter<AnimationResolver> {
        public AnimationResolverBuilderAdapter( AnimationSystem system ) {
            super( system, new AnimationResolverBuilder() );
        }
        @Override
        public final SystemComponentKey<AnimationResolver> componentTypeKey() {
            return AnimationResolver.TYPE_KEY;
        }
        @Override
        public final AnimationResolver get( int id ) {
            return getAnimationResolver( id );
        }
        @Override
        public final void delete( int id ) {
            deleteAnimationResolver( id );
        }
        @Override
        public final Iterator<AnimationResolver> getAll() {
            return animationResolver.iterator();
        }
        @Override
        public final int getId( String name ) {
            return getAnimationResolverId( name );
        }
        @Override
        public final void activate( int id ) {
            throw new UnsupportedOperationException( "Action is not activable" );
        }
        @Override
        public final void deactivate( int id ) {
            throw new UnsupportedOperationException( "Action is not activable" );
        }
    }

}
