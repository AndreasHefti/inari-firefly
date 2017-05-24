package com.inari.firefly.graphics.rendering;

import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.sprite.ESprite;
import com.inari.firefly.system.RenderEvent;

public final class SimpleSpriteRenderer extends Renderer {
    
    public static final RenderingChain.Key CHAIN_KEY = new RenderingChain.Key( "SimpleSpriteRenderer", SimpleSpriteRenderer.class );
    public static final Aspects MATCHING_ASPECTS = EntityComponent.ASPECT_GROUP.createAspects( 
        ETransform.TYPE_KEY, 
        ESprite.TYPE_KEY 
    );

    protected SimpleSpriteRenderer( int index ) {
        super( index );
        setName( CHAIN_KEY.name );
    }
    
    @Override
    public final boolean match( Aspects aspects ) {
        return aspects.include( MATCHING_ASPECTS );
    }

    @Override
    public final void render( RenderEvent event ) {
        final DynArray<IndexedTypeSet> spritesToRender = getSprites( event.getViewId(), event.getLayerId(), false );
        if ( spritesToRender == null ) {
            return;
        }
        
        for ( int i = 0; i < spritesToRender.capacity(); i++ ) {
            final IndexedTypeSet components = spritesToRender.get( i );
            if ( components == null ) {
                continue;
            }

            graphics.renderSprite( 
                components.<ESprite>get( ESprite.TYPE_KEY ), 
                components.<ETransform>get( ETransform.TYPE_KEY ) 
            );
        }
    }

}
