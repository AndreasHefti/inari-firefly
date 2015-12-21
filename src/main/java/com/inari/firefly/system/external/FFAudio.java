package com.inari.firefly.system.external;

import com.inari.firefly.asset.event.AssetEventListener;
import com.inari.firefly.system.FFContextInitiable;

public interface FFAudio extends FFContextInitiable, AssetEventListener {
    
    long playSound( int soundId, int channel, boolean looping, float volume, float pitch, float pan );
    
    void changeSound( int soundId, long instanceId, float volume, float pitch, float pan );
    
    void stopSound( int soundId, long instanceId );
    
    void playMusic( int soundId, boolean looping, float volume, float pan );
    
    void changeMusic( int soundId, float volume, float pan );
    
    void stopMusic( int soundId );

}
