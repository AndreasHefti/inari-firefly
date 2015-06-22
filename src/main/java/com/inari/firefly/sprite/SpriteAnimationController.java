package com.inari.firefly.sprite;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.FFContext;
import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.animation.FloatAnimation;
import com.inari.firefly.animation.IntAnimation;
import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.AttributeMap;
import com.inari.firefly.entity.EntityController;

public class SpriteAnimationController extends EntityController {
    
    public static final AttributeKey<Integer> SPRITE_ID_ANIMATION_ID = new AttributeKey<Integer>( "spriteAnimationId", Integer.class, SpriteAnimationController.class );
    public static final AttributeKey<Integer> TINT_RED_ANIMATION_ID = new AttributeKey<Integer>( "tintRedAnimationId", Integer.class, SpriteAnimationController.class );
    public static final AttributeKey<Integer> TINT_GREEN_ANIMATION_ID = new AttributeKey<Integer>( "tintGreenAnimationId", Integer.class, SpriteAnimationController.class );
    public static final AttributeKey<Integer> TINT_BLUE_ANIMATION_ID = new AttributeKey<Integer>( "tintBlueAnimationId", Integer.class, SpriteAnimationController.class );
    public static final AttributeKey<Integer> TINT_ALPHA_ANIMATION_ID = new AttributeKey<Integer>( "tintAlphaAnimationId", Integer.class, SpriteAnimationController.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        SPRITE_ID_ANIMATION_ID,
        TINT_RED_ANIMATION_ID,
        TINT_GREEN_ANIMATION_ID,
        TINT_BLUE_ANIMATION_ID,
        TINT_BLUE_ANIMATION_ID
    };
    

    
    private final AnimationSystem animationSystem;
    
    private int spriteAnimationId = -1;
    private int tintRedAnimationId = -1;
    private int tintGreenAnimationId = -1;
    private int tintBlueAnimationId = -1;
    private int tintAlphaAnimationId = -1;
    
    SpriteAnimationController( int id, FFContext context ) {
        super( id, context );
        animationSystem = context.get( FFContext.System.ANIMATION_SYSTEM );
    }

    public final int getSpriteAnimationId() {
        return spriteAnimationId;
    }

    public final void setSpriteAnimationId( int spriteAnimationId ) {
        this.spriteAnimationId = spriteAnimationId;
    }

    public final int getTintRedAnimationId() {
        return tintRedAnimationId;
    }

    public final void setTintRedAnimationId( int tintRedAnimationId ) {
        this.tintRedAnimationId = tintRedAnimationId;
    }

    public final int getTintGreenAnimationId() {
        return tintGreenAnimationId;
    }

    public final void setTintGreenAnimationId( int tintGreenAnimationId ) {
        this.tintGreenAnimationId = tintGreenAnimationId;
    }

    public final int getTintBlueAnimationId() {
        return tintBlueAnimationId;
    }

    public final void setTintBlueAnimationId( int tintBlueAnimationId ) {
        this.tintBlueAnimationId = tintBlueAnimationId;
    }

    public final int getTintAlphaAnimationId() {
        return tintAlphaAnimationId;
    }

    public final void setTintAlphaAnimationId( int tintAlphaAnimationId ) {
        this.tintAlphaAnimationId = tintAlphaAnimationId;
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
        
        spriteAnimationId = attributes.getValue( SPRITE_ID_ANIMATION_ID, spriteAnimationId );
        tintRedAnimationId = attributes.getValue( TINT_RED_ANIMATION_ID, tintRedAnimationId );
        spriteAnimationId = attributes.getValue( TINT_GREEN_ANIMATION_ID, tintGreenAnimationId );
        spriteAnimationId = attributes.getValue( TINT_BLUE_ANIMATION_ID, tintBlueAnimationId );
        spriteAnimationId = attributes.getValue( TINT_ALPHA_ANIMATION_ID, tintAlphaAnimationId );
   
    }

    @Override
    public final void toAttributeMap( AttributeMap attributes ) {
        super.toAttributeMap( attributes );
        
        attributes.put( SPRITE_ID_ANIMATION_ID, spriteAnimationId );
        attributes.put( TINT_RED_ANIMATION_ID, tintRedAnimationId );
        attributes.put( TINT_GREEN_ANIMATION_ID, tintGreenAnimationId );
        attributes.put( TINT_BLUE_ANIMATION_ID, tintBlueAnimationId );
        attributes.put( TINT_ALPHA_ANIMATION_ID, tintAlphaAnimationId );
    }

    @Override
    protected int getControlledComponentTypeId() {
        return ESprite.COMPONENT_TYPE;
    }

    @Override
    protected final void update( long time, int entityId ) {
        ESprite sprite = entitySystem.getComponent( entityId, ESprite.COMPONENT_TYPE );
        RGBColor tintColor = sprite.getTintColor();
        if ( spriteAnimationId >= 0 ) {
            IntAnimation animation = animationSystem.getAnimation( IntAnimation.class, spriteAnimationId );
            if ( animation != null ) {
                if ( animation.isActive() ) {
                    sprite.setSpriteId( animation.get( entityId, sprite.getSpriteId() ) );
                }
            } else {
                spriteAnimationId = -1;
            }
        } 
        if ( tintRedAnimationId >= 0 ) {
            FloatAnimation animation = animationSystem.getAnimation( FloatAnimation.class, tintRedAnimationId );
            if ( animation != null ) {
                if ( animation.isActive() ) {
                    tintColor.r = animation.get( entityId, tintColor.r );
                }
            } else {
                tintRedAnimationId = -1;
            }
        }
        if ( tintGreenAnimationId >= 0 ) {
            FloatAnimation animation = animationSystem.getAnimation( FloatAnimation.class, tintGreenAnimationId );
            if ( animation != null ) {
                if ( animation.isActive() ) {
                    tintColor.g = animation.get( entityId, tintColor.r );
                }
            } else {
                tintGreenAnimationId = -1;
            }
        }
        if ( tintBlueAnimationId >= 0 ) {
            FloatAnimation animation = animationSystem.getAnimation( FloatAnimation.class, tintBlueAnimationId );
            if ( animation != null ) {
                if ( animation.isActive() ) {
                    tintColor.b = animation.get( entityId, tintColor.r );
                }
            } else {
                tintBlueAnimationId = -1;
            }
        }
        if ( tintAlphaAnimationId >= 0 ) {
            FloatAnimation animation = animationSystem.getAnimation( FloatAnimation.class, tintAlphaAnimationId );
            if ( animation != null ) {
                if ( animation.isActive() ) {
                    tintColor.a = animation.get( entityId, tintColor.r );
                }
            } else {
                tintAlphaAnimationId = -1;
            }
        }
    }

}
