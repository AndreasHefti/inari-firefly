package com.inari.firefly.system;

import com.inari.firefly.asset.event.AssetEventListener;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.event.SoundEventListener;
import com.inari.firefly.sprite.SpriteRenderable;
import com.inari.firefly.system.event.ViewEventListener;

public interface ILowerSystemFacade extends FFSystem, AssetEventListener, ViewEventListener, SoundEventListener {
    
    void startRendering( View view );
    
    void renderSprite( SpriteRenderable renderableSprite, ETransform transform );
    
    void renderSprite( SpriteRenderable renderableSprite, float xpos, float ypos );
    
    void endRendering( View view );
    
    void flush();
    
    void soundAttributesChanged( Sound sound );
    
    Input getInput();

    int getScreenWidth();

    int getScreenHeight();

}
