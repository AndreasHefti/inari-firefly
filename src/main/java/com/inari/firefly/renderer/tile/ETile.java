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
package com.inari.firefly.renderer.tile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.Position;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public final class ETile extends EntityComponent {
    
    public static final EntityComponentTypeKey<ETile> TYPE_KEY = EntityComponentTypeKey.create( ETile.class );
    
    public static final AttributeKey<Boolean> MULTI_POSITION = new AttributeKey<Boolean>( "multiPosition", Boolean.class, ETile.class );
    public static final AttributeKey<Integer> GRID_X_POSITION = new AttributeKey<Integer>( "gridXPosition", Integer.class, ETile.class );
    public static final AttributeKey<Integer> GRID_Y_POSITION = new AttributeKey<Integer>( "gridYPosition", Integer.class, ETile.class );
    public static final AttributeKey<int[][]> GRID_POSITIONS = new AttributeKey<int[][]>( "gridPositions", int[][].class, ETile.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        MULTI_POSITION,
        GRID_X_POSITION,
        GRID_Y_POSITION,
        GRID_POSITIONS,
    };
    
    private boolean multiPosition;
    private final Position gridPosition = new Position();
    private final Set<Position> gridPositions = new HashSet<Position>();

    public ETile() {
        super( TYPE_KEY );
        resetAttributes();
    }

    @Override
    public final void resetAttributes() {
        setGridXPos( 0 );
        setGridYPos( 0 );
        gridPositions.clear();
        multiPosition = false;
    }
    
    @Override
    public final Class<ETile> componentType() {
        return ETile.class;
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

    public final Set<Position> getGridPositions() {
        return gridPositions;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        multiPosition = attributes.getValue( MULTI_POSITION, multiPosition );
        gridPosition.x = attributes.getValue( GRID_X_POSITION, gridPosition.x );
        gridPosition.y = attributes.getValue( GRID_Y_POSITION, gridPosition.y );
        if ( multiPosition ) {
            int[][] positions = attributes.getValue( GRID_POSITIONS );
            if ( positions != null ) {
                gridPositions.clear();
                for ( int j = 0; j < positions.length; j++ ) {
                    gridPositions.add( new Position( positions[ j ][ 0 ], positions[ j ][ 1 ] ) );
                }
            }
        } else {
            gridPositions.clear();
        }
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
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
    }

}
