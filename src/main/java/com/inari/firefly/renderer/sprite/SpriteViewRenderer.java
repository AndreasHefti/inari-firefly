package com.inari.firefly.renderer.sprite;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.renderer.BaseRenderer;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.RenderEventListener;

public final class SpriteViewRenderer extends BaseRenderer implements RenderEventListener {
    
    public static final TypedKey<SpriteViewRenderer> CONTEXT_KEY = TypedKey.create( "FF_SPRITE_VIEW_RENDERER", SpriteViewRenderer.class );
    
    private SpriteViewSystem spriteViewSystem;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        spriteViewSystem = context.getComponent( SpriteViewSystem.CONTEXT_KEY );
        
        IEventDispatcher eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        eventDispatcher.register( RenderEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        IEventDispatcher eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        eventDispatcher.register( RenderEvent.class, this );
    }
    
    @Override
    public final void render( RenderEvent event ) {
        DynArray<IndexedTypeSet> spritesToRender = spriteViewSystem.getSprites( event.getViewId(), event.getLayerId() );
        if ( spritesToRender == null ) {
            return;
        }
        
        for ( int i = 0; i < spritesToRender.capacity(); i++ ) {
            IndexedTypeSet components = spritesToRender.get( i );
            if ( components == null ) {
                continue;
            }
            
            ESprite sprite = components.get( ESprite.COMPONENT_TYPE );
            ETransform transform = components.get( ETransform.COMPONENT_TYPE );
            transformCollector.set( transform );
            
            render( sprite, transform );
        }
    }

}
