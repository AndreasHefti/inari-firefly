package com.inari.firefly;

import java.util.ArrayList;
import java.util.Collection;

import com.inari.firefly.asset.event.AssetEvent;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.FFAudio;

public class FFAudioMock implements FFAudio {
    
    private final Collection<String> loadedAssets = new ArrayList<String>();

    private final Collection<String> log = new ArrayList<String>();
    
    @Override
    public void init( FFContext context ) {
        context.registerListener( AssetEvent.class, this );
    }
    
    @Override
    public void dispose( FFContext context ) {
        context.disposeListener( AssetEvent.class, this );
        
        clear();
    }
    
    public void clear() {
        loadedAssets.clear();
        log.clear();
    }
    
    @Override
    public void onAssetEvent( AssetEvent event ) {
        switch ( event.eventType ) {
            case ASSET_LOADED: {
                loadedAssets.add( event.asset.getName() );
                break;
            }
            case ASSET_DISPOSED: 
            case ASSET_DELETED: {
                loadedAssets.remove( event.asset.getName() );
            }
            default: {}
        }
    }
    
    @Override
    public long playSound( int soundId, int chanel, boolean looping, float volume, float pitch, float pan ) {
        log.add( "playSound" );
        return soundId;
    }

    @Override
    public void changeSound( int soundId, long instanceId, float volume, float pitch, float pan ) {
        log.add( "changeSound" );
    }

    @Override
    public void stopSound( int soundId, long instanceId ) {
        log.add( "stopSound" );
    }

    @Override
    public void playMusic( int soundId, boolean looping, float volume, float pan ) {
        log.add( "playMusic" );
    }

    @Override
    public void changeMusic( int soundId, float volume, float pan ) {
        log.add( "changeMusic" );
    }

    @Override
    public void stopMusic( int soundId ) {
        log.add( "stopMusic" );
    }
}
