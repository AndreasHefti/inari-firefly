package com.inari.firefly.system;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.inari.firefly.FFContext;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.sprite.SpriteRenderable;

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
        
        @SuppressWarnings( "unused" )
        public TestLowerSystemFacade( FFContext context ) {
            
        }

        @Override
        public void onAssetLoad( Asset asset ) {

        }

        @Override
        public void onAssetDispose( Asset asset ) {

        }

        @Override
        public void onViewCreated( View view ) {

        }

        @Override
        public void onViewDisabled( View view ) {

        }

        @Override
        public void startRendering( View view ) {

        }

        @Override
        public void renderSprite( SpriteRenderable renderableSprite, ETransform transform ) {
        }

        @Override
        public void flush( View view ) {

        }

        @Override
        public void finish() {

        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append( "TestLowerSystemFacade []" );
            return builder.toString();
        }

    }

}
