package com.inari.firefly.graphics.rendering;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.system.component.SystemComponent;
import com.inari.firefly.system.external.FFGraphics;
import com.inari.firefly.system.external.FFTimer;
import com.inari.firefly.system.external.TransformData;

public abstract class Renderer extends SystemComponent {

    public static final SystemComponentKey<Renderer> TYPE_KEY = SystemComponentKey.create( Renderer.class );
    
    protected final TransformDataCollector transformCollector = new ExactTransformDataCollector();
    
    protected EntitySystem entitySystem;
    protected FFGraphics graphics;
    protected final DynArray<DynArray<DynArray<IndexedTypeSet>>> spritesPerViewAndLayer;
    
    protected Renderer( int index ) {
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
    public final IIndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }
    
    public final boolean accept( int entityId, Aspects aspects ) {
        final IndexedTypeSet components = entitySystem.getComponents( entityId );
        if ( accept( entityId, aspects, components ) ) {
            final ETransform transform = components.get( ETransform.TYPE_KEY );
            final DynArray<IndexedTypeSet> renderablesOfView = getEntites( transform.getViewId(), transform.getLayerId(), true );
            renderablesOfView.add( components );
            accepted( entityId, aspects, renderablesOfView );
            return true;
        } 
        
        return false;
    }
    
    public final void dispose( int entityId, Aspects aspects ) {
        final IndexedTypeSet components = entitySystem.getComponents( entityId );
        if ( accept( entityId, aspects, components ) ) {
            final ETransform transform = components.get( ETransform.TYPE_KEY );
            final DynArray<IndexedTypeSet> renderablesOfView = getEntites( transform.getViewId(), transform.getLayerId(), false );
            if ( renderablesOfView != null ) {
                renderablesOfView.remove( components );
            }
        }
    }
    
    protected void accepted( int entityId, final Aspects aspects, final DynArray<IndexedTypeSet> renderablesOfView ) {
        // NOOP
    }
    
    protected boolean accept( int entityId, Aspects aspects, IndexedTypeSet components ) {
        return true;
    }
    
    protected final DynArray<IndexedTypeSet> getEntites( int viewId, int layerId, boolean createNew ) {
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
    
    public abstract boolean match( final Aspects aspects );
    
    
    public abstract void render( int viewId, int layerId, final Rectangle clip, final FFTimer timer );
    
    
    protected interface TransformDataCollector extends TransformData {
        void set( TransformData transform );
        void set( TransformData transform, float xoffset, float yoffset );
        void add( TransformData transform );
        void addOffset( float x, float y );
    }
    
    protected final class ExactTransformDataCollector implements TransformDataCollector {

        public float xpos, ypos;
        public float pivotx, pivoty;
        public float scalex, scaley;
        public float rotation;
        
        ExactTransformDataCollector() {}
        
        @Override
        public final void set( final TransformData transform, float xoffset, float yoffset ) {
            set( transform );
            xpos += xoffset;
            ypos += yoffset;
        }
        
        @Override
        public final void set( final TransformData transform ) {
            xpos = transform.getXOffset();
            ypos = transform.getYOffset();
            pivotx = transform.getPivotX();
            pivoty = transform.getPivotY();
            scalex = transform.getScaleX();
            scaley = transform.getScaleY();
            rotation = transform.getRotation();
        }
        
        @Override
        public final void add( final TransformData transform ) {
            xpos += transform.getXOffset();
            ypos += transform.getYOffset();
            pivotx += transform.getPivotX();
            pivoty += transform.getPivotY();
            scalex *= transform.getScaleX();
            scaley *= transform.getScaleY();
            rotation += transform.getRotation();
        }
        
        @Override
        public final void addOffset( float x, float y ) {
            xpos += x;
            ypos += y;
        }

        @Override
        public final float getXOffset() {
            return xpos;
        }

        @Override
        public final float getYOffset() {
            return ypos;
        }

        @Override
        public final float getScaleX() {
            return scalex;
        }

        @Override
        public final float getScaleY() {
            return scaley;
        }

        @Override
        public final float getPivotX() {
            return pivotx;
        }

        @Override
        public final float getPivotY() {
            return pivoty;
        }

        @Override
        public final float getRotation() {
            return rotation;
        }

        @Override
        public final boolean hasRotation() {
            return rotation != 0f;
        }

        @Override
        public final boolean hasScale() {
            return scalex != 1 || scaley != 1;
        }

    }
    
    protected final class DiskreteTransformDataCollector implements TransformDataCollector {
        
        public float xpos, ypos;
        public float pivotx, pivoty;
        public float scalex, scaley;
        public float rotation;
        
        DiskreteTransformDataCollector() {}
        
        @Override
        public final void set( TransformData transform, float xoffset, float yoffset ) {
            xpos = (float) Math.floor( transform.getXOffset() + xoffset );
            ypos = (float) Math.floor( transform.getYOffset() + yoffset );
            pivotx = (float) Math.floor( transform.getPivotX() );
            pivoty = (float) Math.floor( transform.getPivotY() );
            scalex = transform.getScaleX();
            scaley = transform.getScaleY();
            rotation = transform.getRotation();
        }
        
        @Override
        public final void set( final TransformData transform ) {
            xpos = (float) Math.floor( transform.getXOffset() );
            ypos = (float) Math.floor( transform.getYOffset() );
            pivotx = (float) Math.floor( transform.getPivotX() );
            pivoty = (float) Math.floor( transform.getPivotY() );
            scalex = transform.getScaleX();
            scaley = transform.getScaleY();
            rotation = transform.getRotation();
        }
        
        @Override
        public final void add( final TransformData transform ) {
            xpos += (float) Math.floor( transform.getXOffset() );
            ypos += (float) Math.floor( transform.getYOffset() );
            pivotx += (float) Math.floor( transform.getPivotX() );
            pivoty += (float) Math.floor( transform.getPivotY() );
            scalex *= transform.getScaleX();
            scaley *= transform.getScaleY();
            rotation += transform.getRotation();
        }
        
        @Override
        public final void addOffset( float x, float y ) {
            xpos += (float) Math.floor( x );
            ypos += (float) Math.floor( y );
        }

        @Override
        public final float getXOffset() {
            return xpos;
        }

        @Override
        public final float getYOffset() {
            return ypos;
        }

        @Override
        public final float getScaleX() {
            return scalex;
        }

        @Override
        public final float getScaleY() {
            return scaley;
        }

        @Override
        public final float getPivotX() {
            return pivotx;
        }

        @Override
        public final float getPivotY() {
            return pivoty;
        }

        @Override
        public final float getRotation() {
            return rotation;
        }

        @Override
        public final boolean hasRotation() {
            return rotation != 0f;
        }

        @Override
        public final boolean hasScale() {
            return scalex != 1 || scaley != 1;
        }

    }

}
