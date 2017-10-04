package com.inari.firefly.graphics.particle;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.graphics.BlendMode;
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

    public final EParticle setRendererKey( RendererKey rendererKey ) {
        this.rendererKey = rendererKey;
        return this;
    }
    
    public final boolean rendererMatch( RendererKey chainKey ) {
        return rendererKey == chainKey ;
    }
    
    public final EParticle addParticle( Particle particle ) {
        this.particle.add( particle );
        return this;
    }
    
    public final EParticle addParticle( int spriteId, RGBColor tintColor, BlendMode blendMode, float x, float y ) {
        this.particle.add( new Particle( spriteId, tintColor, blendMode, x, y ) );
        return this;
    }

    public final DynArray<Particle> getParticle() {
        return particle;
    }

    public final Set<AttributeKey<?>> attributeKeys() {
        return ATTRIBUTE_KEYS;
    }

    public final void fromAttributes( AttributeMap attributes ) {
        rendererKey = attributes.getValue( RENDERER_KEY, rendererKey );
        particle.clear();
        if ( attributes.contains( PARTICLE ) ) {
            particle.addAll( attributes.getValue( PARTICLE ) );
        }
    }

    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( RENDERER_KEY, rendererKey );
        attributes.put( PARTICLE, particle );
    }

    public final void resetAttributes() {
        rendererKey = null;
        particle.clear();
    }

}
