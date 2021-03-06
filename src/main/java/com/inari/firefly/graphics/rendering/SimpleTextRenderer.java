package com.inari.firefly.graphics.rendering;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.aspect.IAspects;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArrayRO;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.graphics.BlendMode;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.text.EText;
import com.inari.firefly.graphics.text.FontAsset;
import com.inari.firefly.system.component.SystemComponentType;
import com.inari.firefly.system.external.FFTimer;

public final class SimpleTextRenderer extends Renderer {
    
    public static final SystemComponentType COMPONENT_TYPE = new SystemComponentType( Renderer.TYPE_KEY, SimpleTextRenderer.class );
    public static final RenderingChain.RendererKey CHAIN_KEY = new RenderingChain.RendererKey( "SimpleTextRenderer", SimpleTextRenderer.class );
    public static final Aspects MATCHING_ASPECTS = EntityComponent.ASPECT_GROUP.createAspects( 
        ETransform.TYPE_KEY, 
        EText.TYPE_KEY 
    );
    
    private final ExactTransformDataCollector renderingTransform = new ExactTransformDataCollector();

    protected SimpleTextRenderer( int index ) {
        super( index );
    }

    @Override
    public final boolean match( IAspects aspects ) {
        return aspects.include( MATCHING_ASPECTS );
    }

    @Override
    public final void render( int viewId, int layerId, final Rectangle clip, final FFTimer timer ) {
        DynArrayRO<IndexedTypeSet> texts = getEntites( viewId, layerId, false );
        if ( texts == null ) {
            return;
        }
        
        for ( int i = 0; i < texts.capacity(); i++ ) {
            IndexedTypeSet components = texts.get( i );
            if ( components == null ) {
                continue;
            }
            
            EText text = components.get( EText.TYPE_KEY );
            ETransform transform = components.get( ETransform.TYPE_KEY );
            FontAsset font = context.getSystemComponent( Asset.TYPE_KEY, text.getFontAssetId(), FontAsset.class );
            
            char[] chars = text.getText().toCharArray();
            textRenderable.blendMode = text.getBlendMode();
            textRenderable.tintColor = text.getTintColor();
            textRenderable.shaderId = text.getShaderId();
            renderingTransform.set( transform );
            float horizontalStep = ( font.getCharWidth() + font.getCharSpace() ) * transform.getScaleX();
            float verticalStep = ( font.getCharHeight() + font.getLineSpace() ) * transform.getScaleY();
            
            for ( char character : chars ) {
                if ( character == '\n' ) {
                    renderingTransform.xpos = transform.getXpos();
                    renderingTransform.ypos += verticalStep;
                    continue;
                }
                
                if ( character == ' ' ) {
                    renderingTransform.xpos += horizontalStep;
                    continue;
                }

                textRenderable.spriteId = font.getSpriteId( character );
                graphics.renderSprite( textRenderable, renderingTransform );
                renderingTransform.xpos += horizontalStep;
            }
        }
    }
    
    final TextRenderable textRenderable = new TextRenderable();
    final class TextRenderable implements SpriteRenderable {
        
        int spriteId;
        RGBColor tintColor; 
        BlendMode blendMode;
        int shaderId;

        @Override public final int getSpriteId() { return spriteId; }
        @Override public final RGBColor getTintColor() { return tintColor; }
        @Override public final BlendMode getBlendMode() { return blendMode; }
        @Override public final int getShaderId() { return shaderId; }
    }

}
