package com.inari.firefly.graphics.tile;

import com.inari.firefly.graphics.tile.TileGrid.TileIterator;
import com.inari.firefly.system.RenderEvent;

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
        TileIterator iterator = tileGrid.iterator( event.getClip() );
        if ( iterator == null ) {
            return;
        }
        
        while( iterator.hasNext() ) {
            ETile tile = entitySystem.getComponent( iterator.next(), ETile.TYPE_KEY );
            graphics.renderSprite( tile, iterator.getWorldXPos(), iterator.getWorldYPos() );
        }
    }

}
