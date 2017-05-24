package com.inari.firefly.graphics.rendering;

import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.system.external.FFGraphics;

public abstract class BaseSpriteRenderer extends Renderer {
    
    protected EntitySystem entitySystem;
    protected FFGraphics graphics;
    protected final DynArray<DynArray<DynArray<IndexedTypeSet>>> spritesPerViewAndLayer;
    
    public BaseSpriteRenderer( int index ) {
        super( index );
        spritesPerViewAndLayer = DynArray.createTyped( DynArray.class, 20, 10 );
    }
    
    @Override
    protected void init() throws FFInitException {
        super.init();
        
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        graphics = context.getGraphics();
    }
    
    @Override
    public final boolean accept( int entityId, Aspects aspects ) {
        if ( internalAccespt( entityId, aspects ) ) {
            final IndexedTypeSet components = entitySystem.getComponents( entityId );
            final ETransform transform = components.get( ETransform.TYPE_KEY );
            final DynArray<IndexedTypeSet> renderablesOfView = getSprites( transform.getViewId(), transform.getLayerId(), true );
            renderablesOfView.add( components );
            return true;
        } 
        
        return false;
    }
    
    protected abstract boolean internalAccespt( int entityId, Aspects aspects );

    @Override
    public final void dispose( int entityId, Aspects aspects ) {
        if ( internalDispose( entityId, aspects ) ) {
            final IndexedTypeSet components = entitySystem.getComponents( entityId );
            final ETransform transform = components.get( ETransform.TYPE_KEY );
            final DynArray<IndexedTypeSet> renderablesOfView = getSprites( transform.getViewId(), transform.getLayerId(), false );
            renderablesOfView.remove( components );
        }
    }
    
    protected abstract boolean internalDispose( int entityId, Aspects aspects );
    
    protected final DynArray<IndexedTypeSet> getSprites( int viewId, int layerId, boolean createNew ) {
        DynArray<DynArray<IndexedTypeSet>> spritePerLayer = null;
        if ( spritesPerViewAndLayer.contains( viewId ) ) { 
            spritePerLayer = spritesPerViewAndLayer.get( viewId );
        } else if ( createNew ) {
            spritePerLayer = DynArray.createTyped( DynArray.class, 20, 10 );
            spritesPerViewAndLayer.set( viewId, spritePerLayer );
        }
        
        if ( spritePerLayer == null ) {
            return null;
        }
        
        DynArray<IndexedTypeSet> spritesOfLayer = null;
        if ( spritePerLayer.contains( layerId ) ) { 
            spritesOfLayer = spritePerLayer.get( layerId );
        } else if ( createNew ) {
            spritesOfLayer = DynArray.create( IndexedTypeSet.class, 100, 100 );
            spritePerLayer.set( layerId, spritesOfLayer );
        }
        
        return spritesOfLayer;
    }

}
