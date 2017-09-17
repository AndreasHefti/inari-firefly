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
package com.inari.firefly.audio;

import com.inari.commons.event.Event;

public final class AudioSystemEvent extends Event<AudioSystem> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( AudioSystemEvent.class );
    
    public enum Type {
        PLAY_SOUND,
        STOP_PLAYING
    }
    
    public final int soundId;
    public final String name;
    public final Type eventType;

    public AudioSystemEvent( int soundId, Type eventType ) {
        super( TYPE_KEY );
        this.soundId = soundId;
        this.name = null;
        this.eventType = eventType;
    }
    
    public AudioSystemEvent( String name, Type eventType ) {
        super( TYPE_KEY );
        this.soundId = -1;
        this.name = name;
        this.eventType = eventType;
    }

    @Override
    protected final void notify( AudioSystem listener ) {
        switch ( eventType ) {
            case PLAY_SOUND : {
                if ( soundId >= 0 ) {
                    listener.playSound( soundId ); 
                } else {
                    listener.playSound( listener.sounds.getId( name ) ); 
                }
                break;
            }
            case STOP_PLAYING : {
                if ( soundId >= 0 ) {
                    listener.stopPlaying( soundId ); 
                } else {
                    listener.stopPlaying( listener.sounds.getId( name ) ); 
                }
                break;
            }
        }
    }

}
