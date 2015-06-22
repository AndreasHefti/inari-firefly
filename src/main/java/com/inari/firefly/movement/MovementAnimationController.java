package com.inari.firefly.movement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.Vector2f;
import com.inari.firefly.FFContext;
import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.animation.FloatAnimation;
import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.AttributeMap;
import com.inari.firefly.entity.EntityController;

public class MovementAnimationController extends EntityController {
    
    public static final AttributeKey<Integer> VELOCITY_X_ANIMATION_ID = new AttributeKey<Integer>( "velocityXAnimationId", Integer.class, MovementAnimationController.class );
    public static final AttributeKey<Integer> VELOCITY_Y_ANIMATION_ID = new AttributeKey<Integer>( "velocityYAnimationId", Integer.class, MovementAnimationController.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        VELOCITY_X_ANIMATION_ID,
        VELOCITY_Y_ANIMATION_ID
    };
    
    private AnimationSystem animationSystem;

    private int velocityXAnimationId = -1;
    private int velocityYAnimationId = -1;

    MovementAnimationController( int id, FFContext context ) {
        super( id, context );
        animationSystem = context.get( FFContext.System.ANIMATION_SYSTEM );
    }

    public final int getVelocityXAnimationId() {
        return velocityXAnimationId;
    }

    public final void setVelocityXAnimationId( int velocityXAnimationId ) {
        this.velocityXAnimationId = velocityXAnimationId;
    }

    public final int getVelocityYAnimationId() {
        return velocityYAnimationId;
    }

    public final void setVelocityYAnimationId( int velocityYAnimationId ) {
        this.velocityYAnimationId = velocityYAnimationId;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) ) );
        return attributeKeys;
    }

    @Override
    public final void fromAttributeMap( AttributeMap attributes ) {
        super.fromAttributeMap( attributes );
        
        velocityXAnimationId = attributes.getValue( VELOCITY_X_ANIMATION_ID, velocityXAnimationId );
        velocityYAnimationId = attributes.getValue( VELOCITY_Y_ANIMATION_ID, velocityYAnimationId );
    }

    @Override
    public final void toAttributeMap( AttributeMap attributes ) {
        super.toAttributeMap( attributes );
        
        attributes.put( VELOCITY_X_ANIMATION_ID, velocityXAnimationId );
        attributes.put( VELOCITY_Y_ANIMATION_ID, velocityYAnimationId );
    }
    
    @Override
    protected int getControlledComponentTypeId() {
        return EMovement.COMPONENT_TYPE;
    }

    @Override
    protected final void update( long time, int entityId ) {
        EMovement movement = entitySystem.getComponent( entityId, EMovement.COMPONENT_TYPE );
        Vector2f velocityVector = movement.getVelocityVector();
        if ( velocityXAnimationId >= 0 ) {
            FloatAnimation animation = animationSystem.getAnimation( FloatAnimation.class, velocityXAnimationId );
            if ( animation != null ) {
                if ( animation.isActive() ) {
                    velocityVector.dx = animation.get( entityId, velocityVector.dx );
                }
            } else {
                velocityXAnimationId = -1;
                velocityVector.dx = 0;
            }
        } 
        if ( velocityYAnimationId >= 0 ) {
            FloatAnimation animation = animationSystem.getAnimation( FloatAnimation.class, velocityYAnimationId );
            if ( animation != null ) {
                if ( animation.isActive() ) {
                    velocityVector.dy = animation.get( entityId, velocityVector.dy );
                }
            } else {
                velocityYAnimationId = -1;
                velocityVector.dy = 0;
            }
        }
    }

}
