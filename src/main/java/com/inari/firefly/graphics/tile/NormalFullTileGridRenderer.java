package com.inari.firefly.graphics.tile;

import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.tile.TileGrid.TileGridIterator;
import com.inari.firefly.system.RenderEvent;

@Deprecated // will soon be replaced by RenderingSystem
public final class NormalFullTileGridRenderer extends TileGridRenderer {
    
    public static final String NAME = "NormalFullTileGridRenderer";
    
    protected final ExactTransformDataCollector transformCollector = new ExactTransformDataCollector();
    
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
        
        TileGridIterator tileGridIterator = tileGrid.getTileGridIterator( event.getClip() );
        while( tileGridIterator.hasNext() ) {
            int entityId = tileGridIterator.next();
            ETile tile = entitySystem.getComponent( entityId, ETile.TYPE_KEY );
            ETransform transform = entitySystem.getComponent( entityId, ETransform.TYPE_KEY );
            
            transformCollector.set( transform );
            transformCollector.xpos += tileGridIterator.getWorldXPos();
            transformCollector.ypos += tileGridIterator.getWorldYPos();
            
            render( tile, transformCollector );
        }
    }

}
