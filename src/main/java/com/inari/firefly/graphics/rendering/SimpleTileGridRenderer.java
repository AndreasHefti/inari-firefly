package com.inari.firefly.graphics.rendering;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.graphics.tile.TileGrid;
import com.inari.firefly.graphics.tile.TileGrid.TileGridIterator;
import com.inari.firefly.graphics.tile.TileGridSystem;
import com.inari.firefly.system.component.SystemComponentType;
import com.inari.firefly.system.external.FFTimer;

public final class SimpleTileGridRenderer extends Renderer {
    
    public static final SystemComponentType COMPONENT_TYPE = new SystemComponentType( Renderer.TYPE_KEY, SimpleTileGridRenderer.class );
    public static final RenderingChain.RendererKey CHAIN_KEY = new RenderingChain.RendererKey( "SimpleTileGridRenderer", SimpleTileGridRenderer.class );
    public static final Aspects MATCHING_ASPECTS = EntityComponent.ASPECT_GROUP.createAspects( 
        ETransform.TYPE_KEY, 
        ETile.TYPE_KEY 
    );
    
    private TileGridSystem tileGridSystem;

    protected SimpleTileGridRenderer( int index ) {
        super( index );
        setName( CHAIN_KEY.name );
    }

    @Override
    protected final void init() throws FFInitException {
        super.init();
        tileGridSystem = context.getSystem( TileGridSystem.SYSTEM_KEY );
    }

    @Override
    public final boolean match( Aspects aspects ) {
        return aspects.include( MATCHING_ASPECTS );
    }

    @Override
    public final void render( int viewId, int layerId, final Rectangle clip, final FFTimer timer ) {
        final TileGrid tileGrid = tileGridSystem.getTileGrid( viewId, layerId );
        if ( tileGrid == null ) {
            return;
        }
        
        final TileGridIterator tileGridIterator = tileGrid.getTileGridIterator( clip );
        while( tileGridIterator.hasNext() ) {
            ETile tile = entitySystem.getComponent( tileGridIterator.next(), ETile.TYPE_KEY );
            graphics.renderSprite( tile, tileGridIterator.getWorldXPos(), tileGridIterator.getWorldYPos() );
        }
    }

}
