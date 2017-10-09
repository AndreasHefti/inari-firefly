package com.inari.firefly.graphics.rendering;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArrayRO;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.shape.EShape;
import com.inari.firefly.system.component.SystemComponentType;
import com.inari.firefly.system.external.FFTimer;

public final class SimpleShapeRenderer extends Renderer {
    
    public static final SystemComponentType COMPONENT_TYPE = new SystemComponentType( Renderer.TYPE_KEY, SimpleShapeRenderer.class );
    public static final RenderingChain.RendererKey CHAIN_KEY = new RenderingChain.RendererKey( "SimpleShapeRenderer", SimpleShapeRenderer.class );

    protected SimpleShapeRenderer( int index ) {
        super( index );
        super.setName( CHAIN_KEY.name );
    }

    @Override
    protected final void init() throws FFInitException {
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        graphics = context.getGraphics();
    }

    @Override
    public final boolean match( Aspects aspects ) {
        return aspects.contains( EShape.TYPE_KEY );
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

            graphics.renderShape( 
                components.<EShape>get( EShape.TYPE_KEY ), 
                components.<ETransform>get( ETransform.TYPE_KEY ) 
            );
        }
    }
    
    

}
