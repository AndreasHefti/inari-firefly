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
import java.util.Random;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.entity.EntityPrefabSystem;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.sprite.SpriteViewRenderer;
import com.inari.firefly.renderer.tile.TileGridSystem;
import com.inari.firefly.sound.SoundSystem;
import com.inari.firefly.state.StateSystem;
import com.inari.firefly.system.view.View;
import com.inari.firefly.system.view.ViewSystem;
import com.inari.firefly.task.TaskSystem;

public abstract class FireFly {
    
    public static final Random RANDOM = new Random();
    
    protected final FFContext context;
    
    protected FFSystemInterface lowerSystemFacade;
    protected ViewSystem viewSystem;

    private final UpdateEvent updateEvent;
    private final RenderEvent renderEvent;
    
    private boolean disposed = false;

    protected FireFly( 
            IEventDispatcher eventDispatcher, 
            FFSystemInterface systemInterface, 
            FFTimer timer,
            Input input 
    ) {
        context = new FFContext( eventDispatcher, systemInterface, timer, input );
        
        lowerSystemFacade = context.getSystemInterface();
        viewSystem = context.getSystem( ViewSystem.CONTEXT_KEY );
        
        context.loadSystem( AssetSystem.CONTEXT_KEY );
        context.loadSystem( StateSystem.CONTEXT_KEY );
        context.loadSystem( EntitySystem.CONTEXT_KEY );
        context.loadSystem( EntityPrefabSystem.CONTEXT_KEY );
        context.loadSystem( ControllerSystem.CONTEXT_KEY );
        context.loadSystem( AnimationSystem.CONTEXT_KEY );
        context.loadSystem( SoundSystem.CONTEXT_KEY );
        context.loadSystem( SpriteViewRenderer.CONTEXT_KEY );
        context.loadSystem( TileGridSystem.CONTEXT_KEY );
        context.loadSystem( TaskSystem.CONTEXT_KEY );
        
        updateEvent = new UpdateEvent( timer );
        renderEvent = new RenderEvent();
    }

    public final void dispose() {
        context.dispose();
        disposed = true;
    }

    public final FFContext getContext() {
        return context;
    }
    
    public final void update() {
        updateEvent.timer.tick();
        context.notify( updateEvent );
    }
    
    public final void render() {
        if ( disposed ) {
            return;
        }
        // NOTE: for now there is no renderer that works with approximationTime so I skip the calculation so far.
        // TODO: implements the calculation of approximationTime and set it to the event.
        if ( viewSystem.hasActiveViewports() ) {
            Iterator<View> virtualViewIterator = viewSystem.activeViewportIterator();
            while ( virtualViewIterator.hasNext() ) {
                View virtualView = virtualViewIterator.next();
                if ( !virtualView.isActive() ) {
                    continue;
                }

                int viewId = virtualView.index();
                Rectangle bounds = virtualView.getBounds();
                Position worldPosition = virtualView.getWorldPosition();
                renderEvent.viewId = viewId;
                renderEvent.clip.x = worldPosition.x;
                renderEvent.clip.y = worldPosition.y;
                renderEvent.clip.width = bounds.width;
                renderEvent.clip.height = bounds.height;

                lowerSystemFacade.startRendering( virtualView );
                context.notify( renderEvent );
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
            context.notify( renderEvent );
            lowerSystemFacade.endRendering( baseView );
            
            lowerSystemFacade.flush( null );
        }
    }

}
