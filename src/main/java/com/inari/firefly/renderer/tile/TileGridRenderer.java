package com.inari.firefly.renderer.tile;

import com.inari.commons.lang.TypedKey;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.renderer.BaseRenderer;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.tile.TileGrid.TileIterator;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.RenderEventListener;

public final class TileGridRenderer extends BaseRenderer implements FFSystem, RenderEventListener {
    
    public static final TypedKey<TileGridRenderer> CONTEXT_KEY = TypedKey.create( "FF_TILE_GRID_RENDERER", TileGridRenderer.class );
    
    private TileGridSystem tileGridSystem;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        tileGridSystem = context.getSystem( TileGridSystem.CONTEXT_KEY );
        
        context.registerListener( RenderEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( RenderEvent.class, this );
    }

    @Override
    public final void render( RenderEvent event ) {
        int viewId = event.getViewId();
        int layerId = event.getLayerId();
        TileIterator iterator = tileGridSystem.iterator( viewId, layerId, event.getClip() );
        if ( iterator == null ) {
            return;
        }
        
        switch ( tileGridSystem.getRenderMode( viewId, layerId ) ) {
            case FAST_RENDERING: {
                renderTileGrid( iterator );
                break;
            }
            case FULL_RENDERING: {
                renderTileGridAllData( iterator );
                break;
            }
            default: {}
        }
    }
    
    protected void renderTileGridAllData( TileIterator iterator ) {
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

    protected void renderTileGrid( TileIterator iterator ) {
        while( iterator.hasNext() ) {
            ESprite sprite = entitySystem.getComponent( iterator.next(), ESprite.TYPE_KEY );
            systemInterface.renderSprite( sprite, iterator.getWorldXPos(), iterator.getWorldYPos() );
        }
    }

}
