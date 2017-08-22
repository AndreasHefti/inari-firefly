package com.inari.firefly.graphics.scene;

import com.inari.commons.lang.functional.Callback;

public interface SceneEventListener {
    
    void runScene( String sceneName, Callback callback );
    
    void pauseScene( String sceneName );
    
    void pauseAll();
    
    void resumeScene( String sceneName );
    
    void resumeAll();
    
    void stopScene( String sceneName );
    
    void stopAll();

}
