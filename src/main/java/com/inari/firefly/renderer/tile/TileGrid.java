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
package com.inari.firefly.renderer.tile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.geom.Vector2f;
import com.inari.commons.lang.IntIterator;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public final class TileGrid implements Component {
    
    public enum TileRenderMode {
        FULL_RENDERING,
        FAST_RENDERING
    }
    
    public static final AttributeKey<Integer> ATTRIBUTE_VIEW_ID = new AttributeKey<Integer>( "viewId", Integer.class, TileGrid.class );
    public static final AttributeKey<Integer> ATTRIBUTE_LAYER_ID = new AttributeKey<Integer>( "layerId", Integer.class, TileGrid.class );
    public static final AttributeKey<Integer> ATTRIBUTE_WIDTH = new AttributeKey<Integer>( "width", Integer.class, TileGrid.class );
    public static final AttributeKey<Integer> ATTRIBUTE_HEIGHT = new AttributeKey<Integer>( "height", Integer.class, TileGrid.class );
    public static final AttributeKey<Integer> ATTRIBUTE_CELL_WIDTH = new AttributeKey<Integer>( "cellWidth", Integer.class, TileGrid.class );
    public static final AttributeKey<Integer> ATTRIBUTE_CELL_HEIGHT = new AttributeKey<Integer>( "cellHeight", Integer.class, TileGrid.class );
    public static final AttributeKey<Float> ATTRIBUTE_WORLD_XPOS = new AttributeKey<Float>( "worldXPos", Float.class, TileGrid.class );
    public static final AttributeKey<Float> ATTRIBUTE_WORLD_YPOS = new AttributeKey<Float>( "worldYPos", Float.class, TileGrid.class );
    public static final AttributeKey<Boolean> ATTRIBUTE_SPHERICAL = new AttributeKey<Boolean>( "spherical", Boolean.class, TileGrid.class );
    public static final AttributeKey<TileRenderMode> ATTRIBUTE_RENDER_MODE = new AttributeKey<TileRenderMode>( "renderMode", TileRenderMode.class, TileGrid.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        ATTRIBUTE_VIEW_ID,
        ATTRIBUTE_LAYER_ID,
        ATTRIBUTE_WIDTH,
        ATTRIBUTE_HEIGHT,
        ATTRIBUTE_CELL_WIDTH,
        ATTRIBUTE_CELL_HEIGHT,
        ATTRIBUTE_WORLD_XPOS,
        ATTRIBUTE_WORLD_YPOS,
        ATTRIBUTE_SPHERICAL,
        ATTRIBUTE_RENDER_MODE
    };
    
    public final static int NULL_VALUE = -1;

    private int viewId;
    private int layerId;
    
    private int width;
    private int height;
    private int cellWidth;
    private int cellHeight;
    private float worldXPos;
    private float worldYPos;
    private boolean spherical;
    private TileRenderMode renderMode;
    
    protected int[][] grid;
    
    TileGrid() {
        viewId = -1;
        layerId = -1;
        width = 0;
        height = 0;
        cellWidth = 0;
        cellHeight = 0;
        worldXPos = 0;
        worldYPos = 0;
        spherical = false;
        renderMode = TileRenderMode.FULL_RENDERING;
        createGrid();
    }
    
    @Deprecated
    TileGrid( int viewId, int layerId, int width, int height ) {
        this.viewId = viewId;
        this.layerId = layerId;
        this.width = width;
        this.height = height;
        this.worldXPos = 0;
        this.worldYPos = 0;
        this.spherical = false;
        renderMode = TileRenderMode.FULL_RENDERING;
        createGrid();
    }
    
    @Deprecated
    TileGrid( int viewId, int layerId, int width, int height, int worldXpos, int worldYPos, boolean spherical ) {
        this.viewId = viewId;
        this.layerId = layerId;
        this.width = width;
        this.height = height;
        this.worldXPos = worldXpos;
        this.worldYPos = worldYPos;
        this.spherical = spherical;
        renderMode = TileRenderMode.FULL_RENDERING;
        createGrid();
    }

    @Override
    public final Class<TileGrid> getComponentType() {
        return TileGrid.class;
    }

    public final int getViewId() {
        return viewId;
    }

    public final void setViewId( int viewId ) {
        this.viewId = viewId;
    }

    public final int getLayerId() {
        return layerId;
    }

    public final void setLayerId( int layerId ) {
        this.layerId = layerId;
    }

    public final int getWidth() {
        return width;
    }

    public final void setWidth( int width ) {
        this.width = width;
        createGrid();
    }

    public final int getHeight() {
        return height;
    }

    public final void setHeight( int height ) {
        this.height = height;
        createGrid();
    }

    public final int getCellWidth() {
        return cellWidth;
    }

    public final void setCellWidth( int cellWidth ) {
        this.cellWidth = cellWidth;
    }

    public final int getCellHeight() {
        return cellHeight;
    }

    public final void setCellHeight( int cellHeight ) {
        this.cellHeight = cellHeight;
    }

    public final float getWorldXPos() {
        return worldXPos;
    }

    public final void setWorldXPos( Float worldXPos ) {
        this.worldXPos = worldXPos;
    }

    public final Float getWorldYPos() {
        return worldYPos;
    }

    public final void setWorldYPos( Float worldYPos ) {
        this.worldYPos = worldYPos;
    }

    public final boolean isSpherical() {
        return spherical;
    }

    public final void setSpherical( boolean spherical ) {
        this.spherical = spherical;
    }

    public final TileRenderMode getRenderMode() {
        return renderMode;
    }

    public final void setRenderMode( TileRenderMode renderMode ) {
        this.renderMode = renderMode;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        viewId = attributes.getValue( ATTRIBUTE_VIEW_ID, viewId );
        layerId = attributes.getValue( ATTRIBUTE_LAYER_ID, layerId );
        width = attributes.getValue( ATTRIBUTE_WIDTH, width );
        height = attributes.getValue( ATTRIBUTE_HEIGHT, height );
        cellWidth = attributes.getValue( ATTRIBUTE_CELL_WIDTH, cellWidth );
        cellHeight = attributes.getValue( ATTRIBUTE_CELL_HEIGHT, cellHeight );
        worldXPos = attributes.getValue( ATTRIBUTE_WORLD_XPOS, worldXPos );
        worldYPos = attributes.getValue( ATTRIBUTE_WORLD_YPOS, worldYPos );
        spherical = attributes.getValue( ATTRIBUTE_SPHERICAL, spherical );
        renderMode = attributes.getValue( ATTRIBUTE_RENDER_MODE, renderMode );
        createGrid();
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( ATTRIBUTE_VIEW_ID, viewId );
        attributes.put( ATTRIBUTE_LAYER_ID, layerId );
        attributes.put( ATTRIBUTE_WIDTH, width );
        attributes.put( ATTRIBUTE_HEIGHT, height );
        attributes.put( ATTRIBUTE_CELL_WIDTH, cellWidth );
        attributes.put( ATTRIBUTE_CELL_HEIGHT, cellHeight );
        attributes.put( ATTRIBUTE_WORLD_XPOS, worldXPos );
        attributes.put( ATTRIBUTE_WORLD_YPOS, worldXPos );
        attributes.put( ATTRIBUTE_SPHERICAL, spherical );
        attributes.put( ATTRIBUTE_RENDER_MODE, renderMode );
    }
    
    public final int get( int xpos, int ypos ) {
        if ( spherical ) {
            xpos = xpos % width;
            ypos = ypos % height;
        }
        return grid[ ypos ][ xpos ];
    }

    public final void set( int entityId, int xpos, int ypos ) {
        if ( spherical ) {
            xpos = xpos % width;
            ypos = ypos % height;
        }
        grid[ ypos ][ xpos ] = entityId;
    }
    
    public final int reset( int xpos, int ypos ) {
        if ( spherical ) {
            xpos = xpos % width;
            ypos = ypos % height;
        }
        int old = grid[ ypos ][ xpos ];
        grid[ ypos ][ xpos ] = NULL_VALUE;
        return old;
    }
    
    public final int getNeighbour( int xpos, int ypos, Direction direction) {
        return getNeighbour( xpos, ypos, direction, 1, 1 );
    }
    
    public final int getNeighbour( int xpos, int ypos, Direction direction, int xDistance, int yDistance ) {
        switch ( direction.xDir ) {
              case LEFT: {
                  xpos--;
                  break;
              }
              case RIGHT: {
                  xpos++;
                  break;
              }
              default: {}
        }
        switch ( direction.yDir ) {
            case UP: {
                ypos--;
                break;
            }
            case DOWN: {
                ypos++;
                break;
            }
            default: {}
        }
        
        return get( xpos, ypos );
    }
    
    public final TileGridIterator iterator() {
        return new TileGridIterator( new Rectangle( -1, 0, width, height ) );
    }
    
    public final TileGridIterator iterator( Rectangle worldClip ) {
        return new TileGridIterator( new Rectangle( -1, 0, width, height ) );
    }

    private void createGrid() {
        int[][] old = grid;
        grid = new int[ height ][ width ];
        
        for ( int y = 0; y < height; y++ ) {
            for ( int x = 0; x < width; x++ ) {
                grid[ y ][ x ] = NULL_VALUE;
            }
        }
        
        if ( old != null ) {
            int lowerHeight = ( old.length < height )? old.length : height;
            int lowerWidth = ( old[ 0 ].length < width )? old[ 0 ].length : width;
            for ( int y = 0; y < lowerHeight; y++ ) {
                System.arraycopy( old[ y ], 0, grid[ y ], 0, lowerWidth );
            }
        }
    }
    
    public final class TileGridIterator implements IntIterator {
        
        private final Rectangle clip;
        private final Vector2f worldPosition;
        private boolean hasNext = true;
        
        private TileGridIterator( Rectangle clip ) {
            this.clip = clip;
            worldPosition = new Vector2f();
            findNext();
        }

        @Override
        public final boolean hasNext() {
            return hasNext;
        }

        @Override
        public final int next() {
            int result = grid[ clip.y ][ clip.x ];
            findNext();
            return result;
        }

        public final Vector2f getWorldPosition() {
            return worldPosition;
        }

        private void findNext() {
            clip.x++;
            while ( clip.y < clip.height ) {
                while( clip.x < clip.width ) {
                    if ( grid[ clip.y ][ clip.x ] != NULL_VALUE ) {
                        calcWorldPosition();
                        return;
                    }
                    clip.x++;
                }
                clip.x = 0;
                clip.y++;
            }
            hasNext = false;
        }
        
        private void calcWorldPosition() {
            worldPosition.dx = worldXPos + ( clip.x * cellWidth );
            worldPosition.dy = worldYPos + ( clip.y * cellHeight );
        }
    }

}
