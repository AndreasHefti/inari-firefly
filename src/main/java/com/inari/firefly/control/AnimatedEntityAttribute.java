package com.inari.firefly.control;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.physics.animation.Animation;
import com.inari.firefly.physics.animation.AnimationResolver;
import com.inari.firefly.physics.animation.AnimationSystem;

public abstract class AnimatedEntityAttribute extends EntityController {

    public static final AttributeKey<String> ANIMATION_NAME = new AttributeKey<String>( "animationName", String.class, AnimatedEntityAttribute.class );
    public static final AttributeKey<Integer> ANIMATION_ID = new AttributeKey<Integer>( "animationId", Integer.class, AnimatedEntityAttribute.class );
    public static final AttributeKey<String> ANIMATION_RESOLVER_NAME = new AttributeKey<String>( "animationResolverName", String.class, AnimatedEntityAttribute.class );
    public static final AttributeKey<Integer> ANIMATION_RESOLVER_ID = new AttributeKey<Integer>( "animationResolverId", Integer.class, AnimatedEntityAttribute.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        ANIMATION_ID,
        ANIMATION_RESOLVER_ID
    };
    
    protected int animationId;
    protected int animationResolverId;
    
    protected AnimatedEntityAttribute( int id ) {
        super( id );
        
        animationId = -1;
        animationResolverId = -1;
    }

    public final int getAnimationId() {
        return animationId;
    }

    public final void setAnimationId( int animationId ) {
        this.animationId = animationId;
    }

    public final int getAnimationResolverId() {
        return animationResolverId;
    }

    public final void setAnimationResolverId( int animationResolverId ) {
        this.animationResolverId = animationResolverId;
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
        
        animationId = attributes.getIdForName( ANIMATION_NAME, ANIMATION_ID, Animation.TYPE_KEY, animationId );
        animationResolverId = attributes.getIdForName( ANIMATION_RESOLVER_NAME, ANIMATION_RESOLVER_ID, AnimationResolver.TYPE_KEY, animationResolverId );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( ANIMATION_ID, animationId );
        attributes.put( ANIMATION_RESOLVER_ID, animationResolverId );
    } 
    
    protected final void updateAnimationId( final AnimationSystem animationSystem ) {
        if ( animationResolverId >= 0 ) {
            animationId = animationSystem.getAnimationId( animationResolverId, animationId );
        }
    }

    public abstract AttributeKey<?> getControlledAttribute();
    
}
