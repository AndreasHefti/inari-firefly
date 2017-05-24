package com.inari.firefly.graphics.rendering;

import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.component.SystemComponent;
import com.inari.firefly.system.external.TransformData;

public abstract class Renderer extends SystemComponent {

    public static final SystemComponentKey<Renderer> TYPE_KEY = SystemComponentKey.create( Renderer.class );
    
    protected final TransformDataCollector exactTransformCollector = new ExactTransformDataCollector();
    protected final TransformDataCollector diskreteTransformCollector = new DiskreteTransformDataCollector();
    
    protected Renderer( int index ) {
        super( index );
    }
    
    @Override
    public final IIndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }
    
    public abstract boolean match( final Aspects aspects );
    
    public abstract boolean accept( int entityId, final Aspects aspects );
    
    public abstract void dispose( int entityId, final Aspects aspects );
    
    public abstract void render( RenderEvent event );
    
    
    protected interface TransformDataCollector extends TransformData {
        void set( TransformData transform );
        void set( TransformData transform, float xoffset, float yoffset );
        void add( TransformData transform );
    }
    
    private final class ExactTransformDataCollector implements TransformDataCollector {

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
            scalex += transform.getScaleX();
            scaley += transform.getScaleY();
            rotation += transform.getRotation();
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
    
    private final class DiskreteTransformDataCollector implements TransformDataCollector {
        
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
            scalex += transform.getScaleX();
            scaley += transform.getScaleY();
            rotation += transform.getRotation();
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
