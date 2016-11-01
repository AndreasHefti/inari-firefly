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
package com.inari.firefly.system;

import java.util.Iterator;
import java.util.Random;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.PositionF;
import com.inari.commons.geom.Rectangle;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.audio.AudioSystem;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.control.state.StateSystem;
import com.inari.firefly.control.task.TaskSystem;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.entity.prefab.EntityPrefabSystem;
import com.inari.firefly.graphics.sprite.SpriteViewSystem;
import com.inari.firefly.graphics.tile.TileGridSystem;
import com.inari.firefly.graphics.view.Layer;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.graphics.view.ViewSystem;
import com.inari.firefly.physics.animation.AnimationSystem;
import com.inari.firefly.system.external.FFAudio;
import com.inari.firefly.system.external.FFGraphics;
import com.inari.firefly.system.external.FFInput;
import com.inari.firefly.system.external.FFTimer;

public abstract class FireFlyApp {
    
    public static final Random RANDOM = new Random();
    
    protected final FFContext context;
    
    protected FFGraphics graphics;
    protected ViewSystem viewSystem;

    private final UpdateEvent updateEvent;
    private final RenderEvent renderEvent;
    private final PostRenderEvent postRenderEvent;

    private boolean disposed = false;

    protected FireFlyApp( 
            IEventDispatcher eventDispatcher, 
            FFGraphics graphics,
            FFAudio audio,
            FFTimer timer,
            FFInput input 
    ) {
        context = new FFContext( eventDispatcher, graphics, audio, timer, input );
        
        this.graphics = context.getGraphics();
        viewSystem = context.getSystem( ViewSystem.SYSTEM_KEY );
        
        context.loadSystem( AssetSystem.SYSTEM_KEY );
        context.loadSystem( StateSystem.SYSTEM_KEY );
        context.loadSystem( EntitySystem.SYSTEM_KEY );
        context.loadSystem( EntityPrefabSystem.SYSTEM_KEY );
        context.loadSystem( ControllerSystem.SYSTEM_KEY );
        context.loadSystem( AnimationSystem.SYSTEM_KEY );
        context.loadSystem( AudioSystem.SYSTEM_KEY );
        context.loadSystem( SpriteViewSystem.SYSTEM_KEY );
        context.loadSystem( TileGridSystem.SYSTEM_KEY );
        context.loadSystem( TaskSystem.SYSTEM_KEY );
        
        updateEvent = new UpdateEvent( timer );
        renderEvent = new RenderEvent();
        postRenderEvent = new PostRenderEvent( context );
    }
    
    public final boolean exit() {
        return context.exit;
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
        View baseView = viewSystem.getView( ViewSystem.BASE_VIEW_ID );
        
        // NOTE: for now there is no renderer that works with approximationTime so I skip the calculation so far.
        // TODO: implements the calculation of approximationTime and set it to the event.
        if ( viewSystem.hasActiveViewports() ) {
            Iterator<View> virtualViewIterator = viewSystem.activeViewportIterator();
            while ( virtualViewIterator.hasNext() ) {
                View virtualView = virtualViewIterator.next();
                if ( !virtualView.isActive() ) {
                    continue;
                }

                render( virtualView );
            }
            
            graphics.flush( viewSystem.activeViewportIterator() );
        } else {
            render( baseView );
            graphics.flush( null );
        }
        
        context.notify( postRenderEvent );
    }
    
    private void render( final View view ) {
        Rectangle bounds = view.getBounds();
        PositionF worldPosition = view.getWorldPosition();
        int viewId = view.index();
        
        renderEvent.viewId = viewId;
        renderEvent.clip.x = (int) Math.floor( worldPosition.x );
        renderEvent.clip.y = (int) Math.floor( worldPosition.y );
        renderEvent.clip.width = bounds.width;
        renderEvent.clip.height = bounds.height;

        graphics.startRendering( view, true );
        
        if ( !viewSystem.isLayeringEnabled( viewId ) ) {
            context.notify( renderEvent );
        } else if ( viewSystem.hasActiveLayers( viewId ) ) {
            Iterator<Layer> layerIterator = viewSystem.getActiveLayersOfView( viewId );
            while ( layerIterator.hasNext() ) {
                renderEvent.layerId = layerIterator.next().index();
                context.notify( renderEvent );
            }
            renderEvent.layerId = 0;
        } 

        graphics.endRendering( view );
    }

}
