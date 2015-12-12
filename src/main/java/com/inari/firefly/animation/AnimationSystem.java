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
package com.inari.firefly.animation;

import java.util.Iterator;

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.animation.event.AnimationEvent;
import com.inari.firefly.animation.event.AnimationEventListener;
import com.inari.firefly.component.Component;
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
        AnimationEventListener {
    
    public static final FFSystemTypeKey<AnimationSystem> SYSTEM_KEY = FFSystemTypeKey.create( AnimationSystem.class );
    
    private static final SystemComponentKey[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        Animation.TYPE_KEY
    };

    private final DynArray<Animation> animations;

    AnimationSystem() {
        super( SYSTEM_KEY );
        animations = new DynArray<Animation>();
    }
    
    @Override
    public void init( FFContext context ) {
        super.init( context );
        
        context.registerListener( UpdateEvent.class, this );
        context.registerListener( AnimationEvent.class, this );
    }
    
    @Override
    public void dispose( FFContext context ) {
        clear();
        
        context.disposeListener( UpdateEvent.class, this );
        context.disposeListener( AnimationEvent.class, this );
    }
    
    public final void clear() {
        for ( Animation animation : animations ) {
            disposeAnimation( animation );
        }
        animations.clear();
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

    public final boolean exists( int animationId ) {
        if ( animationId < 0 ) {
            return false;
        }
        return animations.contains( animationId );
    }

    public final boolean isActive( int animationId ) {
        if ( !exists( animationId ) ) {
            return false;
        }

        Animation animation = animations.get( animationId );
        return animation.isActive();
    }
    
    public boolean isFinished( int animationId ) {
        if ( !exists( animationId ) ) {
            return true;
        }

        Animation animation = animations.get( animationId );
        return animation.isFinished();
    }

    @Override
    public final void update( UpdateEvent event ) {
        for ( int i = 0; i < animations.capacity(); i++ ) {
            Animation animation = animations.get( i );
            if ( animation != null ) {
                if ( animation.finished ) {
                    animations.remove( animation.index() );
                    animation.dispose();
                    continue;
                }

                animation.update( event.timer );
            }
        }
    }
    
    public final Animation getAnimation( int animationId ) {
        return animations.get( animationId );
    }
    
    public final <T extends Animation> T getAnimationAs( int animationId, Class<T> animationType ) {
        return animationType.cast( animations.get( animationId ) );
    }
    
    public final int getAnimationId( String animationName ) {
        for ( int i = 0; i < animations.capacity(); i++ ) {
            if ( !animations.contains( i ) ) {
                continue;
            }
            
            Animation anim = animations.get( i );
            if ( animationName.equals( anim.getName() ) ) {
                return anim.getId();
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
        
        disposeAnimation( animations.remove( animationId ) );
    }
    
    public final AnimationBuilder getAnimationBuilder() {
        return new AnimationBuilder();
    }
    
    @Override
    public final SystemComponentKey[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }
    
    private final void disposeAnimation( Animation animation ) {
        if ( animation == null ) {
            return;
        }

        animation.dispose();
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter[] {
            new AnimationBuilderAdapter( this )
        };
    };
    

    public final class AnimationBuilder extends SystemComponentBuilder {
        
        private AnimationBuilder() {}
        
        @Override
        public final SystemComponentKey systemComponentKey() {
            return Animation.TYPE_KEY;
        }

        @Override
        public int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            checkType( componentType );
            attributes.put( Component.INSTANCE_TYPE_NAME, componentType.getName() );
            Animation animation = getInstance( context, componentId );
            
            animation.fromAttributes( attributes );
            
            animations.set( animation.index(), animation );
            postInit( animation, context );
            
            return animation.getId();
        }
    }

    private final class AnimationBuilderAdapter extends SystemBuilderAdapter<Animation> {
        public AnimationBuilderAdapter( AnimationSystem system ) {
            super( system, new AnimationBuilder() );
        }
        @Override
        public final SystemComponentKey componentTypeKey() {
            return Animation.TYPE_KEY;
        }
        @Override
        public final Animation get( int id, Class<? extends Animation> subtype ) {
            return getAnimation( id );
        }
        @Override
        public final void delete( int id, Class<? extends Animation> subtype ) {
            deleteAnimation( id );
        }
        @Override
        public final Iterator<Animation> getAll() {
            return animations.iterator();
        }
    }

}
