package com.inari.firefly.graphics.rendering;

import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.shape.EShape;
import com.inari.firefly.system.RenderEvent;

public final class DefaultShapeRenderer extends Renderer {
    
    public static final RenderingChain.Key CHAIN_KEY = new RenderingChain.Key( "DefaultShapeRenderer", DefaultShapeRenderer.class );

    protected DefaultShapeRenderer( int index ) {
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

            graphics.renderShape( 
                components.<EShape>get( EShape.TYPE_KEY ), 
                components.<ETransform>get( ETransform.TYPE_KEY ) 
            );
        }
    }
    
    

}
