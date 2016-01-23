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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.Position;
import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.graphics.BlendMode;
import com.inari.firefly.graphics.SpriteRenderable;

public final class ETile extends EntityComponent implements SpriteRenderable {
    
    public static final EntityComponentTypeKey<ETile> TYPE_KEY = EntityComponentTypeKey.create( ETile.class );
    
    public static final AttributeKey<Integer> SPRITE_ID = new AttributeKey<Integer>( "spriteId", Integer.class, ETile.class );
    public static final AttributeKey<RGBColor> TINT_COLOR = new AttributeKey<RGBColor>( "tintColor", RGBColor.class, ETile.class );
    public static final AttributeKey<BlendMode> BLEND_MODE = new AttributeKey<BlendMode>( "blendMode", BlendMode.class, ETile.class );
    public static final AttributeKey<Boolean> MULTI_POSITION = new AttributeKey<Boolean>( "multiPosition", Boolean.class, ETile.class );
    public static final AttributeKey<Integer> GRID_X_POSITION = new AttributeKey<Integer>( "gridXPosition", Integer.class, ETile.class );
    public static final AttributeKey<Integer> GRID_Y_POSITION = new AttributeKey<Integer>( "gridYPosition", Integer.class, ETile.class );
    public static final AttributeKey<DynArray<Position>> GRID_POSITIONS = AttributeKey.createForDynArray( "gridPositions", ETile.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        SPRITE_ID,
        TINT_COLOR,
        BLEND_MODE,
        MULTI_POSITION,
        GRID_X_POSITION,
        GRID_Y_POSITION,
        GRID_POSITIONS,
    };
    
    private int spriteId;
    private final RGBColor tintColor = new RGBColor();
    private BlendMode blendMode;
    private boolean multiPosition;
    private final Position gridPosition = new Position();
    private final DynArray<Position> gridPositions = new DynArray<Position>( 10, 50 );

    public ETile() {
        super( TYPE_KEY );
        resetAttributes();
    }

    @Override
    public final void resetAttributes() {
        spriteId = -1;
        setTintColor( new RGBColor( 1, 1, 1, 1 ) );
        blendMode = BlendMode.NONE;
        setGridXPos( 0 );
        setGridYPos( 0 );
        gridPositions.clear();
        multiPosition = false;
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
        return 0;
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

    public final boolean isMultiPosition() {
        return multiPosition;
    }

    public final void setMultiPosition( boolean multiPosition ) {
        this.multiPosition = multiPosition;
        if ( !multiPosition ) {
            gridPositions.clear();
        }
    }

    public final int getGridXPos() {
        return gridPosition.x;
    }
    
    public final void setGridXPos( int pos ) {
        gridPosition.x = pos;
    }
    
    final Position getGridPosition() {
        return gridPosition;
    }
    
    public final int getGridYPos() {
        return gridPosition.y;
    }
    
    public final void setGridYPos( int pos ) {
        gridPosition.y = pos;
    }

    public final DynArray<Position> getGridPositions() {
        return gridPositions;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        spriteId = attributes.getValue( SPRITE_ID, spriteId );
        setTintColor( attributes.getValue( TINT_COLOR, tintColor ) );
        blendMode = attributes.getValue( BLEND_MODE, blendMode );
        multiPosition = attributes.getValue( MULTI_POSITION, multiPosition );
        gridPosition.x = attributes.getValue( GRID_X_POSITION, gridPosition.x );
        gridPosition.y = attributes.getValue( GRID_Y_POSITION, gridPosition.y );
        
        gridPositions.clear();
        if ( multiPosition ) {
            DynArray<Position> gp = attributes.getValue( GRID_POSITIONS );
            if ( gp != null ) {
                for ( Position pos : gp ) {
                    gridPositions.add( pos );
                }
            }
        }
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( SPRITE_ID, spriteId );
        attributes.put( TINT_COLOR, new RGBColor( tintColor ) );
        attributes.put( BLEND_MODE, blendMode );
        attributes.put( MULTI_POSITION, multiPosition );
        if ( multiPosition ) {
            attributes.put( GRID_POSITIONS, gridPositions );
        } else {
            attributes.put( GRID_X_POSITION, gridPosition.x );
            attributes.put( GRID_Y_POSITION, gridPosition.y );
        }
    }

}
