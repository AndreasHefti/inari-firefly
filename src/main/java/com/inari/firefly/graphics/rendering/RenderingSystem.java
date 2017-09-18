package com.inari.firefly.graphics.rendering;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.EntityActivationEvent;
import com.inari.firefly.entity.EntityActivationListener;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.RenderEventListener;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;
import com.inari.firefly.system.external.FFTimer;

public final class RenderingSystem implements FFSystem, RenderEventListener, EntityActivationListener {
    
    public static final RenderingChain DEFAULT_RENDERING_CHAIN = new RenderingChain()
        .addElement( SimpleTileGridRenderer.CHAIN_KEY )
        .addElement( MultiPositionSpriteRenderer.CHAIN_KEY )
        .addElement( SimpleSpriteRenderer.CHAIN_KEY )
        .addElement( SpriteParticleRenderer.CHAIN_KEY )
        .addElement( SimpleShapeRenderer.CHAIN_KEY )
        .addElement( SimpleTextRenderer.CHAIN_KEY )
        .build();
    
    public static final FFSystemTypeKey<RenderingSystem> SYSTEM_KEY = FFSystemTypeKey.create( RenderingSystem.class );
    
    private final RendererBuilder rendererBuilder = new RendererBuilder();
    
    private FFContext context;
    private boolean allowMultipleAcceptance = false;
    private RenderingChain renderingChain;
    

    RenderingSystem() {}

    public void init( FFContext context ) throws FFInitException {
        this.context = context;
        
        context.registerListener( RenderEvent.TYPE_KEY, this );
        context.registerListener( EntityActivationEvent.TYPE_KEY, this );
        
        setRenderingChain( DEFAULT_RENDERING_CHAIN );
    }
    
    public final IIndexedTypeKey indexedTypeKey() {
        return SYSTEM_KEY;
    }

    public final FFSystemTypeKey<?> systemTypeKey() {
        return SYSTEM_KEY;
    }

    public final boolean isAllowMultipleAcceptance() {
        return allowMultipleAcceptance;
    }

    public final void setAllowMultipleAcceptance( boolean allowMultipleAcceptance ) {
        this.allowMultipleAcceptance = allowMultipleAcceptance;
    }

    public final boolean match( final Aspects aspects ) {
        return true;
    }

    public final void entityActivated( final int entityId, final Aspects aspects ) {
        for ( int i = 0; i < renderingChain.elements.capacity(); i++ ) {
            RenderingChain.Element element = renderingChain.elements.get( i );
            if ( element == null || element.renderer == null ) {
                continue;
            }
            
            if ( !element.renderer.match( aspects ) ) {
                continue;
            }
            
            if ( element.renderer.accept( entityId, aspects ) && !allowMultipleAcceptance ) {
                return;
            }
        }
    }

    public final void entityDeactivated( final int entityId, final Aspects aspects ) {
        for ( int i = 0; i < renderingChain.elements.capacity(); i++ ) {
            RenderingChain.Element element = renderingChain.elements.get( i );
            if ( element == null || element.renderer == null ) {
                continue;
            }
            
            if ( !element.renderer.match( aspects ) ) {
                continue;
            }
            
            element.renderer.dispose( entityId, aspects );
        }
    }

    public final void render( int viewId, int layerId, final Rectangle clip, final FFTimer timer ) {
        for ( int i = 0; i < renderingChain.elements.capacity(); i++ ) {
            RenderingChain.Element element = renderingChain.elements.get( i );
            if ( element == null || element.renderer == null ) {
                continue;
            }
            
            element.renderer.render( viewId, layerId, clip, timer );
        }
    }
    
    public final void setRenderingChain( RenderingChain renderingChain ) {
        renderingChain.build();
        this.renderingChain = renderingChain;

        for ( int i = 0; i < renderingChain.elements.capacity(); i++ ) {
            RenderingChain.Element element = renderingChain.elements.get( i );
            if ( element == null ) {
                continue;
            }
            
            rendererBuilder.create( element );
        }
    }

    public final RenderingChain getRenderingChain() {
        return renderingChain;
    }

    public final void dispose( FFContext context ) {
        context.disposeListener( RenderEvent.TYPE_KEY, this );
        context.disposeListener( EntityActivationEvent.TYPE_KEY, this );
        
        for ( int i = 0; i < renderingChain.elements.capacity(); i++ ) {
            RenderingChain.Element element = renderingChain.elements.get( i );
            if ( element == null || element.renderer == null ) {
                continue;
            }
            
            element.renderer = null;
        }
        
        renderingChain = null;
    }
    
    private final class RendererBuilder extends SystemComponentBuilder {
        
        private RendererBuilder() { 
            super( context, null ); 
        }
        
        @Override
        public final SystemComponentKey<Renderer> systemComponentKey() {
            return Renderer.TYPE_KEY;
        }

        @Override
        public int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            throw new UnsupportedOperationException();
        }
        
        public void create( RenderingChain.Element element ) {
            if ( element.renderer != null ) {
                throw new FFInitException( "Renderer : " + element.key + " already exists" );
            }
            
            if ( element.attributes != null ) {
                super.attributes.putAll( element.attributes );
            }
            
            element.renderer = createSystemComponent( -1, element.key.rendererType, context );
        }
    }

}
