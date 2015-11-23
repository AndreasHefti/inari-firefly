package com.inari.firefly.renderer.sprite;

import java.util.Comparator;

import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.aspect.AspectBitSet;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.entity.event.EntityActivationListener;
import com.inari.firefly.renderer.BaseRenderer;
import com.inari.firefly.renderer.SpriteRenderable;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.RenderEventListener;

public final class SpriteViewRenderer extends BaseRenderer implements RenderEventListener, EntityActivationListener {
    
    public static final TypedKey<SpriteViewRenderer> CONTEXT_KEY = TypedKey.create( "FF_SPRITE_VIEW_RENDERER", SpriteViewRenderer.class );
    
    // TODO another approach would be, storing only the id's within DynArray<DynArray<IntBag>>
    private final DynArray<DynArray<DynArray<IndexedTypeSet>>> spritesPerViewAndLayer;
    
    SpriteViewRenderer() {
        spritesPerViewAndLayer = new DynArray<DynArray<DynArray<IndexedTypeSet>>>();
    }

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );

        context.registerListener( RenderEvent.class, this );
        context.registerListener( EntityActivationEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        
        context.disposeListener( RenderEvent.class, this );
        context.disposeListener( EntityActivationEvent.class, this );
    }
    
    @Override
    public final boolean match( AspectBitSet aspect ) {
        return aspect.contains( ESprite.TYPE_KEY ) && !aspect.contains( ETile.TYPE_KEY );
    }
    
    @Override
    public final void onEntityActivationEvent( EntityActivationEvent event ) {
        IndexedTypeSet components = entitySystem.getComponents( event.entityId );
        ETransform transform = components.get( ETransform.TYPE_KEY );
        int viewId = transform.getViewId();
        int layerId = transform.getLayerId();
        switch ( event.eventType ) {
            case ENTITY_ACTIVATED: {
                DynArray<IndexedTypeSet> renderablesOfView = getSprites( viewId, layerId, true );
                renderablesOfView.add( components );
                renderablesOfView.sort( RENDERABLE_COMPARATOR );
                break;
            }
            case ENTITY_DEACTIVATED: {
                DynArray<IndexedTypeSet> renderablesOfView = getSprites( viewId, layerId, false );
                renderablesOfView.remove( components );
            }
        }
    }
    
    @Override
    public final void render( RenderEvent event ) {
        DynArray<IndexedTypeSet> spritesToRender = getSprites( event.getViewId(), event.getLayerId(), false );
        if ( spritesToRender == null ) {
            return;
        }
        
        for ( int i = 0; i < spritesToRender.capacity(); i++ ) {
            IndexedTypeSet components = spritesToRender.get( i );
            if ( components == null ) {
                continue;
            }
            
            ESprite sprite = components.get( ESprite.TYPE_KEY );
            ETransform transform = components.get( ETransform.TYPE_KEY );
            transformCollector.set( transform );
            
            render( sprite, transform.getParentId() );
        }
    }
    
    private final DynArray<IndexedTypeSet> getSprites( int viewId, int layerId, boolean createNew ) {
        DynArray<DynArray<IndexedTypeSet>> spritePerLayer = null;
        if ( spritesPerViewAndLayer.contains( viewId ) ) { 
            spritePerLayer = spritesPerViewAndLayer.get( viewId );
        } else if ( createNew ) {
            spritePerLayer = new DynArray<DynArray<IndexedTypeSet>>();
            spritesPerViewAndLayer.set( viewId, spritePerLayer );
        }
        
        if ( spritePerLayer == null ) {
            return null;
        }
        
        DynArray<IndexedTypeSet> spritesOfLayer = null;
        if ( spritePerLayer.contains( layerId ) ) { 
            spritesOfLayer = spritePerLayer.get( layerId );
        } else if ( createNew ) {
            spritesOfLayer = new DynArray<IndexedTypeSet>();
            spritePerLayer.set( layerId, spritesOfLayer );
        }
        
        return spritesOfLayer;
    }
    
    private final Comparator<IndexedTypeSet> RENDERABLE_COMPARATOR = new Comparator<IndexedTypeSet>() {
        
        @Override
        public final int compare( IndexedTypeSet its1, IndexedTypeSet its2 ) {
            if ( its1 == null && its2 == null ) {
                return 0;
            }
            if ( its1 == null ) {
                return 1;
            }
            if ( its2 == null ) {
                return -1;
            }
            SpriteRenderable sr1 = its1.get( ESprite.TYPE_KEY );
            SpriteRenderable sr2 = its2.get( ESprite.TYPE_KEY );
            int o1 = sr1.getOrdering();
            int o2 = sr2.getOrdering();
            if ( o1 == o2 ) {
                return 0;
            }
            
            return ( o1 < o2 )? 1 : -1;
        }
    };

}
