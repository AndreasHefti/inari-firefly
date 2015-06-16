package com.inari.firefly;

import com.inari.firefly.asset.event.AssetEvent;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.sprite.SpriteRenderable;
import com.inari.firefly.system.ILowerSystemFacade;
import com.inari.firefly.system.View;
import com.inari.firefly.system.event.ViewEvent;

public class LowerSystemFacadeMock implements ILowerSystemFacade {

    @Override
    public void onAssetEvent( AssetEvent event ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onViewEvent( ViewEvent event ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void dispose( FFContext context ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void startRendering( View view ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void renderSprite( SpriteRenderable renderableSprite, ETransform transform ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void renderSprite( SpriteRenderable renderableSprite, float xpos, float ypos ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void endRendering( View view ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void flush() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getScreenWidth() {
        return 100;
    }

    @Override
    public int getScreenHeight() {
        return 100;
    }

    

}
