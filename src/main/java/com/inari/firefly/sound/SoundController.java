package com.inari.firefly.sound;

import com.inari.commons.event.IEventDispatcher;
import com.inari.firefly.FFContext;
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
        soundSystem = context.get( FFContext.System.SOUND_SYSTEM );
        lowerSystemFacade = context.get( FFContext.System.LOWER_SYSTEM_FACADE );
        eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
        
        eventDispatcher.register( SoundEvent.class, this );
    }

    @Override
    public final void dispose( FFContext context ) {
        eventDispatcher.unregister( SoundEvent.class, this );
    }

    @Override
    public final void onSoundEvent( SoundEvent event ) {
        switch ( event.type ) {
            case PLAY_SOUND: {
                if ( event.sound.getControllerId() == indexedId ) {
                    componentIds.add( event.sound.indexedId() );
                }
                break;
            } 
            case STOP_PLAYING: {
                componentIds.remove( event.sound.indexedId() );
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
