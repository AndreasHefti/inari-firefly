package com.inari.firefly.sound.event;

import com.inari.commons.event.Event;

public final class SoundEvent extends Event<SoundEventListener> {
    
    public enum Type {
        PLAY_SOUND,
        STOP_PLAYING,
    }
    
    public final int soundId;

    public SoundEvent( int soundId ) {
        this.soundId = soundId;
    }

    @Override
    public final void notify( SoundEventListener listener ) {
        listener.onSoundEvent( this );
    }

}
