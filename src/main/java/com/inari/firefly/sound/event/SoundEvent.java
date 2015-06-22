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
