package com.inari.firefly.renderer.sprite;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.ILowerSystemFacade;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.RenderEventListener;

public final class SpriteViewRenderer implements FFContextInitiable, RenderEventListener {
    
    private SpriteViewSystem spriteViewSystem;
    private ILowerSystemFacade lowerSystemFacade;
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        spriteViewSystem = context.getComponent( FFContext.Systems.SPRITE_VIEW_SYSTEM );
        lowerSystemFacade = context.getComponent( FFContext.LOWER_SYSTEM_FACADE );
        
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
            
            if ( transform.hasScale() || transform.hasRotation() ) {
                lowerSystemFacade.renderSprite( 
                    sprite, 
                    transform.getXpos(), 
                    transform.getYpos(), 
                    transform.getPivotx(),
                    transform.getPivoty(),
                    transform.getScalex(),
                    transform.getScaley(),
                    transform.getRotation()
                );
            } else {
                lowerSystemFacade.renderSprite( sprite, transform.getXpos(), transform.getYpos() );
            }
        }
    }

}
