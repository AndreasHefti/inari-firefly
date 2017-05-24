package com.inari.firefly.graphics.rendering;

import com.inari.commons.geom.PositionF;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.sprite.ESprite;
import com.inari.firefly.graphics.sprite.ESpriteMultiplier;
import com.inari.firefly.system.RenderEvent;

public final class MultiPositionSpriteRenderer extends BaseSpriteRenderer {
    
    public static final RenderingChain.Key CHAIN_KEY = new RenderingChain.Key( "MultiPositionSpriteRenderer", MultiPositionSpriteRenderer.class );
    public static final Aspects MATCHING_ASPECTS = EntityComponent.ASPECT_GROUP.createAspects( 
        ETransform.TYPE_KEY, 
        ESprite.TYPE_KEY,
        ESpriteMultiplier.TYPE_KEY
    );

    protected MultiPositionSpriteRenderer( int index ) {
        super( index );
    }

    @Override
    public final boolean match( Aspects aspects ) {
        return aspects.include( MATCHING_ASPECTS );
    }

    @Override
    protected final boolean internalAccespt( int entityId, Aspects aspects ) {
        return true;
    }

    @Override
    protected final boolean internalDispose( int entityId, Aspects aspects ) {
        return true;
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

            final ESprite sprite = components.get( ESprite.TYPE_KEY );
            final ETransform transform = components.get( ETransform.TYPE_KEY );
            final ESpriteMultiplier multiplier = components.get( ESpriteMultiplier.TYPE_KEY );
            final DynArray<PositionF> positions = multiplier.getPositions();
            
            for ( int p = 0; p < positions.capacity(); p++ ) {
                PositionF pos = positions.get( p );
                if ( pos == null ) {
                    continue;
                }
                
                exactTransformCollector.set( transform, pos.x, pos.y );
                graphics.renderSprite( sprite, exactTransformCollector );
            }
        }
    }

}
