package com.inari.firefly.graphics.rendering;

import java.util.Set;

import com.inari.commons.lang.aspect.Aspects;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.EntityActivationEvent;
import com.inari.firefly.entity.EntityActivationListener;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.RenderEventListener;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public final class RenderingSystem 
    extends ComponentSystem<RenderingSystem> 
    implements RenderEventListener, EntityActivationListener {
    
    public static final RenderingChain DEFAULT_RENDERING_CHAIN = new RenderingChain()
        .addElement( MultiPositionSpriteRenderer.CHAIN_KEY )
        .addElement( SimpleSpriteRenderer.CHAIN_KEY )
        .addElement( DefaultShapeRenderer.CHAIN_KEY )
        .build();
    
    public static final FFSystemTypeKey<RenderingSystem> SYSTEM_KEY = FFSystemTypeKey.create( RenderingSystem.class );
    
    private final RendererBuilder rendererBuilder = new RendererBuilder();
    
    private boolean allowMultipleAcceptance = false;
    private RenderingChain renderingChain;
    

    RenderingSystem() {
        super( SYSTEM_KEY );
    }

    @Override
    public void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        context.registerListener( RenderEvent.TYPE_KEY, this );
        context.registerListener( EntityActivationEvent.TYPE_KEY, this );
        
        setRenderingChain( DEFAULT_RENDERING_CHAIN, true );
    }

    public final boolean isAllowMultipleAcceptance() {
        return allowMultipleAcceptance;
    }

    public final void setAllowMultipleAcceptance( boolean allowMultipleAcceptance ) {
        this.allowMultipleAcceptance = allowMultipleAcceptance;
    }

    @Override
    public final boolean match( final Aspects aspects ) {
        return true;
    }

    @Override
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

    @Override
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

    @Override
    public final void render( final RenderEvent event ) {
        for ( int i = 0; i < renderingChain.elements.capacity(); i++ ) {
            RenderingChain.Element element = renderingChain.elements.get( i );
            if ( element == null || element.renderer == null ) {
                continue;
            }
            
            element.renderer.render( event );
        }
    }
    
    public final void setRenderingChain( RenderingChain renderingChain, boolean autoBuild ) {
        renderingChain.build();
        this.renderingChain = renderingChain;
        if ( autoBuild ) {
            for ( int i = 0; i < renderingChain.elements.capacity(); i++ ) {
                RenderingChain.Element element = renderingChain.elements.get( i );
                if ( element == null ) {
                    continue;
                }
                
                rendererBuilder.create( element );
            }
        }
    }

    public final RenderingChain getRenderingChain() {
        return renderingChain;
    }

    @Override
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
    }

    @Override
    public final void clear() {
        dispose( context );
        renderingChain = null;
    }
    
    @Override
    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return null;
    }

    @Override
    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return null;
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
