package com.inari.firefly.renderer.text;

import java.util.Arrays;
import java.util.Set;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.component.SystemComponent;

public class Font extends SystemComponent {
    
    public static final SystemComponentKey<Font> TYPE_KEY = SystemComponentKey.create( Font.class );
    
    public static final AttributeKey<String> FONT_TEXTURE_RESOURCE_NAME = new AttributeKey<String>( "fontTextureId", String.class, Font.class );
    public static final AttributeKey<char[][]> CHAR_TEXTURE_MAP = new AttributeKey<char[][]>( "charTextureMap", char[][].class, Font.class );
    public static final AttributeKey<Integer> CHAR_WIDTH = new AttributeKey<Integer>( "charWidth", Integer.class, Font.class );
    public static final AttributeKey<Integer> CHAR_HEIGHT = new AttributeKey<Integer>( "charHeight", Integer.class, Font.class );
    public static final AttributeKey<Integer> CHAR_SPACE = new AttributeKey<Integer>( "charSpace", Integer.class, Font.class );
    public static final AttributeKey<Integer> LINE_SPACE = new AttributeKey<Integer>( "lineSpace", Integer.class, Font.class );
    public static final AttributeKey<Integer> DEFAULT_CHAR = new AttributeKey<Integer>( "defaultChar", Integer.class, Font.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        FONT_TEXTURE_RESOURCE_NAME,
        CHAR_TEXTURE_MAP,
        CHAR_WIDTH,
        CHAR_HEIGHT,
        CHAR_HEIGHT,
        LINE_SPACE,
        DEFAULT_CHAR
    };

    private String fontTextureResourceName;
    private char[][] charTextureMap;
    private int charWidth;
    private int charHeight;
    private int charSpace;
    private int lineSpace;
    private int defaultChar;
    
    private IntBag charSpriteMap;
    
    protected Font( int id ) {
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
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }
    
    final void setCharSpriteMapping( char character, int spriteId ) {
        charSpriteMap.set( character, spriteId );
    }
    
    public final int getSpriteId( char character ) {
        int spriteId = charSpriteMap.get( character );
        if ( spriteId >= 0 ) {
            return spriteId;
        }
        
        return charSpriteMap.get( defaultChar );
    }

    public final String getFontTextureResourceName() {
        return fontTextureResourceName;
    }

    public final void setFontTextureResourceName( String fontTextureResourceName ) {
        this.fontTextureResourceName = fontTextureResourceName;
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
        
        fontTextureResourceName = attributes.getValue( FONT_TEXTURE_RESOURCE_NAME, fontTextureResourceName );
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

        attributes.put( FONT_TEXTURE_RESOURCE_NAME, fontTextureResourceName );
        attributes.put( CHAR_TEXTURE_MAP, charTextureMap );
        attributes.put( CHAR_WIDTH, charWidth );
        attributes.put( CHAR_HEIGHT, charHeight );
        attributes.put( CHAR_SPACE, charSpace );
        attributes.put( LINE_SPACE, lineSpace );
        attributes.put( DEFAULT_CHAR, defaultChar );
    }

}
