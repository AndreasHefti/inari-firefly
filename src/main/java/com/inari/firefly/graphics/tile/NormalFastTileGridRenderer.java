package com.inari.firefly.graphics.tile;

import com.inari.firefly.graphics.sprite.ESprite;
import com.inari.firefly.graphics.tile.TileGrid.TileIterator;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.RenderEvent;

public class NormalFastTileGridRenderer extends TileGridRenderer {
    
    public static final String NAME = "NormalFastTileGridRenderer";
    
    NormalFastTileGridRenderer( int id, FFContext context ) {
        super( id, context );
    }

    @Override
    public final void render( RenderEvent event ) {
        int viewId = event.getViewId();
        int layerId = event.getLayerId();
        
        TileGrid tileGrid = tileGridSystem.getTileGrid( viewId, layerId );
        if ( tileGrid == null || tileGrid.getRendererId() != getId() ) {
          return;
        }
        TileIterator iterator = tileGrid.iterator( event.getClip() );
        if ( iterator == null ) {
            return;
        }
        
        while( iterator.hasNext() ) {
            ESprite sprite = entitySystem.getComponent( iterator.next(), ESprite.TYPE_KEY );
            graphics.renderSprite( sprite, iterator.getWorldXPos(), iterator.getWorldYPos() );
        }
    }

}
