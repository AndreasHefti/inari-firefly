package com.inari.firefly.graphics.rendering;

import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.shape.EShape;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.external.FFGraphics;

public final class DefaultShapeRenderer extends Renderer {
    
    private final DynArray<DynArray<IntBag>> shapesPerViewAndLayer;
    private EntitySystem entitySystem;
    private FFGraphics graphics;

    protected DefaultShapeRenderer( int index ) {
        super( index );
        super.setName( "DefaultShapeRenderer" );
        shapesPerViewAndLayer = DynArray.createTyped( DynArray.class, 20, 10 );
    }

    @Override
    protected final void init() throws FFInitException {
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        graphics = context.getGraphics();
    }

    @Override
    public final boolean match( Aspects aspects ) {
        return aspects.contains( EShape.TYPE_KEY );
    }

    @Override
    public final boolean accept( int entityId, Aspects aspects ) {
        final ETransform transform = entitySystem.getComponent( entityId, ETransform.TYPE_KEY );
        final IntBag renderablesOfView = getShapeIds( transform.getViewId(), transform.getLayerId(), true );
        renderablesOfView.add( entityId );
        return true;
    }

    @Override
    public final void dispose( int entityId, Aspects aspects ) {
        final ETransform transform = entitySystem.getComponent( entityId, ETransform.TYPE_KEY );
        final IntBag renderablesOfView = getShapeIds( transform.getViewId(), transform.getLayerId(), false );
        renderablesOfView.remove( entityId );
    }

    @Override
    public final void render( RenderEvent event ) {
        IntBag shapeIds = getShapeIds( event.getViewId(), event.getLayerId(), false );
        if ( shapeIds == null || shapeIds.isEmpty() ) {
            return;
        }
        
        final int nullValue = shapeIds.getNullValue();
        for ( int i = 0; i < shapeIds.length(); i++ ) {
            int entityId = shapeIds.get( i );
            if ( nullValue == entityId ) {
                continue;
            }
            
            EShape shape = entitySystem.getComponent( entityId, EShape.TYPE_KEY );
            ETransform transform = entitySystem.getComponent( entityId, ETransform.TYPE_KEY );
            graphics.renderShape( shape, transform );
        }
    }
    
    private final IntBag getShapeIds( int viewId, int layerId, boolean createNew ) {
        DynArray<IntBag> shapePerLayer = null;
        if ( shapesPerViewAndLayer.contains( viewId ) ) { 
            shapePerLayer = shapesPerViewAndLayer.get( viewId );
        } else if ( createNew ) {
            shapePerLayer = DynArray.create( IntBag.class, 20, 10 );
            shapesPerViewAndLayer.set( viewId, shapePerLayer );
        }
        
        if ( shapePerLayer == null ) {
            return null;
        }
        
        IntBag shapesOfLayer = null;
        if ( shapePerLayer.contains( layerId ) ) { 
            shapesOfLayer = shapePerLayer.get( layerId );
        } else if ( createNew ) {
            shapesOfLayer = new IntBag();
            shapePerLayer.set( layerId, shapesOfLayer );
        }
        
        return shapesOfLayer;
    }

}
