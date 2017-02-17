package com.inari.firefly.graphics.tile;

import com.inari.firefly.system.RenderEvent;

public class NormalFastTileGridRenderer extends TileGridRenderer {
    
    public static final String NAME = "NormalFastTileGridRenderer";
    
    private final TileGridIterator tileGridIterator;
    
    NormalFastTileGridRenderer( int id ) {
        super( id );
        tileGridIterator = new TileGridIterator();
    }

    @Override
    public final void render( RenderEvent event ) {
        int viewId = event.getViewId();
        int layerId = event.getLayerId();
        
        TileGrid tileGrid = tileGridSystem.getTileGrid( viewId, layerId );
        if ( tileGrid == null || tileGrid.getRendererId() != index() ) {
          return;
        }
        
        tileGridIterator.reset( event.getClip(), tileGrid );
        while( tileGridIterator.hasNext() ) {
            ETile tile = entitySystem.getComponent( tileGridIterator.next(), ETile.TYPE_KEY );
            graphics.renderSprite( tile, tileGridIterator.getWorldXPos(), tileGridIterator.getWorldYPos() );
        }
    }

}
