package com.inari.firefly.graphics.particle;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.graphics.rendering.RenderingChain.RendererKey;

public final class EParticle extends EntityComponent {
    
    public static final EntityComponentTypeKey<EParticle> TYPE_KEY = EntityComponentTypeKey.create( EParticle.class );
    
    public static final AttributeKey<RendererKey> RENDERER_KEY = new AttributeKey<>( "rendererKey", RendererKey.class, EParticle.class );
    public static final AttributeKey<DynArray<Particle>> PARTICLE = AttributeKey.createDynArray( "particle", EParticle.class, Particle.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        RENDERER_KEY,
        PARTICLE
    );
    
    private RendererKey rendererKey;
    private final DynArray<Particle> particle;

    EParticle() {
        super( TYPE_KEY );
        particle = DynArray.create( Particle.class, 20 );
        resetAttributes();
    }

    public final RendererKey getRendererKey() {
        return rendererKey;
    }

    public final void setRendererKey( RendererKey rendererKey ) {
        this.rendererKey = rendererKey;
    }
    
    public final boolean rendererMatch( RendererKey chainKey ) {
        return rendererKey == chainKey ;
    }

    public final DynArray<Particle> getParticle() {
        return particle;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return ATTRIBUTE_KEYS;
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        rendererKey = attributes.getValue( RENDERER_KEY, rendererKey );
        particle.clear();
        if ( attributes.contains( PARTICLE ) ) {
            particle.addAll( attributes.getValue( PARTICLE ) );
        }
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( RENDERER_KEY, rendererKey );
        attributes.put( PARTICLE, particle );
    }

    @Override
    public final void resetAttributes() {
        rendererKey = null;
        particle.clear();
    }

}
