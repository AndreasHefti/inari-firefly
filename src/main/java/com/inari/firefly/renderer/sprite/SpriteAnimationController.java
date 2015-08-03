/*******************************************************************************
 * Copyright (c) 2015, Andreas Hefti, inarisoft@yahoo.de 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/ 
package com.inari.firefly.renderer.sprite;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
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
        animationSystem = context.getComponent( FFContext.System.ANIMATION_SYSTEM );
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
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        spriteAnimationId = attributes.getValue( SPRITE_ID_ANIMATION_ID, spriteAnimationId );
        tintRedAnimationId = attributes.getValue( TINT_RED_ANIMATION_ID, tintRedAnimationId );
        spriteAnimationId = attributes.getValue( TINT_GREEN_ANIMATION_ID, tintGreenAnimationId );
        spriteAnimationId = attributes.getValue( TINT_BLUE_ANIMATION_ID, tintBlueAnimationId );
        spriteAnimationId = attributes.getValue( TINT_ALPHA_ANIMATION_ID, tintAlphaAnimationId );
   
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( SPRITE_ID_ANIMATION_ID, spriteAnimationId );
        attributes.put( TINT_RED_ANIMATION_ID, tintRedAnimationId );
        attributes.put( TINT_GREEN_ANIMATION_ID, tintGreenAnimationId );
        attributes.put( TINT_BLUE_ANIMATION_ID, tintBlueAnimationId );
        attributes.put( TINT_ALPHA_ANIMATION_ID, tintAlphaAnimationId );
    } 

    @Override
    protected final int getControlledComponentTypeId() {
        return ESprite.COMPONENT_TYPE;
    }

    @Override
    protected final void update( long time, int entityId ) {
        ESprite sprite = entitySystem.getComponent( entityId, ESprite.COMPONENT_TYPE );
        RGBColor tintColor = sprite.getTintColor();

        if ( spriteAnimationId >= 0 && animationSystem.exists( spriteAnimationId ) ) {
            sprite.setSpriteId( animationSystem.getValue( spriteAnimationId, time, entityId, sprite.getSpriteId() ) );
        } else {
            spriteAnimationId = -1;
        }

        if ( tintRedAnimationId >= 0 && animationSystem.exists( tintRedAnimationId ) ) {
            tintColor.r = animationSystem.getValue( tintRedAnimationId, time, entityId, tintColor.r );
        } else {
            tintRedAnimationId = -1;
        }

        if ( tintGreenAnimationId >= 0 && animationSystem.exists( tintGreenAnimationId ) ) {
            tintColor.g = animationSystem.getValue( tintGreenAnimationId, time, entityId, tintColor.r );
        } else {
            tintGreenAnimationId = -1;
        }

        if ( tintBlueAnimationId >= 0 && animationSystem.exists( tintBlueAnimationId ) ) {
            tintColor.b = animationSystem.getValue( tintBlueAnimationId, time, entityId, tintColor.r );
        } else {
            tintBlueAnimationId = -1;
        }

        if ( tintAlphaAnimationId >= 0 && animationSystem.exists( tintAlphaAnimationId ) ) {
            tintColor.a = animationSystem.getValue( tintAlphaAnimationId, time, entityId, tintColor.r );
        } else {
            tintAlphaAnimationId = -1;
        }
    }

}
