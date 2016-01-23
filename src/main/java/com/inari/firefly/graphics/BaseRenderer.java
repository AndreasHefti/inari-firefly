package com.inari.firefly.graphics;

import com.inari.firefly.Disposable;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.RenderEventListener;
import com.inari.firefly.system.component.SystemComponent;
import com.inari.firefly.system.external.FFGraphics;

public abstract class BaseRenderer extends SystemComponent implements RenderEventListener, Disposable {

    protected FFGraphics graphics;
    protected EntitySystem entitySystem;

    protected final TransformDataCollector transformCollector = new TransformDataCollector();

    protected BaseRenderer( int id, FFContext context ) {
        super( id );
        
        graphics = context.getGraphics();
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        context.registerListener( RenderEvent.class, this );
    }
    
    @Override
    public void dispose( FFContext context ) {
        super.dispose();
        context.disposeListener( RenderEvent.class, this );
    };
    
    protected final void render( SpriteRenderable sprite ) {
        render( sprite, -1 );
    }
    
    protected final void render( SpriteRenderable sprite, int parentId ) {
        
        if ( parentId >= 0 ) {
            collectTransformData( parentId );
        }
        
        if ( transformCollector.scalex != 1 || transformCollector.scaley != 1 || transformCollector.rotation != 0 ) {
            graphics.renderSprite( 
                sprite, 
                transformCollector.xpos, 
                transformCollector.ypos, 
                transformCollector.pivotx,
                transformCollector.pivoty,
                transformCollector.scalex,
                transformCollector.scaley,
                transformCollector.rotation
            );
        } else {
            graphics.renderSprite( sprite, transformCollector.xpos, transformCollector.ypos );
        }
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

    protected final class TransformDataCollector {
        
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
        
    }

}
