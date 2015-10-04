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
package com.inari.firefly.system;

import java.util.Iterator;

import com.inari.commons.event.EventDispatcher;
import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.TypedKey;
import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.ComponentControllerSystem;
import com.inari.firefly.entity.EntityPrefabSystem;
import com.inari.firefly.entity.EntityProvider;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.sprite.SpriteViewRenderer;
import com.inari.firefly.renderer.sprite.SpriteViewSystem;
import com.inari.firefly.renderer.tile.TileGridRenderer;
import com.inari.firefly.renderer.tile.TileGridSystem;
import com.inari.firefly.sound.SoundSystem;
import com.inari.firefly.state.StateSystem;
import com.inari.firefly.system.FFContextImpl.InitMap;
import com.inari.firefly.system.view.View;
import com.inari.firefly.system.view.ViewSystem;
import com.inari.firefly.task.TaskSystem;

public final class FireFly {
    
    private FFContextImpl context;
    
    private IEventDispatcher eventDispatcher;
    private ViewSystem viewSystem;
    private LowerSystemFacade lowerSystemFacade;

    private final UpdateEvent updateEvent;
    private final RenderEvent renderEvent;


    public FireFly( 
        Class<? extends LowerSystemFacade> lowerSystemFacadeType,
        Class<? extends Input> input
    ) {
        InitMap initMap = new InitMap();
        initMap.put( FFContext.EVENT_DISPATCHER, EventDispatcher.class );
        initMap.put( FFContext.TIMER, DefaultFFTimerImpl.class );
        initMap.put( FFContext.INPUT, input );
        initMap.put( FFContext.LOWER_SYSTEM_FACADE, lowerSystemFacadeType );
        initMap.put( FFContext.ENTITY_PROVIDER, EntityProvider.class );
        initMap.put( AssetSystem.CONTEXT_KEY, AssetSystem.class );
        initMap.put( StateSystem.CONTEXT_KEY, StateSystem.class );
        initMap.put( ViewSystem.CONTEXT_KEY, ViewSystem.class );
        initMap.put( EntitySystem.CONTEXT_KEY, EntitySystem.class );
        initMap.put( EntityPrefabSystem.CONTEXT_KEY, EntityPrefabSystem.class );
        initMap.put( SpriteViewSystem.CONTEXT_KEY, SpriteViewSystem.class );
        initMap.put( TileGridSystem.CONTEXT_KEY, TileGridSystem.class );
        initMap.put( ComponentControllerSystem.CONTEXT_KEY, ComponentControllerSystem.class );
        initMap.put( AnimationSystem.CONTEXT_KEY, AnimationSystem.class );
        initMap.put( SoundSystem.CONTEXT_KEY, SoundSystem.class );
        initMap.put( SpriteViewRenderer.CONTEXT_KEY, SpriteViewRenderer.class );
        initMap.put( TileGridRenderer.CONTEXT_KEY, TileGridRenderer.class );
        initMap.put( TaskSystem.CONTEXT_KEY, TaskSystem.class );

        init( initMap );
        
        updateEvent = new UpdateEvent( context.getComponent( FFContext.TIMER ) );
        renderEvent = new RenderEvent();
    }

    
    public FireFly( InitMap initMap ) {
        init( initMap );
        
        updateEvent = new UpdateEvent( context.getComponent( FFContext.TIMER ) );
        renderEvent = new RenderEvent();
    }

    private void init( InitMap initMap ) {
        context = new FFContextImpl( initMap );
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        viewSystem = context.getComponent( ViewSystem.CONTEXT_KEY );
        lowerSystemFacade = context.getComponent( FFContext.LOWER_SYSTEM_FACADE );

        if ( eventDispatcher == null ) {
            throw new FFInitException( "Missing IEventDispatcher instance from FFContext" );
        }
        if ( viewSystem == null ) {
            throw new FFInitException( "Missing ViewSystem instance from FFContext" );
        }
    }


    public final void dispose() {
        context.dispose();
    }
    
    public final <T> T get( TypedKey<T> key ) {
        return context.getComponent( key );
    }
    
    public final FFContext getContext() {
        return context;
    }
    
    public final void update() {
        updateEvent.timer.tick();
        //System.out.println( updateEvent );
        eventDispatcher.notify( updateEvent );
    }
    
    public final void render() {
        // NOTE: for now there is no renderer that works with approximationTime so I skip the calculation so far.
        // TODO: implements the calculation of approximationTime and set it to the event.
        if ( viewSystem.hasActiveViewports() ) {
            Iterator<View> virtualViewIterator = viewSystem.activeViewportIterator();
            while ( virtualViewIterator.hasNext() ) {
                View virtualView = virtualViewIterator.next();

                int viewId = virtualView.index();
                Rectangle bounds = virtualView.getBounds();
                Position worldPosition = virtualView.getWorldPosition();
                renderEvent.viewId = viewId;
                renderEvent.clip.x = worldPosition.x;
                renderEvent.clip.y = worldPosition.y;
                renderEvent.clip.width = bounds.width;
                renderEvent.clip.height = bounds.height;

                lowerSystemFacade.startRendering( virtualView );
                eventDispatcher.notify( renderEvent );
                lowerSystemFacade.endRendering( virtualView );
            }
            
            lowerSystemFacade.flush( viewSystem.activeViewportIterator() );
        } else {
            View baseView = viewSystem.getView( ViewSystem.BASE_VIEW_ID );
            
            Rectangle bounds = baseView.getBounds();
            Position worldPosition = baseView.getWorldPosition();
            renderEvent.viewId = ViewSystem.BASE_VIEW_ID;
            renderEvent.clip.x = worldPosition.x;
            renderEvent.clip.y = worldPosition.y;
            renderEvent.clip.width = bounds.width;
            renderEvent.clip.height = bounds.height;
            
            lowerSystemFacade.startRendering( baseView );
            eventDispatcher.notify( renderEvent );
            lowerSystemFacade.endRendering( baseView );
            
            lowerSystemFacade.flush( null );
        }
    }

}
