/*******************************************************************************
 * Copyright (c) 2015, Andreas Hefti, inarisoft@yahoo.de 
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

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFContext;
import com.inari.firefly.animation.event.AnimationEvent;
import com.inari.firefly.animation.event.AnimationEventListener;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.ComponentBuilderHelper;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.event.UpdateEvent;
import com.inari.firefly.system.event.UpdateEventListener;

public final class AnimationSystem 
    implements 
        FFSystem, 
        ComponentSystem,
        ComponentBuilderFactory, 
        UpdateEventListener, 
        AnimationEventListener {
    
    private IEventDispatcher eventDispatcher;
    
    private final DynArray<Animation> animations;

    AnimationSystem() {
        animations = new DynArray<Animation>();
    }
    
    @Override
    public void init( FFContext context ) {
        eventDispatcher = context.get( FFContext.EVENT_DISPATCHER );
        
        eventDispatcher.register( UpdateEvent.class, this );
        eventDispatcher.register( AnimationEvent.class, this );
    }
    
    @Override
    public void dispose( FFContext context ) {
        clear();
        
        eventDispatcher.unregister( UpdateEvent.class, this );
        eventDispatcher.unregister( AnimationEvent.class, this );
    }
    
    public final void clear() {
        for ( Animation animation : animations ) {
            animation.dispose();
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
    
    public final void deleteAnimation( int animationId ) {
        Animation animation = animations.remove( animationId );
        if ( animation != null ) {
            animation.dispose();
        }
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
    
    private static final Set<Class<?>> SUPPORTED_COMPONENT_TYPES = new HashSet<Class<?>>();
    @Override
    public final Set<Class<?>> supportedComponentTypes() {
        if ( SUPPORTED_COMPONENT_TYPES.isEmpty() ) {
            SUPPORTED_COMPONENT_TYPES.add( Animation.class );
        }
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final void fromAttributes( Attributes attributes ) {
        fromAttributes( attributes, BuildType.CLEAR_OLD ); 
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public final void fromAttributes( Attributes attributes, BuildType buildType ) {
        if ( buildType == BuildType.CLEAR_OLD ) {
            clear();
        }
        
        for ( Class<? extends Animation> animationSubType : attributes.getAllSubTypes( Animation.class ) ) {
            new ComponentBuilderHelper<Animation>() {
                @Override
                public Animation get( int id ) {
                    return getAnimation( id );
                }
                @Override
                public void delete( int id ) {
                    deleteAnimation( id );
                }
            }.buildComponents( Animation.class, buildType, (AnimationBuilder<Animation>) getAnimationBuilder( animationSubType ), attributes );
        }
        
    }

    @Override
    public final void toAttributes( Attributes attributes ) {
        for ( Animation animation : animations ) {
            ComponentBuilderHelper.toAttributes( attributes, animation.getIndexedObjectType(), animation );
        }
    }
    

    public final class AnimationBuilder<A extends Animation> extends BaseComponentBuilder<A> {
        
        private final Class<A> animationType;
        
        private AnimationBuilder( AnimationSystem system, Class<A> animationType ) {
            super( system );
            this.animationType = animationType;
        }
        
        @Override
        protected A createInstance( Constructor<A> constructor, Object... paramValues ) throws Exception {
            return constructor.newInstance( paramValues );
        }

        @Override
        public A build( int componentId ) {
            attributes.put( Component.INSTANCE_TYPE_NAME, animationType.getName() );
            A animation = getInstance( componentId );
            
            animation.fromAttributes( attributes );
            
            animations.set( animation.indexedId(), animation );
            return animation;
        }
    }

}
