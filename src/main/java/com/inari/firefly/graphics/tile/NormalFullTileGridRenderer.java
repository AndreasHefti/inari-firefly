package com.inari.firefly.graphics.tile;

import com.inari.firefly.entity.ETransform;
import com.inari.firefly.graphics.tile.TileGrid.TileIterator;
import com.inari.firefly.system.RenderEvent;

public final class NormalFullTileGridRenderer extends TileGridRenderer {
    
    public static final String NAME = "NormalFullTileGridRenderer";
    
    NormalFullTileGridRenderer( int id ) {
        super( id );
    }

    @Override
    public final void render( RenderEvent event ) {
        int viewId = event.getViewId();
        int layerId = event.getLayerId();
        
        TileGrid tileGrid = tileGridSystem.getTileGrid( viewId, layerId );
        if ( tileGrid == null || ( tileGrid.getRendererId() >= 0 && tileGrid.getRendererId() != index() ) ) {
            return;
        }
        
        TileIterator iterator = tileGrid.iterator( event.getClip() );
        if ( iterator == null ) {
            return;
        }

        while( iterator.hasNext() ) {
            int entityId = iterator.next();
            ETile tile = entitySystem.getComponent( entityId, ETile.TYPE_KEY );
            ETransform transform = entitySystem.getComponent( entityId, ETransform.TYPE_KEY );
            
            transformCollector.set( transform );
            transformCollector.xpos += iterator.getWorldXPos();
            transformCollector.ypos += iterator.getWorldYPos();
            
            render( tile, transform.getParentId() );
        }
    }

}
