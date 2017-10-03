/*******************************************************************************
 * Copyright (c) 2015 - 2016, Andreas Hefti, inarisoft@yahoo.de 
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
package com.inari.firefly.graphics.sprite;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.graphics.BlendMode;
import com.inari.firefly.graphics.rendering.SpriteRenderable;
import com.inari.firefly.physics.animation.AttributeAnimationAdapter.AttributeAnimationAdapterKey;
import com.inari.firefly.physics.animation.EntityFloatAnimationAdapter;
import com.inari.firefly.physics.animation.EntityIntAnimationAdapter;
import com.inari.firefly.physics.animation.EntityValueAnimationAdapter;
import com.inari.firefly.physics.animation.FloatAnimation;
import com.inari.firefly.physics.animation.IntAnimation;
import com.inari.firefly.physics.animation.ValueAnimation;
import com.inari.firefly.system.FFContext;

public final class ESprite extends EntityComponent implements SpriteRenderable {
    
    public static final EntityComponentTypeKey<ESprite> TYPE_KEY = EntityComponentTypeKey.create( ESprite.class );

    public static final AttributeKey<String> SPRITE_ASSET_NAME = AttributeKey.createString( "spriteAssetName", ESprite.class );
    public static final AttributeKey<Integer> SPRITE_ID = AttributeKey.createInt( "spriteId", ESprite.class );
    public static final AttributeKey<RGBColor> TINT_COLOR = AttributeKey.createColor( "tintColor", ESprite.class );
    public static final AttributeKey<BlendMode> BLEND_MODE = AttributeKey.createBlendMode( "blendMode", ESprite.class );
    public static final AttributeKey<String> SHADER_ASSET_NAME = AttributeKey.createString( "shaderAssetName", ESprite.class );
    public static final AttributeKey<Integer> SHADER_ID = AttributeKey.createInt( "shaderId", ESprite.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        SPRITE_ID,
        TINT_COLOR,
        BLEND_MODE,
        SHADER_ID
    );

    private int spriteId;
    private final RGBColor tintColor;
    private BlendMode blendMode;
    private int shaderId;
    
    
    ESprite() {
        super( TYPE_KEY );
        tintColor = new RGBColor();
        resetAttributes();
    }

    @Override
    public final void resetAttributes() {
        spriteId = -1;
        setTintColor( new RGBColor( 1, 1, 1, 1 ) );
        blendMode = BlendMode.NONE;
        shaderId = -1;
    }

    @Override
    public final int getSpriteId() {
        return spriteId;
    }

    public final void setSpriteId( int spriteId ) {
        this.spriteId = spriteId;
    }

    @Override
    public final RGBColor getTintColor() {
        return tintColor;
    }

    public final void setTintColor( RGBColor tintColor ) {
        this.tintColor.r = tintColor.r;
        this.tintColor.g = tintColor.g;
        this.tintColor.b = tintColor.b;
        this.tintColor.a = tintColor.a;
    }

    @Override
    public final BlendMode getBlendMode() {
        return blendMode;
    }

    public final void setBlendMode( BlendMode blendMode ) {
        this.blendMode = blendMode;
    }

    @Override
    public final int getShaderId() {
        return shaderId;
    }

    public final void setShaderId( int shaderId ) {
        this.shaderId = shaderId;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return ATTRIBUTE_KEYS;
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        spriteId = attributes.getAssetInstanceId( SPRITE_ASSET_NAME, SPRITE_ID, spriteId );
        setTintColor( attributes.getValue( TINT_COLOR, tintColor ) );
        blendMode = attributes.getValue( BLEND_MODE, blendMode );
        shaderId = attributes.getAssetInstanceId( SHADER_ASSET_NAME, SHADER_ID, shaderId );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( SPRITE_ID, spriteId );
        attributes.put( TINT_COLOR, new RGBColor( tintColor ) );
        attributes.put( BLEND_MODE, blendMode );
        attributes.put( SHADER_ID, shaderId );
    }
    
    
    public interface AnimationAdapter {
        AttributeAnimationAdapterKey<SpriteIdAnimationAdapter> SPRITE_ID = SpriteIdAnimationAdapter.TYPE_KEY;
        AttributeAnimationAdapterKey<TintColorRedAnimationAdapter> TINT_COLOR_RED = TintColorRedAnimationAdapter.TYPE_KEY;
        AttributeAnimationAdapterKey<TintColorGreenAnimationAdapter> TINT_COLOR_GREEN = TintColorGreenAnimationAdapter.TYPE_KEY;
        AttributeAnimationAdapterKey<TintColorBlueAnimationAdapter> TINT_COLOR_BLUE = TintColorBlueAnimationAdapter.TYPE_KEY;
        AttributeAnimationAdapterKey<TintColorAlphaAnimationAdapter> TINT_COLOR_ALPHA = TintColorAlphaAnimationAdapter.TYPE_KEY;
        AttributeAnimationAdapterKey<TintColorAnimationAdapter> TINT_COLOR = TintColorAnimationAdapter.TYPE_KEY;
    }
    
    private static final class SpriteIdAnimationAdapter implements EntityIntAnimationAdapter {
        public static final AttributeAnimationAdapterKey<SpriteIdAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new SpriteIdAnimationAdapter() );
        public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        public final void apply( int entityId, IntAnimation animation, FFContext context ) {
            final ESprite sprite = context.getEntityComponent( entityId, ESprite.TYPE_KEY );
            sprite.setSpriteId( animation.getValue( entityId, sprite.getSpriteId() ) );
        }
    }
    
    private static final class TintColorRedAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<TintColorRedAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new TintColorRedAnimationAdapter() );
        public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        public final void apply( int entityId, FloatAnimation animation, FFContext context ) {
            final ESprite sprite = context.getEntityComponent( entityId, ESprite.TYPE_KEY );
            final RGBColor tintColor = sprite.getTintColor();
            tintColor.r = animation.getValue( entityId, tintColor.r );
        }
    }
    
    private static final class TintColorGreenAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<TintColorGreenAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new TintColorGreenAnimationAdapter() );
        public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        public final void apply( int entityId, FloatAnimation animation, FFContext context ) {
            final ESprite sprite = context.getEntityComponent( entityId, ESprite.TYPE_KEY );
            final RGBColor tintColor = sprite.getTintColor();
            tintColor.g = animation.getValue( entityId, tintColor.g );
        }
    }
    
    private static final class TintColorBlueAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<TintColorBlueAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new TintColorBlueAnimationAdapter() );
        public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        public final void apply( int entityId, FloatAnimation animation, FFContext context ) {
            final ESprite sprite = context.getEntityComponent( entityId, ESprite.TYPE_KEY );
            final RGBColor tintColor = sprite.getTintColor();
            tintColor.b = animation.getValue( entityId, tintColor.b );
        }
    }
    
    private static final class TintColorAlphaAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<TintColorAlphaAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new TintColorAlphaAnimationAdapter() );
        public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        public final void apply( int entityId, FloatAnimation animation, FFContext context ) {
            final ESprite sprite = context.getEntityComponent( entityId, ESprite.TYPE_KEY );
            final RGBColor tintColor = sprite.getTintColor();
            tintColor.a = animation.getValue( entityId, tintColor.a );
        }
    }
    
    private static final class TintColorAnimationAdapter implements EntityValueAnimationAdapter<RGBColor> {
        public static final AttributeAnimationAdapterKey<TintColorAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new TintColorAnimationAdapter() );
        public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        public final void apply( int entityId, ValueAnimation<RGBColor> animation, FFContext context ) {
            final ESprite sprite = context.getEntityComponent( entityId, ESprite.TYPE_KEY );
            sprite.setTintColor( animation.getValue( entityId, sprite.getTintColor() ) );
        }
    }
}
