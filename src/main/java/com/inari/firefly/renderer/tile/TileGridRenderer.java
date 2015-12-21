package com.inari.firefly.renderer.tile;

import com.inari.firefly.entity.ETransform;
import com.inari.firefly.renderer.BaseRenderer;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.tile.TileGrid.TileIterator;
import com.inari.firefly.system.FFContext;

public final class TileGridRenderer extends BaseRenderer {

    TileGridRenderer( FFContext context ) {
        super.init( context );
    }

    final void renderTileGridAllData( TileIterator iterator ) {
        while( iterator.hasNext() ) {
            int entityId = iterator.next();
            ESprite sprite = entitySystem.getComponent( entityId, ESprite.TYPE_KEY );
            ETransform transform = entitySystem.getComponent( entityId, ETransform.TYPE_KEY );
            
            transformCollector.set( transform );
            transformCollector.xpos += iterator.getWorldXPos();
            transformCollector.ypos += iterator.getWorldYPos();
            
            render( sprite, transform.getParentId() );
        }
    }

    final void renderTileGrid( TileIterator iterator ) {
        while( iterator.hasNext() ) {
            ESprite sprite = entitySystem.getComponent( iterator.next(), ESprite.TYPE_KEY );
            graphics.renderSprite( sprite, iterator.getWorldXPos(), iterator.getWorldYPos() );
        }
    }

}
