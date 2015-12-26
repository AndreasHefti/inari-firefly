package com.inari.firefly.renderer.text;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.renderer.BlendMode;

public class EText extends EntityComponent {
    
    public static final EntityComponentTypeKey<EText> TYPE_KEY = EntityComponentTypeKey.create( EText.class );
    
    public static final AttributeKey<Integer> RENDERER_ID = new AttributeKey<Integer>( "rendererId", Integer.class, EText.class );
    public static final AttributeKey<Integer> FONT_ID = new AttributeKey<Integer>( "fontId", Integer.class, EText.class );
    public static final AttributeKey<char[]> TEXT = new AttributeKey<char[]>( "text", char[].class, EText.class );
    public static final AttributeKey<String> TEXT_STRING = new AttributeKey<String>( "text_string", String.class, EText.class );
    public static final AttributeKey<RGBColor> TINT_COLOR = new AttributeKey<RGBColor>( "tintColor", RGBColor.class, EText.class );
    public static final AttributeKey<BlendMode> BLEND_MODE = new AttributeKey<BlendMode>( "blendMode", BlendMode.class, EText.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        RENDERER_ID,
        FONT_ID,
        TEXT,
        TINT_COLOR,
        BLEND_MODE
    };
    
    private int rendererId;
    private int fontId;
    private char[] text;
    private final RGBColor tintColor = new RGBColor();
    private BlendMode blendMode;
    
    
    public EText() {
        super( TYPE_KEY );
        resetAttributes();
    }

    @Override
    public final void resetAttributes() {
        rendererId = -1;
        fontId = -1;
        text = null;
        setTintColor( new RGBColor( 1f, 1f, 1f, 1f ) );
        blendMode = BlendMode.NONE;
    }

    public final int getRendererId() {
        return rendererId;
    }

    public final void setRendererId( int rendererId ) {
        this.rendererId = rendererId;
    }

    public final int getFontId() {
        return fontId;
    }

    public final void setFontId( int fontId ) {
        this.fontId = fontId;
    }

    public final char[] getText() {
        return text;
    }

    public final void setText( char[] text ) {
        this.text = text;
    }

    public final BlendMode getBlendMode() {
        return blendMode;
    }

    public final void setBlendMode( BlendMode blendMode ) {
        this.blendMode = blendMode;
    }

    public final RGBColor getTintColor() {
        return tintColor;
    }
    
    public final void setTintColor( RGBColor tintColor ) {
        this.tintColor.r = tintColor.r;
        this.tintColor.g = tintColor.g;
        this.tintColor.b = tintColor.b;
        this.tintColor.a = tintColor.a;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        rendererId = attributes.getValue( RENDERER_ID, rendererId );
        fontId = attributes.getValue( FONT_ID, fontId );
        text = attributes.getValue( TEXT, text );
        if ( attributes.contains( TEXT_STRING ) ) {
            text = attributes.getValue( TEXT_STRING ).toCharArray();
        }
        setTintColor( attributes.getValue( TINT_COLOR, tintColor ) );
        blendMode = attributes.getValue( BLEND_MODE, blendMode );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( RENDERER_ID, rendererId );
        attributes.put( FONT_ID, fontId );
        attributes.put( TEXT, text );
        attributes.put( TINT_COLOR, new RGBColor( tintColor ) );
        attributes.put( BLEND_MODE, blendMode );
    }

}
