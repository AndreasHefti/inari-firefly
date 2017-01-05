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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.PositionF;
import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.graphics.BlendMode;
import com.inari.firefly.graphics.SpriteRenderable;

public final class ESprite extends EntityComponent implements SpriteRenderable {
    
    public static final EntityComponentTypeKey<ESprite> TYPE_KEY = EntityComponentTypeKey.create( ESprite.class );

    public static final AttributeKey<String> SPRITE_ASSET_NAME = new AttributeKey<String>( "spriteAssetName", String.class, ESprite.class );
    public static final AttributeKey<Integer> SPRITE_ID = new AttributeKey<Integer>( "spriteId", Integer.class, ESprite.class );
    public static final AttributeKey<Integer> ORDERING = new AttributeKey<Integer>( "ordering", Integer.class, ESprite.class );
    public static final AttributeKey<RGBColor> TINT_COLOR = new AttributeKey<RGBColor>( "tintColor", RGBColor.class, ESprite.class );
    public static final AttributeKey<BlendMode> BLEND_MODE = new AttributeKey<BlendMode>( "blendMode", BlendMode.class, ESprite.class );
    public static final AttributeKey<String> SHADER_ASSET_NAME = new AttributeKey<String>( "shaderAssetName", String.class, ESprite.class );
    public static final AttributeKey<Integer> SHADER_ID = new AttributeKey<Integer>( "shaderId", Integer.class, ESprite.class );
    public static final AttributeKey<DynArray<PositionF>> MULTI_POSITIONS = AttributeKey.createDynArray( "positions", ESprite.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        SPRITE_ID,
        ORDERING,
        TINT_COLOR,
        BLEND_MODE,
        SHADER_ID,
        MULTI_POSITIONS
    };

    private int spriteId;
    private int ordering;
    private final RGBColor tintColor;
    private BlendMode blendMode;
    private int shaderId;
    private DynArray<PositionF> positions;
    
    public ESprite() {
        super( TYPE_KEY );
        tintColor = new RGBColor();
        positions = new DynArray<PositionF>();
        resetAttributes();
    }

    @Override
    public final void resetAttributes() {
        spriteId = -1;
        setTintColor( new RGBColor( 1, 1, 1, 1 ) );
        blendMode = BlendMode.NONE;
        shaderId = -1;
        positions.clear();
    }

    @Override
    public final int getSpriteId() {
        return spriteId;
    }

    public final void setSpriteId( int spriteId ) {
        this.spriteId = spriteId;
    }
    
    @Override
    public final int getOrdering() {
        return ordering;
    }

    public final void setOrdering( int ordering ) {
        this.ordering = ordering;
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
    
    public final boolean isMultiPosition() {
        return !positions.isEmpty();
    }
    
    public final DynArray<PositionF> getPositions() {
        return positions;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        spriteId = attributes.getAssetInstanceId( SPRITE_ASSET_NAME, SPRITE_ID, spriteId );
        ordering = attributes.getValue( ORDERING, ordering );
        setTintColor( attributes.getValue( TINT_COLOR, tintColor ) );
        blendMode = attributes.getValue( BLEND_MODE, blendMode );
        shaderId = attributes.getAssetInstanceId( SHADER_ASSET_NAME, SHADER_ID, shaderId );
        
        positions.clear();
        if ( attributes.contains( MULTI_POSITIONS ) ) {
            positions.addAll( attributes.getValue( MULTI_POSITIONS ) );
        }
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( SPRITE_ID, spriteId );
        attributes.put( ORDERING, ordering );
        attributes.put( TINT_COLOR, new RGBColor( tintColor ) );
        attributes.put( BLEND_MODE, blendMode );
        attributes.put( SHADER_ID, shaderId );
        
        if ( isMultiPosition() ) {
            attributes.put( MULTI_POSITIONS, positions );
        }
    }
}
