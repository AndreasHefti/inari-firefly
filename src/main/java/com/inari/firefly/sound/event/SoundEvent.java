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
package com.inari.firefly.sound.event;

import com.inari.commons.event.Event;

public final class SoundEvent extends Event<SoundEventListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( SoundEvent.class );
    
    public enum Type {
        PLAY_SOUND,
        STOP_PLAYING
    }
    
    public final int soundId;
    public final String name;
    public final Type eventType;

    public SoundEvent( int soundId, Type eventType ) {
        super( TYPE_KEY );
        this.soundId = soundId;
        this.name = null;
        this.eventType = eventType;
    }
    
    public SoundEvent( String name, Type eventType ) {
        super( TYPE_KEY );
        this.soundId = -1;
        this.name = name;
        this.eventType = eventType;
    }

    @Override
    public final void notify( SoundEventListener listener ) {
        listener.onSoundEvent( this );
    }

}
