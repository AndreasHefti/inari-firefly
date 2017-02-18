package com.inari.firefly.graphics.shape;

import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityActivationEvent;
import com.inari.firefly.entity.EntityActivationListener;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.BaseRenderer;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public final class ShapeRenderSystem
    implements 
        FFSystem, 
        EntityActivationListener {
    
    private static final SystemComponentKey<ShapeRenderer> SHAPE_RENDERER_TYPE_KEY = SystemComponentKey.create( ShapeRenderer.class );
    public static final FFSystemTypeKey<ShapeRenderSystem> SYSTEM_KEY = FFSystemTypeKey.create( ShapeRenderSystem.class );
    
    private EntitySystem entitySystem;
    private final DynArray<DynArray<IntBag>> shapesPerViewAndLayer;
    private ShapeRenderer shapeRenderer;
    
    
    ShapeRenderSystem() {
        shapesPerViewAndLayer = new DynArray<DynArray<IntBag>>();
    }
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return SYSTEM_KEY;
    }

    @Override
    public final FFSystemTypeKey<ShapeRenderSystem> systemTypeKey() {
        return SYSTEM_KEY;
    }

    @Override
    public final void init( FFContext context ) throws FFInitException {
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        shapeRenderer = new ShapeRenderer( context );
        
        context.registerListener( RenderEvent.TYPE_KEY, shapeRenderer );
        context.registerListener( EntityActivationEvent.TYPE_KEY, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( RenderEvent.TYPE_KEY, shapeRenderer );
        context.disposeListener( EntityActivationEvent.TYPE_KEY, this );
        
        shapeRenderer.dispose();
    }
    
    @Override
    public final boolean match( Aspects aspects ) {
        return aspects.contains( EShape.TYPE_KEY );
    }
    
    public final void entityActivated( int entityId, final Aspects aspects ) {
        final ETransform transform = entitySystem.getComponent( entityId, ETransform.TYPE_KEY );
        final IntBag renderablesOfView = getShapeIds( transform.getViewId(), transform.getLayerId(), true );
        renderablesOfView.add( entityId );
    }
    
    public final void entityDeactivated( int entityId, final Aspects aspects ) {
        final ETransform transform = entitySystem.getComponent( entityId, ETransform.TYPE_KEY );
        final IntBag renderablesOfView = getShapeIds( transform.getViewId(), transform.getLayerId(), false );
        renderablesOfView.remove( entityId );
    }
    
    private final IntBag getShapeIds( int viewId, int layerId, boolean createNew ) {
        DynArray<IntBag> shapePerLayer = null;
        if ( shapesPerViewAndLayer.contains( viewId ) ) { 
            shapePerLayer = shapesPerViewAndLayer.get( viewId );
        } else if ( createNew ) {
            shapePerLayer = new DynArray<IntBag>();
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
    
    final class ShapeRenderer extends BaseRenderer {
        
        protected final TransformDataCollector transformCollector = new ExactTransformDataCollector();

        protected ShapeRenderer( FFContext context ) {
            super( 0 );
            
            injectContext( context );
            init();
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
                transformCollector.set( transform );
                render( shape, transform.getParentId(), transformCollector );
            }
        }

        @Override
        public final IIndexedTypeKey indexedTypeKey() {
            return SHAPE_RENDERER_TYPE_KEY;
        }
    }

}
