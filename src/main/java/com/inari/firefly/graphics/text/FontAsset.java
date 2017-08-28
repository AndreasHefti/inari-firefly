package com.inari.firefly.graphics.text;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.functional.IntFunction;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.graphics.TextureAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.SystemComponentType;
import com.inari.firefly.system.external.FFGraphics;
import com.inari.firefly.system.external.SpriteData;
import com.inari.firefly.system.external.TextureData;
import com.inari.firefly.system.utils.Disposable;

public final class FontAsset extends Asset implements TextureData {
    
    public static final SystemComponentType COMPONENT_TYPE = new SystemComponentType( Asset.TYPE_KEY, FontAsset.class );
    public static final AttributeKey<String> TEXTURE_RESOURCE_NAME = AttributeKey.createString( "textureResourceName", FontAsset.class );
    public static final AttributeKey<Boolean> MIP_MAP = AttributeKey.createBoolean( "mipmap", FontAsset.class );
    public static final AttributeKey<Integer> WRAP_S = AttributeKey.createInt( "wrapS", FontAsset.class );
    public static final AttributeKey<Integer> WRAP_T = AttributeKey.createInt( "wrapT", FontAsset.class );
    public static final AttributeKey<Integer> MIN_FILTER = AttributeKey.createInt( "minFilter", FontAsset.class );
    public static final AttributeKey<Integer> MAG_FILTER = AttributeKey.createInt( "magFilter", FontAsset.class );
    public static final AttributeKey<IntFunction> COLOR_CONVERTER = new AttributeKey<IntFunction>( "colorConverter", IntFunction.class, TextureAsset.class );
    public static final AttributeKey<char[][]> CHAR_TEXTURE_MAP = AttributeKey.create( "charTextureMap", char[][].class, FontAsset.class );
    public static final AttributeKey<Integer> CHAR_WIDTH = AttributeKey.createInt( "charWidth", FontAsset.class );
    public static final AttributeKey<Integer> CHAR_HEIGHT = AttributeKey.createInt( "charHeight", FontAsset.class );
    public static final AttributeKey<Integer> CHAR_SPACE = AttributeKey.createInt( "charSpace", FontAsset.class );
    public static final AttributeKey<Integer> LINE_SPACE = AttributeKey.createInt( "lineSpace", FontAsset.class );
    public static final AttributeKey<Integer> DEFAULT_CHAR = AttributeKey.createInt( "defaultChar", FontAsset.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        TEXTURE_RESOURCE_NAME,
        MIP_MAP,
        WRAP_S,
        WRAP_T,
        MIN_FILTER,
        MAG_FILTER,
        COLOR_CONVERTER,
        CHAR_TEXTURE_MAP,
        CHAR_WIDTH,
        CHAR_HEIGHT,
        CHAR_HEIGHT,
        LINE_SPACE,
        DEFAULT_CHAR
    );

    private String textureResourceName;
    private boolean mipmap;
    private int wrapS;
    private int wrapT;
    private int minFilter;
    private int magFilter;
    private IntFunction colorConverter;
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
        mipmap = false;
        wrapS = -1;
        wrapT = -1;
        minFilter = -1;
        magFilter = -1;
        colorConverter = null;
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
    
    public final boolean isMipmap() {
        return mipmap;
    }

    public final void setMipmap( boolean mipmap ) {
        this.mipmap = mipmap;
    }

    public final int getWrapS() {
        return wrapS;
    }

    public final void setWrapS( int wrapS ) {
        this.wrapS = wrapS;
    }

    public final int getWrapT() {
        return wrapT;
    }

    public final void setWrapT( int wrapT ) {
        this.wrapT = wrapT;
    }

    public final int getMinFilter() {
        return minFilter;
    }

    public final void setMinFilter( int minFilter ) {
        this.minFilter = minFilter;
    }

    public final int getMagFilter() {
        return magFilter;
    }

    public final void setMagFilter( int magFilter ) {
        this.magFilter = magFilter;
    }

    public final IntFunction getColorConverter() {
        return colorConverter;
    }

    public final void setColorConverter( IntFunction colorConverter ) {
        this.colorConverter = colorConverter;
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
        return JavaUtils.unmodifiableSet( super.attributeKeys(), ATTRIBUTE_KEYS );
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
        mipmap = attributes.getValue( MIP_MAP, mipmap );
        wrapS = attributes.getValue( WRAP_S, wrapS );
        wrapT = attributes.getValue( WRAP_T, wrapT );
        minFilter = attributes.getValue( MIN_FILTER, minFilter );
        magFilter = attributes.getValue( MAG_FILTER, magFilter );
        colorConverter = attributes.getValue( COLOR_CONVERTER, colorConverter );
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
        attributes.put( MIP_MAP, mipmap );
        attributes.put( WRAP_S, wrapS );
        attributes.put( WRAP_T, wrapT );
        attributes.put( MIN_FILTER, minFilter );
        attributes.put( MAG_FILTER, magFilter );
        attributes.put( COLOR_CONVERTER, colorConverter );
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
        public final boolean isHorizontalFlip() {
            return false;
        }

        @Override
        public final boolean isVerticalFlip() {
            return false;
        }
    }

}
