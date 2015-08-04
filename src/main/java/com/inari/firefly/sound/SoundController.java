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
package com.inari.firefly.sound;

import com.inari.commons.event.IEventDispatcher;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.control.Controller;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.sound.event.SoundEventListener;
import com.inari.firefly.system.ILowerSystemFacade;

public abstract class SoundController extends Controller implements SoundEventListener {

    protected SoundSystem soundSystem;
    protected ILowerSystemFacade lowerSystemFacade;
    protected IEventDispatcher eventDispatcher;
    
    SoundController( int id, FFContext context ) {
        super( id );
        soundSystem = context.getComponent( FFContext.Systems.SOUND_SYSTEM );
        lowerSystemFacade = context.getComponent( FFContext.LOWER_SYSTEM_FACADE );
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        
        eventDispatcher.register( SoundEvent.class, this );
    }

    @Override
    public final void dispose( FFContext context ) {
        eventDispatcher.unregister( SoundEvent.class, this );
    }

    @Override
    public final void onSoundEvent( SoundEvent event ) {
        switch ( event.eventType ) {
            case PLAY_SOUND: {
                if ( event.sound.getControllerId() == indexedId ) {
                    componentIds.add( event.sound.index() );
                }
                break;
            } 
            case STOP_PLAYING: {
                componentIds.remove( event.sound.index() );
                break;
            }
            default: {}
        }
    }

    @Override
    public final void update( long time ) {
        for ( int i = 0; i < componentIds.length(); i++ ) {
            if ( componentIds.isEmpty( i ) ) {
                continue;
            }
            int soundId = componentIds.get( i );
            Sound sound = soundSystem.getSound( soundId );
            if ( sound != null && sound.getControllerId() == indexedId ) {
                update( time, sound );
            }
        }
    }


    public abstract void update( long time, Sound sound );

}
