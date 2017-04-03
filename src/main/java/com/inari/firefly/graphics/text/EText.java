package com.inari.firefly.graphics.text;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.graphics.BlendMode;

public class EText extends EntityComponent {
    
    public static final EntityComponentTypeKey<EText> TYPE_KEY = EntityComponentTypeKey.create( EText.class );
    
    public static final AttributeKey<String> RENDERER_NAME = AttributeKey.createString( "rendererName", EText.class );
    public static final AttributeKey<Integer> RENDERER_ID = AttributeKey.createInt( "rendererId", EText.class );
    public static final AttributeKey<String> FONT_ASSET_NAME = AttributeKey.createString( "fontAssetName", EText.class );
    public static final AttributeKey<Integer> FONT_ASSET_ID = AttributeKey.createInt( "fontAssetId", EText.class );
    public static final AttributeKey<String> TEXT = AttributeKey.createString( "text", EText.class );
    public static final AttributeKey<RGBColor> TINT_COLOR = AttributeKey.createColor( "tintColor", EText.class );
    public static final AttributeKey<BlendMode> BLEND_MODE = AttributeKey.createBlendMode( "blendMode", EText.class );
    public static final AttributeKey<String> SHADER_ASSET_NAME = AttributeKey.createString( "shaderAssetName", EText.class );
    public static final AttributeKey<Integer> SHADER_ID = AttributeKey.createInt( "shaderId", EText.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        RENDERER_ID,
        FONT_ASSET_ID,
        TEXT,
        TINT_COLOR,
        BLEND_MODE,
        SHADER_ID
    );
    
    private int rendererId;
    private int fontAssetId;
    private StringBuffer textBuffer;
    private final RGBColor tintColor = new RGBColor();
    private BlendMode blendMode;
    private int shaderId;
    
    public EText() {
        super( TYPE_KEY );
        resetAttributes();
    }

    @Override
    public final void resetAttributes() {
        rendererId = -1;
        fontAssetId = -1;
        textBuffer = new StringBuffer();
        setTintColor( new RGBColor( 1f, 1f, 1f, 1f ) );
        blendMode = BlendMode.NONE;
        shaderId = -1;
    }

    public final int getRendererId() {
        return rendererId;
    }

    public final void setRendererId( int rendererId ) {
        this.rendererId = rendererId;
    }

    public final int getFontAssetId() {
        return fontAssetId;
    }

    public final void setFontAssetId( int fontAssetId ) {
        this.fontAssetId = fontAssetId;
    }
    
    public final StringBuffer getTextBuffer() {
        return textBuffer;
    }

    public final String getText() {
        return textBuffer.toString();
    }

    public final void setText( String text ) {
        textBuffer = new StringBuffer( text );
    }
    
    public final void appendText( String text ) {
        textBuffer.append( text );
    }
    
    public final void prependText( String text ) {
        textBuffer.insert( 0, text );
    }

    public final BlendMode getBlendMode() {
        return blendMode;
    }

    public final void setBlendMode( BlendMode blendMode ) {
        this.blendMode = blendMode;
    }
    
    public final int getShaderId() {
        return shaderId;
    }

    public final void setShaderId( int shaderId ) {
        this.shaderId = shaderId;
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
        return ATTRIBUTE_KEYS;
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        rendererId = attributes.getIdForName( RENDERER_NAME, RENDERER_ID, TextRenderer.TYPE_KEY, rendererId );
        fontAssetId = attributes.getIdForName( FONT_ASSET_NAME, FONT_ASSET_ID, Asset.TYPE_KEY, fontAssetId );
        if ( attributes.contains( TEXT ) ) {
            setText( attributes.getValue( TEXT ) );
        }
        setTintColor( attributes.getValue( TINT_COLOR, tintColor ) );
        blendMode = attributes.getValue( BLEND_MODE, blendMode );
        shaderId = attributes.getValue( SHADER_ID, shaderId );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( RENDERER_ID, rendererId );
        attributes.put( FONT_ASSET_ID, fontAssetId );
        attributes.put( TEXT, textBuffer.toString() );
        attributes.put( TINT_COLOR, new RGBColor( tintColor ) );
        attributes.put( BLEND_MODE, blendMode );
        attributes.put( SHADER_ID, shaderId );
    }

}
