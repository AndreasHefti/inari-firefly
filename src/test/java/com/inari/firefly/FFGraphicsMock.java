package com.inari.firefly;

import java.util.ArrayList;
import java.util.Collection;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.graphics.ShaderAsset;
import com.inari.firefly.graphics.SpriteRenderable;
import com.inari.firefly.graphics.view.ActiveViewportProvider;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.graphics.view.ViewEvent;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.FFGraphics;
import com.inari.firefly.system.external.ShapeData;
import com.inari.firefly.system.external.SpriteData;
import com.inari.firefly.system.external.TextureData;
import com.inari.firefly.system.external.TransformData;

public class FFGraphicsMock implements FFGraphics {
    
    private final DynArray<String> loadedAssets = new DynArray<String>();
    private final Collection<String> views = new ArrayList<String>();
    
    private final Collection<String> log = new ArrayList<String>();

    @Override
    public void init( FFContext context ) {
        context.registerListener( ViewEvent.TYPE_KEY, this );
    }
    
    @Override
    public void dispose( FFContext context ) {
        context.disposeListener( ViewEvent.TYPE_KEY, this );
        
        clear();
    }

    public void clear() {
        loadedAssets.clear();
        views.clear();
        log.clear();
    }
    
    @Override
    public int createTexture( TextureData data ) {
        return loadedAssets.add( data.getResourceName() );
    }

    @Override
    public void disposeTexture( int textureId ) {
        loadedAssets.remove( textureId );
    }

    @Override
    public int createSprite( SpriteData data ) {
        return loadedAssets.add( "sprite:"+ data.getTextureId() + " : "+ data.getTextureRegion() );
    }

    @Override
    public void disposeSprite( int spriteId ) {
        loadedAssets.remove( spriteId );
    }

    @Override
    public int createShader( ShaderAsset shaderAsset ) {
        return loadedAssets.add( shaderAsset.getName() );
    }

    @Override
    public void disposeShader( int shaderId ) {
        loadedAssets.remove( shaderId );
    }

    @Override
    public void onViewEvent( ViewEvent event ) {
        switch ( event.getType() ) {
            case VIEW_CREATED: {
                views.add( event.getView().getName() );
                break;
            }
            case VIEW_DELETED: {
                views.remove( event.getView().getName() );
            }
            default: {}
        }
    }

    @Override
    public void startRendering( View view, boolean clear ) {
        log.add( "startRendering::View(" + view.getName() + ")" );
    }
    
    @Override
    public final void renderSprite( SpriteRenderable spriteRenderable, float xpos, float ypos ) {
        log.add( "renderSprite::Sprite(" + spriteRenderable.getSpriteId() + ")" );
    }
    
    @Override
    public final void renderSprite( SpriteRenderable spriteRenderable, float xpos, float ypos, float scale ) {
        log.add( "renderSprite::Sprite(" + spriteRenderable.getSpriteId() + ")" );
    }
    
    @Override
    public final void renderSprite( SpriteRenderable spriteRenderable, TransformData transformData ) {
        log.add( "renderSprite::Sprite(" + spriteRenderable.getSpriteId() + ")" );
    }

    @Override
    public void renderShape( ShapeData data ) {
        log.add( "renderShape:: " + data );
    }
    
    @Override
    public final void renderShape( ShapeData data, TransformData transformData ) {
        log.add( "renderShape:: " + data + " : " + transformData );
    }

    @Override
    public void endRendering( View view ) {
        log.add( "endRendering::View(" + view.getName() + ")" );
    }

    @Override
    public void flush( ActiveViewportProvider virtualViews ) {
        log.add( "flush" );
    }

    @Override
    public int getScreenWidth() {
        return 100;
    }

    @Override
    public int getScreenHeight() {
        return 100;
    }
    
    public String loadedAssets() {
        return loadedAssets.toString();
    }
    
    public String views() {
        return views.toString();
    }
    
    public String log() {
        return log.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "LowerSystemFacadeMock [loadedAssets=" );
        assetsToString( builder );
        builder.append( ", views=" );
        builder.append( views );
        builder.append( ", log=" );
        builder.append( log );
        builder.append( "]" );
        return builder.toString();
    }

    private void assetsToString( StringBuilder builder ) {
        builder.append( "[" );
        for ( String assetName : loadedAssets ) {
            builder.append( assetName ).append( "," );
        }
        
        if ( loadedAssets.size() > 0 ) {
            builder.deleteCharAt( builder.length() -1  );
        }
        
        builder.append( "]" );
    }

    @Override
    public byte[] getScreenshotPixels( Rectangle area ) {
        // TODO Auto-generated method stub
        return null;
    }

}
