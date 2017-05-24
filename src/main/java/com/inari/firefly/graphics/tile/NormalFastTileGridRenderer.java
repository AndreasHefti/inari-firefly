package com.inari.firefly.graphics.tile;

import com.inari.firefly.graphics.tile.TileGrid.TileGridIterator;
import com.inari.firefly.system.RenderEvent;

@Deprecated // will soon be replaced by RenderingSystem
public class NormalFastTileGridRenderer extends TileGridRenderer {
    
    public static final String NAME = "NormalFastTileGridRenderer";
    
    NormalFastTileGridRenderer( int id ) {
        super( id );
    }

    @Override
    public final void render( RenderEvent event ) {
        int viewId = event.getViewId();
        int layerId = event.getLayerId();
        
        TileGrid tileGrid = tileGridSystem.getTileGrid( viewId, layerId );
        if ( tileGrid == null || tileGrid.getRendererId() != index() ) {
          return;
        }
        
        TileGridIterator tileGridIterator = tileGrid.getTileGridIterator( event.getClip() );
        while( tileGridIterator.hasNext() ) {
            ETile tile = entitySystem.getComponent( tileGridIterator.next(), ETile.TYPE_KEY );
            graphics.renderSprite( tile, tileGridIterator.getWorldXPos(), tileGridIterator.getWorldYPos() );
        }
    }

}
