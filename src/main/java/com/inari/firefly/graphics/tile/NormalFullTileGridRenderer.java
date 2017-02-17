package com.inari.firefly.graphics.tile;

import com.inari.firefly.entity.ETransform;
import com.inari.firefly.system.RenderEvent;

public final class NormalFullTileGridRenderer extends TileGridRenderer {
    
    public static final String NAME = "NormalFullTileGridRenderer";
    
    protected final ExactTransformDataCollector transformCollector = new ExactTransformDataCollector();
    private final TileGridIterator tileGridIterator;
    
    NormalFullTileGridRenderer( int id ) {
        super( id );
        tileGridIterator = new TileGridIterator();
    }

    @Override
    public final void render( RenderEvent event ) {
        int viewId = event.getViewId();
        int layerId = event.getLayerId();
        
        TileGrid tileGrid = tileGridSystem.getTileGrid( viewId, layerId );
        if ( tileGrid == null || ( tileGrid.getRendererId() >= 0 && tileGrid.getRendererId() != index() ) ) {
            return;
        }
        
        tileGridIterator.reset( event.getClip(), tileGrid );
        while( tileGridIterator.hasNext() ) {
            int entityId = tileGridIterator.next();
            ETile tile = entitySystem.getComponent( entityId, ETile.TYPE_KEY );
            ETransform transform = entitySystem.getComponent( entityId, ETransform.TYPE_KEY );
            
            transformCollector.set( transform );
            transformCollector.xpos += tileGridIterator.getWorldXPos();
            transformCollector.ypos += tileGridIterator.getWorldYPos();
            
            render( tile, transform.getParentId(), transformCollector );
        }
    }

}
