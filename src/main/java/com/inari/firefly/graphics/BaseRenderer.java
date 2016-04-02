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

    protected final TransformDataCollector transformCollector = new TransformDataCollector();

    protected BaseRenderer( int id ) {
        super( id );
    }

    @Override
    public void init() {
        super.init();
        
        graphics = context.getGraphics();
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        context.registerListener( RenderEvent.class, this );
    }

    @Override
    public void dispose() {
        context.disposeListener( RenderEvent.class, this );
        
        super.dispose();
    };
    
    protected final void render( SpriteRenderable sprite ) {
        graphics.renderSprite( sprite, transformCollector );
    }
    
    protected final void render( SpriteRenderable sprite, int parentId ) {
        if ( parentId >= 0 ) {
            collectTransformData( parentId );
        }
        
        graphics.renderSprite( sprite, transformCollector );
    }
    
    protected final void render( EShape shape ) {
        graphics.renderShape( 
            shape.getShapeType(), 
            shape.getVertices(), 
            shape.getSegments(), 
            shape.getColors(), 
            shape.getBlendMode(), 
            shape.isFill(),
            shape.getShaderId()
        );
    }
    
    protected final void render( EShape shape, int parentId ) {
        if ( parentId >= 0 ) {
            collectTransformData( parentId );
        }
        
        graphics.renderShape( 
            shape.getShapeType(), 
            shape.getVertices(), 
            shape.getSegments(), 
            shape.getColors(), 
            shape.getBlendMode(), 
            shape.isFill(), 
            shape.getShaderId(),
            transformCollector 
        );
    }
    
    private void collectTransformData( int parentId ) {
        ETransform parentTransform = entitySystem.getComponent( parentId, ETransform.TYPE_KEY );
        if ( parentTransform != null ) {
            transformCollector.add( parentTransform );
            if ( parentTransform.getParentId() >= 0 ) {
                collectTransformData( parentTransform.getParentId() );
            }
        }
        
    }

    protected final class TransformDataCollector implements TransformData {
        
        public float xpos, ypos;
        public float pivotx, pivoty;
        public float scalex, scaley;
        public float rotation;
        
        public final void set( ETransform transform ) {
            xpos = transform.getXpos();
            ypos = transform.getYpos();
            pivotx = transform.getPivotx();
            pivoty = transform.getPivoty();
            scalex = transform.getScalex();
            scaley = transform.getScaley();
            rotation = transform.getRotation();
        }
        
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

}
