package com.inari.firefly.graphics.text;

import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.system.RenderEvent;

public final class DefaultTextRenderer extends TextRenderer {
    
    protected final ExactTransformDataCollector transformCollector = new ExactTransformDataCollector();

    protected DefaultTextRenderer( int id ) {
        super( id );
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
            if ( rendererId >= 0 && rendererId != index() ) {
                continue;
            }
            
            ETransform transform = components.get( ETransform.TYPE_KEY );
            FontAsset font = assetSystem.getAssetAs( text.getFontAssetId(), FontAsset.class );
            
            char[] chars = text.getText().toCharArray();
            textRenderable.blendMode = text.getBlendMode();
            textRenderable.tintColor = text.getTintColor();
            textRenderable.shaderId = text.getShaderId();
            transformCollector.set( transform );
            float horizontalStep = ( font.getCharWidth() + font.getCharSpace() ) * transform.getScalex();
            float verticalStep = ( font.getCharHeight() + font.getLineSpace() ) * transform.getScaley();
            
            for ( char character : chars ) {
                if ( character == '\n' ) {
                    transformCollector.xpos = transform.getXpos();
                    transformCollector.ypos += verticalStep;
                    continue;
                }

                textRenderable.spriteId = font.getSpriteId( character );
                render( textRenderable, transformCollector );
                transformCollector.xpos += horizontalStep;
            }
        }
    }

    

}
