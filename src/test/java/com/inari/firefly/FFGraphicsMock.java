package com.inari.firefly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.graphics.BlendMode;
import com.inari.firefly.graphics.ShaderAsset;
import com.inari.firefly.graphics.SpriteRenderable;
import com.inari.firefly.graphics.TextureAsset;
import com.inari.firefly.graphics.shape.EShape;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.FFGraphics;
import com.inari.firefly.system.external.TransformData;
import com.inari.firefly.system.view.View;
import com.inari.firefly.system.view.ViewEvent;

public class FFGraphicsMock implements FFGraphics {
    
    private final DynArray<String> loadedAssets = new DynArray<String>();
    private final Collection<String> views = new ArrayList<String>();
    
    private final Collection<String> log = new ArrayList<String>();

    @Override
    public void init( FFContext context ) {
        context.registerListener( ViewEvent.class, this );
    }
    
    @Override
    public void dispose( FFContext context ) {
        context.disposeListener( ViewEvent.class, this );
        
        clear();
    }

    public void clear() {
        loadedAssets.clear();
        views.clear();
        log.clear();
    }
    
    @Override
    public int createTexture( String resourceName ) {
        return loadedAssets.add( resourceName );
    }

    @Override
    public int createTexture( TextureAsset textureAsset ) {
        return createTexture( textureAsset.getName() );
    }

    @Override
    public void disposeTexture( int textureId ) {
        loadedAssets.remove( textureId );
    }

    @Override
    public int createSprite( int textureId, Rectangle textureRegion ) {
        return loadedAssets.add( "sprite:"+ textureRegion );
    }

    @Override
    public int createSprite( SpriteAsset spriteAsset ) {
        return loadedAssets.add( spriteAsset.getName() );
    }

    @Override
    public void disposeSprite( int spriteId ) {
        loadedAssets.remove( spriteId );
    }

    @Override
    public int createShader( String shaderProgram ) {
        return loadedAssets.add( shaderProgram );
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
        switch ( event.eventType ) {
            case VIEW_CREATED: {
                views.add( event.view.getName() );
                break;
            }
            case VIEW_DELETED: {
                views.remove( event.view.getName() );
            }
            default: {}
        }
    }

    @Override
    public void startRendering( View view ) {
        log.add( "startRendering::View(" + view.getName() + ")" );
    }
    
    @Override
    public final void renderSprite( SpriteRenderable spriteRenderable, float xpos, float ypos ) {
        log.add( "renderSprite::Sprite(" + spriteRenderable.getSpriteId() + ")" );
    }
    
    @Override
    public final void renderSprite( SpriteRenderable spriteRenderable, TransformData transformData ) {
        log.add( "renderSprite::Sprite(" + spriteRenderable.getSpriteId() + ")" );
    }

    @Override
    public void renderShape( EShape.Type type, float[] vertices, int segments, DynArray<RGBColor> colors, BlendMode blendMode, boolean fill ) {
        log.add( "renderShape::type="+type+" vertices="+vertices+" segments="+segments+" colors="+colors+" blendMode="+blendMode+" fill="+fill );
    }
    
    @Override
    public final void renderShape( EShape.Type type, float[] vertices, int segments, DynArray<RGBColor> colors, BlendMode blendMode, boolean fill, TransformData transformData ) {
        log.add( "renderShape::type="+type+" vertices="+vertices+" segments="+segments+" colors="+colors+" blendMode="+blendMode+" fill="+fill );
    }

    @Override
    public void endRendering( View view ) {
        log.add( "endRendering::View(" + view.getName() + ")" );
    }

    @Override
    public void flush( Iterator<View> virtualViews ) {
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

}
