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
import com.inari.commons.lang.TypedKey;
import com.inari.firefly.FFContext;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.system.FFContextImpl.InitMap;
import com.inari.firefly.system.event.RenderEvent;
import com.inari.firefly.system.event.UpdateEvent;

public final class FireFly {
    
    private final FFContext context;
    
    private final IEventDispatcher eventDispatcher;
    private final UpdateEvent updateEvent = new UpdateEvent();
    private final RenderEvent renderEvent = new RenderEvent();

    private long lastUpdateTime = 0;
    private long update = 0;

    public FireFly( Class<? extends ILowerSystemFacade> lowerSystemFacadeType ) {
        InitMap initMap = new InitMap();
        initMap.put( FFContext.EVENT_DISPATCHER, EventDispatcher.class );
        initMap.put( FFContext.System.ASSET_SYSTEM, AssetSystem.class );
        initMap.put( FFContext.System.ENTITY_SYSTEM, EntitySystem.class );
        initMap.put( FFContext.LOWER_SYSTEM_FACADE, lowerSystemFacadeType );

        context = new FFContextImpl( initMap );
        eventDispatcher = context.get( FFContext.EVENT_DISPATCHER );
    }
    
    public FireFly( InitMap initMap ) {
        context = new FFContextImpl( initMap );
        eventDispatcher = context.get( FFContext.EVENT_DISPATCHER );
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
        long timeElapsed = 0;
        if ( lastUpdateTime == 0 ) {
            lastUpdateTime = System.nanoTime();
        } else {
            long currentTime = System.nanoTime();
            timeElapsed = currentTime - lastUpdateTime;
            lastUpdateTime = currentTime;
        }
        
        updateEvent.setTimeElapsed( timeElapsed );
        updateEvent.setUpdate( update );
        
        eventDispatcher.notify( updateEvent );
        
        update++;
    }
    
    public final void render() {
        // NOTE: for now there is no renderer that works with approximationTime so I skip the calculation so far.
        // TODO: implements the calculation of approximationTime and set it to the event.
        eventDispatcher.notify( renderEvent );
    }

}
