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
import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.entity.EntityActivationEvent;
import com.inari.firefly.entity.EntityActivationListener;
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
        UpdateEventListener,
        EntityActivationListener {
    
    public static final FFSystemTypeKey<AnimationSystem> SYSTEM_KEY = FFSystemTypeKey.create( AnimationSystem.class );
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        Animation.TYPE_KEY
    );

    final DynArray<Animation> animations;
    final DynArray<AnimationMapping> activeMappings;

    AnimationSystem() {
        super( SYSTEM_KEY );
        animations = DynArray.create( Animation.class, 20, 10 );
        activeMappings = DynArray.create( AnimationMapping.class, 100, 100 );
    }
    
    @Override
    public void init( FFContext context ) {
        super.init( context );
        
        context.registerListener( UpdateEvent.TYPE_KEY, this );
        context.registerListener( AnimationSystemEvent.TYPE_KEY, this );
        context.registerListener( EntityActivationEvent.TYPE_KEY, this );
    }
    
    @Override
    public void dispose( FFContext context ) {
        clear();
        
        context.disposeListener( UpdateEvent.TYPE_KEY, this );
        context.disposeListener( AnimationSystemEvent.TYPE_KEY, this );
        context.disposeListener( EntityActivationEvent.TYPE_KEY, this );
    }
    
    @Override
    public final boolean match( Aspects aspects ) {
        return aspects.contains( EAnimation.TYPE_KEY );
    }

    @Override
    public final void entityActivated( int entityId, Aspects aspects ) {
        DynArray<AnimationMapping> animationMappings = context.getEntityComponent( entityId, EAnimation.TYPE_KEY ).getAnimationMappings();
        for ( int i = 0; i < animationMappings.capacity(); i++ ) {
            final AnimationMapping animationMapping = animationMappings.get( i );
            if ( animationMapping == null ) {
                continue;
            }
            
            animationMapping.entityId = entityId;
            if ( animationMapping.animationId < 0 ) {
                animationMapping.animationId = getAnimationId( animationMapping.animationName );
            }
            
            activeMappings.add( animationMapping );
        }
    }

    @Override
    public final void entityDeactivated( int entityId, Aspects aspects ) {
        for ( int i = 0; i < activeMappings.capacity(); i++ ) {
            AnimationMapping animationMapping = activeMappings.get( i );
            if ( animationMapping == null ) {
                continue;
            }
            
            if ( animationMapping.entityId == entityId ) {
                activeMappings.remove( i );
            }
        }
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
                    applyValueAttribute( animation );
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
    
    private void applyValueAttribute( final Animation animation ) {
        for ( int i = 0; i < activeMappings.capacity(); i++ ) {
            AnimationMapping animationMapping = activeMappings.get( i );
            if ( animationMapping == null || animation.index() != animationMapping.animationId ) {
                continue;
            }
            
            animationMapping
                .adapterKey
                .getAdapterInstance()
                .apply( animationMapping.entityId, animation, context );
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

    public final SystemComponentBuilder getAnimationBuilder( Class<? extends Animation> componentType ) {
        if ( componentType == null ) {
            throw new IllegalArgumentException( "componentType is needed for SystemComponentBuilder for component: " + Animation.TYPE_KEY.name() );
        }
        return new AnimationBuilder( componentType );
    }
    
    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            new AnimationBuilderAdapter()
        );
    }

    private final class AnimationBuilder extends SystemComponentBuilder {
        
        private AnimationBuilder( Class<? extends Animation> componentType ) { 
            super( context, componentType ); 
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

    private final class AnimationBuilderAdapter extends SystemBuilderAdapter<Animation> {
        private AnimationBuilderAdapter() {
            super( AnimationSystem.this, Animation.TYPE_KEY );
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
        @Override
        public final SystemComponentBuilder createComponentBuilder( Class<? extends Animation> componentType ) {
            return getAnimationBuilder( componentType );
        }
    }
}
