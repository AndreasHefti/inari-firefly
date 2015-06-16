package com.inari.firefly.system;

import com.inari.firefly.Disposable;
import com.inari.firefly.asset.event.AssetEventListener;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.sprite.SpriteRenderable;
import com.inari.firefly.system.event.ViewEventListener;

public interface ILowerSystemFacade extends AssetEventListener, ViewEventListener, Disposable {
    
    void startRendering( View view );
    
    void renderSprite( SpriteRenderable renderableSprite, ETransform transform );
    
    void renderSprite( SpriteRenderable renderableSprite, float xpos, float ypos );
    
    void endRendering( View view );
    
    void flush();

    int getScreenWidth();

    int getScreenHeight();

}
