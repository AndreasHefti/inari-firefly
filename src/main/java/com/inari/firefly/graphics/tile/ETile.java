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
package com.inari.firefly.graphics.tile;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.geom.Position;
import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.commons.lang.list.DynArray;
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

public final class ETile extends EntityComponent implements SpriteRenderable {
    
    public static final EntityComponentTypeKey<ETile> TYPE_KEY = EntityComponentTypeKey.create( ETile.class );
    
    public static final AttributeKey<String> SPRITE_ASSET_NAME = AttributeKey.createString( "spriteAssetName", ETile.class );
    public static final AttributeKey<Integer> SPRITE_ID = AttributeKey.createInt( "spriteId", ETile.class ); 
    public static final AttributeKey<RGBColor> TINT_COLOR = AttributeKey.create( "tintColor", RGBColor.class, ETile.class );
    public static final AttributeKey<BlendMode> BLEND_MODE = AttributeKey.create( "blendMode", BlendMode.class, ETile.class );
    public static final AttributeKey<String> SHADER_ASSET_NAME = AttributeKey.createString( "shaderAssetName", ETile.class );
    public static final AttributeKey<Integer> SHADER_ID = AttributeKey.createInt( "shaderId", ETile.class );
    public static final AttributeKey<DynArray<Position>> GRID_POSITIONS = AttributeKey.createDynArray( "gridPositions", ETile.class, Position.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        SPRITE_ID,
        TINT_COLOR,
        BLEND_MODE,
        SHADER_ID,
        GRID_POSITIONS
    );
    
    private int spriteId;
    private final RGBColor tintColor = new RGBColor();
    private BlendMode blendMode;
    private int shaderId;
    private final DynArray<Position> gridPositions;

    public ETile() {
        super( TYPE_KEY );
        gridPositions = DynArray.create( Position.class, 100, 20 );
        resetAttributes();
        
    }

    @Override
    public final void resetAttributes() {
        spriteId = -1;
        setTintColor( new RGBColor( 1, 1, 1, 1 ) );
        blendMode = BlendMode.NONE;
        shaderId = -1;
        gridPositions.clear();
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

    public final DynArray<Position> getGridPositions() {
        return gridPositions;
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
        
        gridPositions.clear();
        if ( attributes.contains( GRID_POSITIONS ) ) {
            gridPositions.addAll( attributes.getValue( GRID_POSITIONS ) );
        }
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( SPRITE_ID, spriteId );
        attributes.put( TINT_COLOR, new RGBColor( tintColor ) );
        attributes.put( BLEND_MODE, blendMode );
        attributes.put( SHADER_ID, shaderId );
        attributes.put( GRID_POSITIONS, gridPositions );
    }
    
    public interface AnimationAdapter {
        AttributeAnimationAdapterKey<TileSpriteIdAnimationAdapter> SPRITE_ID = TileSpriteIdAnimationAdapter.TYPE_KEY;
        AttributeAnimationAdapterKey<TintColorRedAnimationAdapter> TINT_COLOR_RED = TintColorRedAnimationAdapter.TYPE_KEY;
        AttributeAnimationAdapterKey<TintColorGreenAnimationAdapter> TINT_COLOR_GREEN = TintColorGreenAnimationAdapter.TYPE_KEY;
        AttributeAnimationAdapterKey<TintColorBlueAnimationAdapter> TINT_COLOR_BLUE = TintColorBlueAnimationAdapter.TYPE_KEY;
        AttributeAnimationAdapterKey<TintColorAlphaAnimationAdapter> TINT_COLOR_ALPHA = TintColorAlphaAnimationAdapter.TYPE_KEY;
        AttributeAnimationAdapterKey<TintColorAnimationAdapter> TINT_COLOR = TintColorAnimationAdapter.TYPE_KEY;
    }
    
    private static final class TileSpriteIdAnimationAdapter implements EntityIntAnimationAdapter {
        public static final AttributeAnimationAdapterKey<TileSpriteIdAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new TileSpriteIdAnimationAdapter() );
        @Override public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        @Override
        public final void apply( int entityId, IntAnimation animation, FFContext context ) {
            final ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
            tile.setSpriteId( animation.getValue( entityId, tile.getSpriteId() ) );
        }
    }
    
    private static final class TintColorRedAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<TintColorRedAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new TintColorRedAnimationAdapter() );
        @Override public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        @Override
        public final void apply( int entityId, FloatAnimation animation, FFContext context ) {
            final ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
            final RGBColor tintColor = tile.getTintColor();
            tintColor.r = animation.getValue( entityId, tintColor.r );
        }
    }
    
    private static final class TintColorGreenAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<TintColorGreenAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new TintColorGreenAnimationAdapter() );
        @Override public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        @Override
        public final void apply( int entityId, FloatAnimation animation, FFContext context ) {
            final ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
            final RGBColor tintColor = tile.getTintColor();
            tintColor.g = animation.getValue( entityId, tintColor.g );
        }
    }
    
    private static final class TintColorBlueAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<TintColorBlueAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new TintColorBlueAnimationAdapter() );
        @Override public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        @Override
        public final void apply( int entityId, FloatAnimation animation, FFContext context ) {
            final ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
            final RGBColor tintColor = tile.getTintColor();
            tintColor.b = animation.getValue( entityId, tintColor.b );
        }
    }
    
    private static final class TintColorAlphaAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<TintColorAlphaAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new TintColorAlphaAnimationAdapter() );
        @Override public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        @Override
        public final void apply( int entityId, FloatAnimation animation, FFContext context ) {
            final ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
            final RGBColor tintColor = tile.getTintColor();
            tintColor.a = animation.getValue( entityId, tintColor.a );
        }
    }
    
    private static final class TintColorAnimationAdapter implements EntityValueAnimationAdapter<RGBColor> {
        public static final AttributeAnimationAdapterKey<TintColorAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new TintColorAnimationAdapter() );
        @Override public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        @Override
        public final void apply( int entityId, ValueAnimation<RGBColor> animation, FFContext context ) {
            final ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
            tile.setTintColor( animation.getValue( entityId, tile.getTintColor() ) );
        }
    }

}
