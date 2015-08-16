package com.inari.firefly.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFContext;

public class TransformAnimationContoller extends EntityController {
    
    public static final AttributeKey<Integer> X_POSITION_ANIMATION_ID = new AttributeKey<Integer>( "xposAnimationId", Integer.class, TransformAnimationContoller.class );
    public static final AttributeKey<Integer> Y_POSITION_ANIMATION_ID = new AttributeKey<Integer>( "yposAnimationId", Integer.class, TransformAnimationContoller.class );
    public static final AttributeKey<Integer> X_SCALE_ANIMATION_ID = new AttributeKey<Integer>( "xscaleAnimationId", Integer.class, TransformAnimationContoller.class );
    public static final AttributeKey<Integer> Y_SCALE_ANIMATION_ID = new AttributeKey<Integer>( "yscaleAnimationId", Integer.class, TransformAnimationContoller.class );
    public static final AttributeKey<Integer> ROTATION_ANIMATION_ID = new AttributeKey<Integer>( "rotAnimationId", Integer.class, TransformAnimationContoller.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        X_POSITION_ANIMATION_ID,
        Y_POSITION_ANIMATION_ID,
        X_SCALE_ANIMATION_ID,
        Y_SCALE_ANIMATION_ID,
        ROTATION_ANIMATION_ID
    };
    
    private final AnimationSystem animationSystem;
    
    private int xposAnimationId, 
                yposAnimationId, 
                xscaleAnimationId, 
                yscaleAnimationId, 
                rotAnimationId = -1;
    
    TransformAnimationContoller( int id, FFContext context ) {
        super( id, context );
        animationSystem = context.getComponent( FFContext.Systems.ANIMATION_SYSTEM );
    }
    
    @Override
    protected final int getControlledComponentTypeId() {
        return ETransform.COMPONENT_TYPE;
    }

    public final int getXposAnimationId() {
        return xposAnimationId;
    }

    public final void setXposAnimationId( int xposAnimationId ) {
        this.xposAnimationId = xposAnimationId;
    }

    public final int getYposAnimationId() {
        return yposAnimationId;
    }

    public final void setYposAnimationId( int yposAnimationId ) {
        this.yposAnimationId = yposAnimationId;
    }

    public final int getXscaleAnimationId() {
        return xscaleAnimationId;
    }

    public final void setXscaleAnimationId( int xscaleAnimationId ) {
        this.xscaleAnimationId = xscaleAnimationId;
    }

    public final int getYscaleAnimationId() {
        return yscaleAnimationId;
    }

    public final void setYscaleAnimationId( int yscaleAnimationId ) {
        this.yscaleAnimationId = yscaleAnimationId;
    }

    public final int getRotAnimationId() {
        return rotAnimationId;
    }

    public final void setRotAnimationId( int rotAnimationId ) {
        this.rotAnimationId = rotAnimationId;
    }

    public final AnimationSystem getAnimationSystem() {
        return animationSystem;
    }
    
    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) ) );
        return attributeKeys;
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        xposAnimationId = attributes.getValue( X_POSITION_ANIMATION_ID, xposAnimationId );
        yposAnimationId = attributes.getValue( Y_POSITION_ANIMATION_ID, yposAnimationId );
        xscaleAnimationId = attributes.getValue( X_SCALE_ANIMATION_ID, xscaleAnimationId );
        yscaleAnimationId = attributes.getValue( Y_SCALE_ANIMATION_ID, yscaleAnimationId );
        rotAnimationId = attributes.getValue( ROTATION_ANIMATION_ID, rotAnimationId );
   
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( X_POSITION_ANIMATION_ID, xposAnimationId );
        attributes.put( Y_POSITION_ANIMATION_ID, yposAnimationId );
        attributes.put( X_SCALE_ANIMATION_ID, xscaleAnimationId );
        attributes.put( Y_SCALE_ANIMATION_ID, yscaleAnimationId );
        attributes.put( ROTATION_ANIMATION_ID, rotAnimationId );
    } 
    
    @Override
    protected final void update( long time, int entityId ) {
        ETransform transform = entitySystem.getComponent( entityId, ETransform.COMPONENT_TYPE );

        if ( animationActive( xposAnimationId ) ) {
            transform.setXpos( animationSystem.getValue( xposAnimationId, time, entityId, transform.getXpos() ) );
        } else {
            xposAnimationId = -1;
        }

        if ( animationActive( yposAnimationId ) ) {
            transform.setYpos( animationSystem.getValue( yposAnimationId, time, entityId, transform.getYpos() ) );
        } else {
            yposAnimationId = -1;
        }

        if ( animationActive( xscaleAnimationId ) ) {
            transform.setScalex( animationSystem.getValue( xscaleAnimationId, time, entityId, transform.getScalex() ) );
        } else {
            xscaleAnimationId = -1;
        }

        if ( animationActive( yscaleAnimationId ) ) {
            transform.setScaley( animationSystem.getValue( yscaleAnimationId, time, entityId, transform.getScaley() ) );
        } else {
            yscaleAnimationId = -1;
        }
        
        if ( animationActive( rotAnimationId ) ) {
            transform.setRotation( animationSystem.getValue( rotAnimationId, time, entityId, transform.getRotation() ) );
        } else {
            rotAnimationId = -1;
        }
    }

    private boolean animationActive( int animationId ) {
        return animationId >= 0 && animationSystem.exists( animationId );
    }
    
}
