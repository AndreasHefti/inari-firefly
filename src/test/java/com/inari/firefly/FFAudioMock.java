package com.inari.firefly;

import java.util.ArrayList;
import java.util.Collection;

import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.FFAudio;

public class FFAudioMock implements FFAudio {
    
    private final Collection<String> loadedAssets = new ArrayList<String>();

    private final Collection<String> log = new ArrayList<String>();
    
    @Override
    public void init( FFContext context ) {
    }
    
    @Override
    public void dispose( FFContext context ) {
        clear();
    }
    
    public void clear() {
        loadedAssets.clear();
        log.clear();
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

    @Override
    public int createSound( SoundAsset asset ) {
        loadedAssets.add( asset.getName() );
        return asset.getId();
    }

    @Override
    public void disposeSound( SoundAsset asset ) {
        loadedAssets.remove( asset.getName() );
    }
}
