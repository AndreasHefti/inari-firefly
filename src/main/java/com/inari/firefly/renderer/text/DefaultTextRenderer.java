package com.inari.firefly.renderer.text;

import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.renderer.BlendMode;
import com.inari.firefly.renderer.SpriteRenderable;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.RenderEvent;

public final class DefaultTextRenderer extends TextRenderer {
    
    private static boolean singletonInstance = false;

    protected DefaultTextRenderer( int id, FFContext context ) {
        super( id, context );
        if ( singletonInstance ) {
            throw new FFInitException( "There is already an instance of DefaultTextRenderer and DefaultTextRenderer is a singleton" );
        }
        singletonInstance = true;
    }

    @Override
    public final void render( RenderEvent event ) {
        int viewId = event.getViewId();
        int layerId = event.getLayerId();
        
        if ( !textSystem.hasTexts( viewId ) ) {
            return;
        } 
        
        DynArray<IndexedTypeSet> textsToRender = textSystem.getTexts( viewId, layerId );
        if ( textsToRender == null ) {
            return;
        }
        
        for ( int i = 0; i < textsToRender.capacity(); i++ ) {
            IndexedTypeSet components = textsToRender.get( i );
            if ( components == null ) {
                continue;
            }
            
            EText text = components.get( EText.TYPE_KEY );
            int rendererId = text.getRendererId();
            if ( rendererId >= 0 && rendererId != getId() ) {
                continue;
            }
            
            ETransform transform = components.get( ETransform.TYPE_KEY );
            FontAsset font = assetSystem.getAssetAs( text.getFontId(), FontAsset.class );
            
            char[] chars = text.getText();
            textRenderable.blendMode = text.getBlendMode();
            textRenderable.tintColor = text.getTintColor();
            transformCollector.set( transform );
            int horizontalStep = font.getCharWidth() + font.getCharSpace();
            int verticalStep = font.getCharHeight() + font.getLineSpace();
            
            for ( char character : chars ) {
                if ( character == '\n' ) {
                    transformCollector.xpos = transform.getXpos();
                    transformCollector.ypos += verticalStep;
                    continue;
                }

                textRenderable.spriteId = font.getSpriteId( character );
                render( textRenderable );
                transformCollector.xpos += horizontalStep;
            }
        }
    }

    final TextRenderable textRenderable = new TextRenderable();
    
    final class TextRenderable implements SpriteRenderable {
        
        int spriteId;
        RGBColor tintColor; 
        BlendMode blendMode;

        @Override
        public int getSpriteId() {
            return spriteId;
        }

        @Override
        public RGBColor getTintColor() {
            return tintColor;
        }

        @Override
        public BlendMode getBlendMode() {
            return blendMode;
        }

        @Override
        public int getOrdering() {
            return 0;
        }
    }

}
