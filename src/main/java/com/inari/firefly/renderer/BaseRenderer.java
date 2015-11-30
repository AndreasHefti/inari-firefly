package com.inari.firefly.renderer;

import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.FFSystemInterface;

public abstract class BaseRenderer implements FFContextInitiable {
    
    protected FFSystemInterface systemInterface;
    protected EntitySystem entitySystem;

    protected final TransformDataCollector transformCollector = new TransformDataCollector();
    
    @Override
    public void init( FFContext context ) throws FFInitException {
        systemInterface = context.getSystemInterface();
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
    }
    
    @Override
    public void dispose( FFContext context ) {
    }
    
    protected final void render( SpriteRenderable sprite ) {
        render( sprite, -1 );
    }
    
    protected final void render( SpriteRenderable sprite, int parentId ) {
        
        if ( parentId >= 0 ) {
            collectTransformData( parentId );
        }
        
        if ( transformCollector.scalex != 1 || transformCollector.scaley != 1 || transformCollector.rotation != 0 ) {
            systemInterface.renderSprite( 
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
            systemInterface.renderSprite( sprite, transformCollector.xpos, transformCollector.ypos );
        }
    }
    
    private void collectTransformData( int parentId ) {
        ETransform parentTransform = entitySystem.getComponent( parentId, ETransform.class );
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
