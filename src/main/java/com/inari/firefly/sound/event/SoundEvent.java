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
import com.inari.firefly.sound.Sound;

public final class SoundEvent extends Event<SoundEventListener> {
    
    public enum Type {
        PLAY_SOUND,
        STOP_PLAYING,
    }
    
    public final Sound sound;
    public final Type type;

    public SoundEvent( Sound sound, Type type ) {
        this.sound = sound;
        this.type = type;
    }

    @Override
    public final void notify( SoundEventListener listener ) {
        listener.onSoundEvent( this );
    }

}
