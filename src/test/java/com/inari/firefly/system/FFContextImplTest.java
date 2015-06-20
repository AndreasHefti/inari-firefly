package com.inari.firefly.system;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.inari.firefly.FFContext;
import com.inari.firefly.asset.event.AssetEvent;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.sprite.SpriteRenderable;
import com.inari.firefly.system.event.ViewEvent;

public class FFContextImplTest {

    @Test
    public void testInit() {
        FFContextImpl context = new FFContextImpl( TestLowerSystemFacade.class );
        
        assertEquals( 
            "", 
            context.toString() 
        );
    }
    
    private static class TestLowerSystemFacade implements ILowerSystemFacade {

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
        public void renderSprite( SpriteRenderable renderableSprite,
                ETransform transform ) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void renderSprite( SpriteRenderable renderableSprite,
                float xpos, float ypos ) {
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
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getScreenHeight() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void init( FFContext context ) {
            // TODO Auto-generated method stub
            
        }

    }

}
