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

import com.inari.commons.event.EventDispatcher;
import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.TypedKey;
import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.movement.MovementSystem;
import com.inari.firefly.sound.SoundSystem;
import com.inari.firefly.sprite.SpriteRendererSystem;
import com.inari.firefly.sprite.tile.TileGridSystem;
import com.inari.firefly.state.StateSystem;
import com.inari.firefly.system.FFContextImpl.InitMap;
import com.inari.firefly.system.view.View;
import com.inari.firefly.system.view.ViewSystem;

public final class FireFly {
    
    private FFContextImpl context;
    
    private IEventDispatcher eventDispatcher;
    private ViewSystem viewSystem;

    private final UpdateEvent updateEvent = new UpdateEvent();
    private final RenderEvent renderEvent = new RenderEvent();


    public FireFly( Class<? extends ILowerSystemFacade> lowerSystemFacadeType ) {
        InitMap initMap = new InitMap();
        initMap.put( FFContext.EVENT_DISPATCHER, EventDispatcher.class );
        initMap.put( FFContext.LOWER_SYSTEM_FACADE, lowerSystemFacadeType );
        initMap.put( FFContext.System.ASSET_SYSTEM, AssetSystem.class );
        initMap.put( FFContext.System.STATE_SYSTEM, StateSystem.class );
        initMap.put( FFContext.System.VIEW_SYSTEM, ViewSystem.class );
        initMap.put( FFContext.System.ENTITY_SYSTEM, EntitySystem.class );
        initMap.put( FFContext.System.SPRITE_RENDERER_SYSTEM, SpriteRendererSystem.class );
        initMap.put( FFContext.System.TILE_GRID_SYSTEM, TileGridSystem.class );
        initMap.put( FFContext.System.MOVEMENT_SYSTEM, MovementSystem.class );
        initMap.put( FFContext.System.ENTITY_CONTROLLER_SYSTEM, ControllerSystem.class );
        initMap.put( FFContext.System.ANIMATION_SYSTEM, AnimationSystem.class );
        initMap.put( FFContext.System.SOUND_SYSTEM, SoundSystem.class );

        init( initMap );
    }
    
    public FireFly( InitMap initMap ) {
        init( initMap );
    }

    private void init( InitMap initMap ) {
        context = new FFContextImpl( initMap );
        eventDispatcher = context.get( FFContext.EVENT_DISPATCHER );
        viewSystem = context.get( FFContext.System.VIEW_SYSTEM );

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
        return context.get( key );
    }
    
    public final FFContext getContext() {
        return context;
    }
    
    public final void update() {
        updateEvent.timeElapsed = 0;
        if ( updateEvent.lastUpdateTime == 0 ) {
            updateEvent.lastUpdateTime = System.nanoTime();
        } else {
            long currentTime = System.nanoTime();
            updateEvent.timeElapsed = currentTime - updateEvent.lastUpdateTime;
            updateEvent.lastUpdateTime = currentTime;
        }

        updateEvent.update++;
        eventDispatcher.notify( updateEvent );
    }
    
    public final void render() {
        // NOTE: for now there is no renderer that works with approximationTime so I skip the calculation so far.
        // TODO: implements the calculation of approximationTime and set it to the event.
        for ( int i = 0; i < viewSystem.viewArrayLength(); i++ ) {
            View view = viewSystem.getView( i );
            if ( view == null || !view.isActive() ) {
                continue;
            }

            int viewId = view.index();
            Rectangle bounds = view.getBounds();
            Position worldPosition = view.getWorldPosition();
            renderEvent.viewId = viewId;
            renderEvent.clip.x = worldPosition.x;
            renderEvent.clip.y = worldPosition.y;
            renderEvent.clip.width = bounds.width;
            renderEvent.clip.height = bounds.height;

            eventDispatcher.notify( renderEvent );
        }
        eventDispatcher.notify( renderEvent );
    }

}
