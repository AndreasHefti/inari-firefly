package com.inari.firefly.graphics.rendering;

import com.inari.commons.geom.PositionF;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.aspect.IAspects;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArrayRO;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.sprite.ESprite;
import com.inari.firefly.graphics.sprite.ESpriteMultiplier;
import com.inari.firefly.system.component.SystemComponentType;
import com.inari.firefly.system.external.FFTimer;

public final class MultiPositionSpriteRenderer extends Renderer {
    
    public static final SystemComponentType COMPONENT_TYPE = new SystemComponentType( Renderer.TYPE_KEY, MultiPositionSpriteRenderer.class );
    public static final RenderingChain.RendererKey CHAIN_KEY = new RenderingChain.RendererKey( "MultiPositionSpriteRenderer", MultiPositionSpriteRenderer.class );
    public static final Aspects MATCHING_ASPECTS = EntityComponent.ASPECT_GROUP.createAspects( 
        ETransform.TYPE_KEY, 
        ESprite.TYPE_KEY,
        ESpriteMultiplier.TYPE_KEY
    );

    protected MultiPositionSpriteRenderer( int index ) {
        super( index );
        setName( CHAIN_KEY.name );
    }

    @Override
    public final boolean match( IAspects aspects ) {
        return aspects.include( MATCHING_ASPECTS );
    }

    @Override
    public final void render( int viewId, int layerId, final Rectangle clip, final FFTimer timer ) {
        final DynArrayRO<IndexedTypeSet> spritesToRender = getEntites( viewId, layerId, false );
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
            final DynArrayRO<PositionF> positions = multiplier.getPositions();
            
            for ( int p = 0; p < positions.capacity(); p++ ) {
                PositionF pos = positions.get( p );
                if ( pos == null ) {
                    continue;
                }
                
                transformCollector.set( transform, pos.x, pos.y );
                graphics.renderSprite( sprite, transformCollector );
            }
        }
    }

}
