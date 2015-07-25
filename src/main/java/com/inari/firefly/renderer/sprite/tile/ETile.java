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
package com.inari.firefly.renderer.sprite.tile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.Position;
import com.inari.commons.geom.Vector2i;
import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.renderer.sprite.SpriteRenderable;

public final class ETile extends EntityComponent implements SpriteRenderable {
    
    public static final int COMPONENT_TYPE = Indexer.getIndexForType( ETile.class, EntityComponent.class );
    
    public static final AttributeKey<Integer> SPRITE_ID = new AttributeKey<Integer>( "spriteId", Integer.class, ETile.class );
    public static final AttributeKey<Integer> VIEW_ID = new AttributeKey<Integer>( "viewId", Integer.class, ETile.class );
    public static final AttributeKey<Integer> LAYER_ID = new AttributeKey<Integer>( "layerId", Integer.class, ETile.class );
    public static final AttributeKey<Boolean> MULTI_POSITION = new AttributeKey<Boolean>( "multiPosition", Boolean.class, ETile.class );
    public static final AttributeKey<Integer> GRID_X_POSITION = new AttributeKey<Integer>( "gridXPosition", Integer.class, ETile.class );
    public static final AttributeKey<Integer> GRID_Y_POSITION = new AttributeKey<Integer>( "gridYPosition", Integer.class, ETile.class );
    public static final AttributeKey<int[][]> GRID_POSITIONS = new AttributeKey<int[][]>( "gridPositions", int[][].class, ETile.class );
    public static final AttributeKey<Integer> X_OFFSET = new AttributeKey<Integer>( "xOffset", Integer.class, ETile.class );
    public static final AttributeKey<Integer> Y_OFFSET = new AttributeKey<Integer>( "yOffset", Integer.class, ETile.class );
    public static final AttributeKey<RGBColor> TINT_COLOR = new AttributeKey<RGBColor>( "tintColor", RGBColor.class, ETile.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        SPRITE_ID, 
        VIEW_ID,
        LAYER_ID,
        MULTI_POSITION,
        GRID_X_POSITION,
        GRID_Y_POSITION,
        GRID_POSITIONS,
        X_OFFSET,
        Y_OFFSET,
        TINT_COLOR
    };
    
    private int spriteId;
    private int viewId;
    private int layerId;
    
    private boolean multiPosition = false;
    private final Position gridPosition;
    private final DynArray<Position> gridPositions;

    private final Vector2i offset;
    private final RGBColor tintColor;

    protected ETile() {
        spriteId = -1;
        viewId = 0;
        layerId = 0;
        gridPosition = new Position();
        gridPositions = new DynArray<Position>();
        offset = new Vector2i( 0, 0 );
        tintColor = new RGBColor( 1, 1, 1, 0 );
    }
    
    @Override
    public final Class<ETile> getComponentType() {
        return ETile.class;
    }

    @Override
    public final int getSpriteId() {
        return spriteId;
    }

    public final void setSpriteId( int spriteId ) {
        this.spriteId = spriteId;
    }

    @Override
    public final int getViewId() {
        return viewId;
    }

    public final void setViewId( int viewId ) {
        this.viewId = viewId;
    }

    @Override
    public final int getLayerId() {
        return layerId;
    }

    public final void setLayerId( int layerId ) {
        this.layerId = layerId;
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

    public final Position getGridPosition() {
        return gridPosition;
    }

    public final DynArray<Position> getGridPositions() {
        return gridPositions;
    }

    public final boolean hasOffset() {
        return ( offset.dx != 0 || offset.dy != 0 );
    }

    public final Vector2i getOffset() {
        return offset;
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
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        spriteId = attributes.getValue( SPRITE_ID, spriteId );
        viewId = attributes.getValue( VIEW_ID, viewId );
        layerId = attributes.getValue( LAYER_ID, layerId );
        multiPosition = attributes.getValue( MULTI_POSITION, multiPosition );
        gridPosition.x = attributes.getValue( GRID_X_POSITION, gridPosition.x );
        gridPosition.y = attributes.getValue( GRID_Y_POSITION, gridPosition.y );
        if ( multiPosition ) {
            int[][] positions = attributes.getValue( GRID_POSITIONS );
            if ( positions != null ) {
                gridPositions.clear();
                for ( int j = 0; j < positions.length; j++ ) {
                    gridPositions.set( j, new Position( positions[ j ][ 0 ], positions[ j ][ 1 ] ) );
                }
            }
        } else {
            gridPositions.clear();
        }
        offset.dx = attributes.getValue( X_OFFSET, offset.dx );
        offset.dy = attributes.getValue( Y_OFFSET, offset.dy );
        setTintColor( attributes.getValue( TINT_COLOR, tintColor ) );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( SPRITE_ID, spriteId );
        attributes.put( VIEW_ID, viewId );
        attributes.put( LAYER_ID, layerId );
        attributes.put( MULTI_POSITION, multiPosition );
        if ( multiPosition ) {
            int[][] result = new int[ gridPositions.size() ][ 2 ];
            int index = 0;
            for ( Position position : gridPositions ) {
                result[ index ][ 0 ] = position.x;
                result[ index ][ 1 ] = position.y;
                index++;
            }
            attributes.put( GRID_POSITIONS, result );
        } else {
            attributes.put( GRID_X_POSITION, gridPosition.x );
            attributes.put( GRID_Y_POSITION, gridPosition.y );
        }
        attributes.put( X_OFFSET, offset.dx );
        attributes.put( Y_OFFSET, offset.dy );
        attributes.put( TINT_COLOR, tintColor );
    }

}
