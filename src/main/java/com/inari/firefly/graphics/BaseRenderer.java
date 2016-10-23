package com.inari.firefly.graphics;

import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.shape.EShape;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.RenderEventListener;
import com.inari.firefly.system.component.SystemComponent;
import com.inari.firefly.system.external.FFGraphics;
import com.inari.firefly.system.external.TransformData;

public abstract class BaseRenderer extends SystemComponent implements RenderEventListener {

    protected FFGraphics graphics;
    protected EntitySystem entitySystem;

    //protected final TransformDataCollector transformCollector = new TransformDataCollector();

    protected BaseRenderer( int id ) {
        super( id );
    }

    @Override
    public void init() {
        super.init();
        
        graphics = context.getGraphics();
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        context.registerListener( RenderEvent.TYPE_KEY, this );
    }

    @Override
    public void dispose() {
        context.disposeListener( RenderEvent.TYPE_KEY, this );
        
        super.dispose();
    };
    
    protected final void render( final SpriteRenderable sprite, final TransformDataCollector transformCollector ) {
        graphics.renderSprite( sprite, transformCollector );
    }
    
    protected final void render( final SpriteRenderable sprite, final int parentId, final TransformDataCollector transformCollector ) {
        if ( parentId >= 0 ) {
            collectTransformData( parentId, transformCollector );
        }
        
        graphics.renderSprite( sprite, transformCollector );
    }
    
    protected final void render( final EShape shape, final int parentId, final TransformDataCollector transformCollector ) {
        if ( parentId >= 0 ) {
            collectTransformData( parentId, transformCollector );
        }
        
        graphics.renderShape( shape,transformCollector );
    }
    
    private void collectTransformData( final int parentId, final TransformDataCollector transformCollector ) {
        ETransform parentTransform = entitySystem.getComponent( parentId, ETransform.TYPE_KEY );
        if ( parentTransform != null ) {
            transformCollector.add( parentTransform );
            if ( parentTransform.getParentId() >= 0 ) {
                collectTransformData( parentTransform.getParentId(), transformCollector );
            }
        }
        
    }
    
    public interface TransformDataCollector extends TransformData {
        void set( ETransform transform );
        void add( ETransform transform );
    }

    protected final class ExactTransformDataCollector implements TransformDataCollector {

        public float xpos, ypos;
        public float pivotx, pivoty;
        public float scalex, scaley;
        public float rotation;
        
        public ExactTransformDataCollector() {}
        
        @Override
        public final void set( ETransform transform ) {
            xpos = transform.getXpos();
            ypos = transform.getYpos();
            pivotx = transform.getPivotx();
            pivoty = transform.getPivoty();
            scalex = transform.getScalex();
            scaley = transform.getScaley();
            rotation = transform.getRotation();
        }
        
        @Override
        public final void add( ETransform transform ) {
            xpos += transform.getXpos();
            ypos += transform.getYpos();
            pivotx += transform.getPivotx();
            pivoty += transform.getPivoty();
            scalex += transform.getScalex();
            scaley += transform.getScaley();
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
    
    protected final class DiskreteTransformDataCollector implements TransformDataCollector {
        
        public float xpos, ypos;
        public float pivotx, pivoty;
        public float scalex, scaley;
        public float rotation;
        
        public DiskreteTransformDataCollector() {}
        
        @Override
        public final void set( ETransform transform ) {
            xpos = (float) Math.floor( transform.getXpos() );
            ypos = (float) Math.floor( transform.getYpos() );
            pivotx = (float) Math.floor( transform.getPivotx() );
            pivoty = (float) Math.floor( transform.getPivoty() );
            scalex = transform.getScalex();
            scaley = transform.getScaley();
            rotation = transform.getRotation();
        }
        
        @Override
        public final void add( ETransform transform ) {
            xpos += (float) Math.floor( transform.getXpos() );
            ypos += (float) Math.floor( transform.getYpos() );
            pivotx += (float) Math.floor( transform.getPivotx() );
            pivoty += (float) Math.floor( transform.getPivoty() );
            scalex += transform.getScalex();
            scaley += transform.getScaley();
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
