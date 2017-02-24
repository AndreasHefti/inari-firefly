package com.inari.firefly.system;

import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.graphics.BlendMode;
import com.inari.firefly.graphics.SpriteRenderable;
import com.inari.firefly.graphics.text.FontAsset;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.graphics.view.ViewSystem;
import com.inari.firefly.system.external.FFGraphics;
import com.inari.firefly.system.external.ShapeData;
import com.inari.firefly.system.info.SystemInfo;
import com.inari.firefly.system.info.SystemInfoDisplay;

final class SystemInfoDisplayImpl implements SystemInfoDisplay, PostRenderEventListener {

    private final FFContext context;
    private final FFGraphics graphics;
    
    boolean active = false;
    final DynArray<SystemInfo> infos = DynArray.create( SystemInfo.class, 10, 10 );
    final StringBuffer textbuffer = new StringBuffer();
    
    private int width = 0;
    private int horizontalStep;
    private int verticalStep;
    FontAsset defaultFontAsset = null;

    SystemInfoDisplayImpl( FFContext context ) {
        this.context = context;
        graphics = context.getGraphics();
    }

    @Override
    public final boolean isActive() {
        return active;
    }

    @Override
    public final SystemInfoDisplay setActive( boolean active ) {
        if ( defaultFontAsset == null ) {
            defaultFontAsset = context.getSystemComponent( Asset.TYPE_KEY, FFContext.DEFAULT_FONT, FontAsset.class );
            if ( defaultFontAsset == null ) {
                throw new FFInitException( "No Asset for default font found" );
            }
            if ( !defaultFontAsset.isLoaded() ) {
                defaultFontAsset.load( context );
            }
            horizontalStep = defaultFontAsset.getCharWidth() + defaultFontAsset.getCharSpace();
            verticalStep = defaultFontAsset.getCharHeight() + defaultFontAsset.getLineSpace();
        }
        
        this.active = active;
        
        if ( active ) {
            context.registerListener( PostRenderEvent.TYPE_KEY, this );
        } else {
            context.disposeListener( PostRenderEvent.TYPE_KEY, this );
        }
        
        return this;
    }
    
    @Override
    public final SystemInfoDisplay addSystemInfo( SystemInfo systemInfo ) {
        infos.add( systemInfo );
        textbuffer.append( new char[ systemInfo.getLength() ]  );
        textbuffer.append( '\n' );
        if ( width < systemInfo.getLength() ) {
            width = systemInfo.getLength();
        }
        return this;
    }
    
    @Override
    public final void postRendering( FFContext context ) {
        View baseView = context.getSystemComponent( View.TYPE_KEY, ViewSystem.BASE_VIEW_ID );
        graphics.startRendering( baseView, false );
        renderSystemInfoDisplay();
        graphics.endRendering( baseView );
        graphics.flush( null );
    }
    
    final void update() {
        int startIndex = 0;
        
        for ( int i = 0; i < infos.capacity(); i++ ) {
            SystemInfo info = infos.get( i );
            if ( info == null ) {
                continue;
            }
            
            info.update( context, textbuffer, startIndex );
            startIndex += info.getLength() + 1;
        }
    }
    
    final CharSequence getInfo() {
        return textbuffer;
    }
    
    final void renderSystemInfoDisplay() {
        update();
        
        infoDisplayBackground.rectVertices[ 2 ] = width * horizontalStep + horizontalStep;
        infoDisplayBackground.rectVertices[ 3 ] = infos.size() * verticalStep + verticalStep;
        graphics.renderShape( infoDisplayBackground );
        
        int xpos = 5;
        int ypos = 5;

        for ( int i = 0; i < textbuffer.length(); i++ ) {
            char character = textbuffer.charAt( i );
            if ( character == '\n' ) {
                xpos = 0;
                ypos += verticalStep;
                continue;
            }

            textRenderable.spriteId = defaultFontAsset.getSpriteId( character );
            graphics.renderSprite( textRenderable, xpos, ypos );
            xpos += horizontalStep;
        }
    }
    
    private final SystemInfoDisplayImpl.TextRenderable textRenderable = new TextRenderable(); 
    private static final class TextRenderable implements SpriteRenderable {
        
        public int spriteId;
        public final RGBColor tintColor = new RGBColor( 1f, 1f, 1f, 1f ); 

        @Override public final int getSpriteId() { return spriteId; }
        @Override public final RGBColor getTintColor() { return tintColor; }
        @Override public final BlendMode getBlendMode() { return BlendMode.NORMAL_ALPHA; }
        @Override public final int getOrdering() { return 0; }
        @Override public final int getShaderId() { return -1; }
    }
    
    private final SystemInfoDisplayImpl.InfoDisplayBackground infoDisplayBackground =  new InfoDisplayBackground();
    private static final class InfoDisplayBackground implements ShapeData {

        public final DynArray<RGBColor> colors = DynArray.create( RGBColor.class, 4, 1 ); 
        public final float[] rectVertices = new float[] { 0, 0, 0, 0 };
        
        InfoDisplayBackground() { colors.add( new RGBColor( 0.8f, 0.8f, 0.8f, 0.5f ) ); }
        @Override public final Type getShapeType() { return ShapeData.Type.RECTANGLE; }
        @Override public final float[] getVertices() { return rectVertices; }
        @Override public final int getSegments() { return 0; }
        @Override public final DynArray<RGBColor> getColors() { return colors; }
        @Override public final BlendMode getBlendMode() { return BlendMode.NORMAL_ALPHA; }
        @Override public final boolean isFill() { return true; }
        @Override public int getShaderId() { return -1; }
        
    }

}