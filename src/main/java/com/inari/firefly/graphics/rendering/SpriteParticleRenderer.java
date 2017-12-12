package com.inari.firefly.graphics.rendering;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.aspect.IAspects;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArrayRO;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.particle.EParticle;
import com.inari.firefly.graphics.particle.Particle;
import com.inari.firefly.system.component.SystemComponentType;
import com.inari.firefly.system.external.FFTimer;

public final class SpriteParticleRenderer extends Renderer {
    
    public static final SystemComponentType COMPONENT_TYPE = new SystemComponentType( Renderer.TYPE_KEY, SpriteParticleRenderer.class );
    public static final RenderingChain.RendererKey CHAIN_KEY = new RenderingChain.RendererKey( "SpriteParticleRenderer", SpriteParticleRenderer.class );
    public static final Aspects MATCHING_ASPECTS = EntityComponent.ASPECT_GROUP.createAspects( 
        ETransform.TYPE_KEY,
        EParticle.TYPE_KEY 
    );

    protected SpriteParticleRenderer( int index ) {
        super( index );
        setName( CHAIN_KEY.name );
    }

    @Override
    public boolean match( IAspects aspects ) {
        return aspects.include( MATCHING_ASPECTS );
    }

    @Override
    public final void render( int viewId, int layerId, final Rectangle clip, final FFTimer timer ) {
        final DynArrayRO<IndexedTypeSet> particleToRender = getEntites( viewId, layerId, false );
        if ( particleToRender == null ) {
            return;
        }
        
        for ( int i = 0; i < particleToRender.capacity(); i++ ) {
            final IndexedTypeSet components = particleToRender.get( i );
            if ( components == null ) {
                continue;
            }

            final DynArrayRO<Particle> particle = components.<EParticle>get( EParticle.TYPE_KEY ).getParticle();
            final ETransform transform = components.get( ETransform.TYPE_KEY );
            for ( int ip = 0; ip < particle.capacity(); ip++ ) {
                final Particle p = particle.get( ip );
                if ( p == null ) {
                    continue;
                }
                
                transformCollector.set( transform );
                transformCollector.add( p );
                graphics.renderSprite( p, transformCollector );
            }
        }
    }

}
