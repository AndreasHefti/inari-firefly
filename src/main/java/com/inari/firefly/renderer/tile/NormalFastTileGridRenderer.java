package com.inari.firefly.renderer.tile;

import com.inari.firefly.FFInitException;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.tile.TileGrid.TileIterator;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.RenderEvent;

public class NormalFastTileGridRenderer extends TileGridRenderer {
    
    public static final String NAME = "NormalFastTileGridRenderer";
    
    private static boolean singletonInstance = false;
    
    NormalFastTileGridRenderer( int id, FFContext context ) {
        super( id, context );
        if ( singletonInstance ) {
            throw new FFInitException( "There is already an instance of NormalFastTileGridRenderer and NormalFastTileGridRenderer is a singleton" );
        }
        singletonInstance = true;
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