package com.inari.firefly.graphics.text;

import java.util.Arrays;
import java.util.Set;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.FFGraphics;
import com.inari.firefly.system.external.SpriteData;
import com.inari.firefly.system.external.TextureData;
import com.inari.firefly.system.utils.Disposable;

public final class FontAsset extends Asset implements TextureData {
    
    public static final AttributeKey<String> TEXTURE_RESOURCE_NAME = new AttributeKey<String>( "fontTextureId", String.class, FontAsset.class );
    public static final AttributeKey<char[][]> CHAR_TEXTURE_MAP = new AttributeKey<char[][]>( "charTextureMap", char[][].class, FontAsset.class );
    public static final AttributeKey<Integer> CHAR_WIDTH = new AttributeKey<Integer>( "charWidth", Integer.class, FontAsset.class );
    public static final AttributeKey<Integer> CHAR_HEIGHT = new AttributeKey<Integer>( "charHeight", Integer.class, FontAsset.class );
    public static final AttributeKey<Integer> CHAR_SPACE = new AttributeKey<Integer>( "charSpace", Integer.class, FontAsset.class );
    public static final AttributeKey<Integer> LINE_SPACE = new AttributeKey<Integer>( "lineSpace", Integer.class, FontAsset.class );
    public static final AttributeKey<Integer> DEFAULT_CHAR = new AttributeKey<Integer>( "defaultChar", Integer.class, FontAsset.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        TEXTURE_RESOURCE_NAME,
        CHAR_TEXTURE_MAP,
        CHAR_WIDTH,
        CHAR_HEIGHT,
        CHAR_HEIGHT,
        LINE_SPACE,
        DEFAULT_CHAR
    };

    private String textureResourceName;
    private int textureWidth;
    private int textureHeight;
    
    private char[][] charTextureMap;
    private int charWidth;
    private int charHeight;
    private int charSpace;
    private int lineSpace;
    private int defaultChar;
    
    private IntBag charSpriteMap;
    private int textureId = -1;
    
    FontAsset( int id ) {
        super( id );
        charTextureMap = null;
        charSpriteMap = new IntBag( 256, -1 );
        charWidth = 0;
        charHeight = 0;
        charSpace = 0;
        lineSpace = 0;
        defaultChar = -1;
    }

    @Override
    public final int getInstanceId( int index ) {
        throw new UnsupportedOperationException();
    }

    public final int getSpriteId( char character ) {
        int spriteId = charSpriteMap.get( character );
        if ( spriteId >= 0 ) {
            return spriteId;
        }
        
        return charSpriteMap.get( defaultChar );
    }
    
    @Override
    public final String getResourceName() {
        return textureResourceName;
    }

    public final String getTextureResourceName() {
        return textureResourceName;
    }
    
    public final void setTextureResourceName( String textureResourceName ) {
        this.textureResourceName = textureResourceName;
    }

    public final int getTextureWidth() {
        return textureWidth;
    }

    @Override
    public final void setTextureWidth( int textureWidth ) {
        this.textureWidth = textureWidth;
    }

    public final int getTextureHeight() {
        return textureHeight;
    }

    @Override
    public final void setTextureHeight( int textureHeight ) {
        this.textureHeight = textureHeight;
    }

    public final char[][] getCharTextureMap() {
        return charTextureMap;
    }

    public final void setCharTextureMap( char[][] charTextureMap ) {
        this.charTextureMap = charTextureMap;
    }

    public final int getCharWidth() {
        return charWidth;
    }

    public final void setCharWidth( int charWidth ) {
        this.charWidth = charWidth;
    }

    public final int getCharHeight() {
        return charHeight;
    }

    public final void setCharHeight( int charHeight ) {
        this.charHeight = charHeight;
    }

    public final int getCharSpace() {
        return charSpace;
    }

    public final void setCharSpace( int charSpace ) {
        this.charSpace = charSpace;
    }

    public final int getLineSpace() {
        return lineSpace;
    }

    public final void setLineSpace( int lineSpace ) {
        this.lineSpace = lineSpace;
    }

    public final int getDefaultChar() {
        return defaultChar;
    }

    public final void setDefaultChar( int defaultChar ) {
        this.defaultChar = defaultChar;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( Arrays.asList( ATTRIBUTE_KEYS ) );
        return attributeKeys;
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        textureResourceName = attributes.getValue( TEXTURE_RESOURCE_NAME, textureResourceName );
        charTextureMap = attributes.getValue( CHAR_TEXTURE_MAP, charTextureMap );
        charWidth = attributes.getValue( CHAR_WIDTH, charWidth );
        charHeight = attributes.getValue( CHAR_HEIGHT, charHeight );
        charSpace = attributes.getValue( CHAR_SPACE, charSpace );
        lineSpace = attributes.getValue( LINE_SPACE, lineSpace );
        defaultChar = attributes.getValue( DEFAULT_CHAR, defaultChar );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );

        attributes.put( TEXTURE_RESOURCE_NAME, textureResourceName );
        attributes.put( CHAR_TEXTURE_MAP, charTextureMap );
        attributes.put( CHAR_WIDTH, charWidth );
        attributes.put( CHAR_HEIGHT, charHeight );
        attributes.put( CHAR_SPACE, charSpace );
        attributes.put( LINE_SPACE, lineSpace );
        attributes.put( DEFAULT_CHAR, defaultChar );
    }

    @Override
    public final Disposable load( FFContext context ) {
        if ( loaded ) {
            return this;
        }
        
        FFGraphics graphics = context.getGraphics();
        
        textureId = graphics.createTexture( this );
        Rectangle textureRegion = new Rectangle( 0, 0, getCharWidth(), getCharHeight() );
        InternalSpriteData spriteData = new InternalSpriteData( this, textureRegion );
        
        for ( int y = 0; y < charTextureMap.length; y++ ) {
            for ( int x = 0; x < charTextureMap[ y ].length; x++ ) {
                textureRegion.x = x * charWidth;
                textureRegion.y = y * charHeight;
                
                int charSpriteId = graphics.createSprite( spriteData );
                charSpriteMap.set( charTextureMap[ y ][ x ], charSpriteId );
            }
        }
        
        return this;
    }

    @Override
    public final void dispose( FFContext context ) {
        if ( !loaded ) {
            return;
        }
        
        FFGraphics graphics = context.getGraphics();
        
        IntIterator iterator = charSpriteMap.iterator();
        while ( iterator.hasNext() ) {
            graphics.disposeSprite( iterator.next() );
        }
        charSpriteMap.clear();
        
        graphics.disposeTexture( textureId );
        textureId = -1;
    }
    
    private final class InternalSpriteData implements SpriteData {
        
        private final FontAsset fontAsset;
        private final Rectangle region;

        public InternalSpriteData( FontAsset fontAsset, Rectangle region ) {
            super();
            this.fontAsset = fontAsset;
            this.region = region;
        }

        @Override
        public final int getTextureId() {
            return fontAsset.textureId;
        }

        @Override
        public final Rectangle getTextureRegion() {
            return region;
        }

        @Override
        public final <A> A getDynamicAttribute( AttributeKey<A> key ) {
            return fontAsset.getDynamicAttribute( key );
        }
    }

}
