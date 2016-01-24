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
import java.util.Set;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.geom.Vector2f;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.component.SystemComponent;

public final class TileGrid extends SystemComponent {
    
    public static final SystemComponentKey<TileGrid> TYPE_KEY = SystemComponentKey.create( TileGrid.class );
    
    public static final AttributeKey<Integer> RENDERER_ID = new AttributeKey<Integer>( "rendererId", Integer.class, TileGrid.class );
    public static final AttributeKey<Integer> VIEW_ID = new AttributeKey<Integer>( "viewId", Integer.class, TileGrid.class );
    public static final AttributeKey<Integer> LAYER_ID = new AttributeKey<Integer>( "layerId", Integer.class, TileGrid.class );
    public static final AttributeKey<Integer> WIDTH = new AttributeKey<Integer>( "width", Integer.class, TileGrid.class );
    public static final AttributeKey<Integer> HEIGHT = new AttributeKey<Integer>( "height", Integer.class, TileGrid.class );
    public static final AttributeKey<Integer> CELL_WIDTH = new AttributeKey<Integer>( "cellWidth", Integer.class, TileGrid.class );
    public static final AttributeKey<Integer> CELL_HEIGHT = new AttributeKey<Integer>( "cellHeight", Integer.class, TileGrid.class );
    public static final AttributeKey<Float> WORLD_XPOS = new AttributeKey<Float>( "worldXPos", Float.class, TileGrid.class );
    public static final AttributeKey<Float> WORLD_YPOS = new AttributeKey<Float>( "worldYPos", Float.class, TileGrid.class );
    public static final AttributeKey<Boolean> SPHERICAL = new AttributeKey<Boolean>( "spherical", Boolean.class, TileGrid.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        RENDERER_ID,
        VIEW_ID,
        LAYER_ID,
        WIDTH,
        HEIGHT,
        CELL_WIDTH,
        CELL_HEIGHT,
        WORLD_XPOS,
        WORLD_YPOS,
        SPHERICAL
    };
    
    public final static int NULL_VALUE = -1;

    private int rendererId;
    private int viewId;
    private int layerId;
    
    private int width;
    private int height;
    private int cellWidth;
    private int cellHeight;
    private float worldXPos;
    private float worldYPos;
    private boolean spherical;
    
    protected int[][] grid;
    
    private final Rectangle normalisedBounds = new Rectangle( 0, 0, 0, 0 );
    private final Rectangle tmpClip = new Rectangle();
    
    protected TileGrid( int id ) {
        super( id );
        rendererId = -1;
        viewId = 0;
        layerId = 0;
        width = 0;
        height = 0;
        cellWidth = 0;
        cellHeight = 0;
        worldXPos = 0;
        worldYPos = 0;
        spherical = false;
        createGrid();
    }
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

    public final int getRendererId() {
        return rendererId;
    }

    public final void setRendererId( int rendererId ) {
        this.rendererId = rendererId;
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

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( Arrays.asList( ATTRIBUTE_KEYS ) );
        return attributeKeys;
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        rendererId = attributes.getValue( RENDERER_ID, rendererId );
        viewId = attributes.getValue( VIEW_ID, viewId );
        layerId = attributes.getValue( LAYER_ID, layerId );
        width = attributes.getValue( WIDTH, width );
        height = attributes.getValue( HEIGHT, height );
        cellWidth = attributes.getValue( CELL_WIDTH, cellWidth );
        cellHeight = attributes.getValue( CELL_HEIGHT, cellHeight );
        worldXPos = attributes.getValue( WORLD_XPOS, worldXPos );
        worldYPos = attributes.getValue( WORLD_YPOS, worldYPos );
        spherical = attributes.getValue( SPHERICAL, spherical );
        createGrid();
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( RENDERER_ID, rendererId );
        attributes.put( VIEW_ID, viewId );
        attributes.put( LAYER_ID, layerId );
        attributes.put( WIDTH, width );
        attributes.put( HEIGHT, height );
        attributes.put( CELL_WIDTH, cellWidth );
        attributes.put( CELL_HEIGHT, cellHeight );
        attributes.put( WORLD_XPOS, worldXPos );
        attributes.put( WORLD_YPOS, worldXPos );
        attributes.put( SPHERICAL, spherical );
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
    
    public final void resetIfMatch( int entityId, int xpos, int ypos ) {
        if ( spherical ) {
            xpos = xpos % width;
            ypos = ypos % height;
        }
        if ( grid[ ypos ][ xpos ] == entityId ) {
            grid[ ypos ][ xpos ] = NULL_VALUE;
        }
    }
    
    public final int getNeighbour( int xpos, int ypos, Direction direction ) {
        return getNeighbour( xpos, ypos, direction, 1, 1 );
    }
    
    public final int getNeighbour( int xpos, int ypos, final Direction direction, final int xDistance, final int yDistance ) {
        switch ( direction.horizontal ) {
              case WEST: {
                  xpos = xpos - xDistance;
                  break;
              }
              case EAST: {
                  xpos = xpos + xDistance;
                  break;
              }
              default: {}
        }
        switch ( direction.vertical ) {
            case NORTH: {
                ypos = ypos + yDistance;
                break;
            }
            case SOUTH: {
                ypos = ypos +yDistance;
                break;
            }
            default: {}
        }
        
        return get( xpos, ypos );
    }
    
    public final TileIterator iterator() {
        return new TileIterator( new Rectangle( 0, 0, width, height ) );
    }
    
    public final TileIterator iterator( Rectangle worldClip ) {
        //return new TileIterator( new Rectangle( 0, 0, width, height ) );
        return new TileIterator( mapWorldClipToTileGridClip( worldClip ) );
    }

    final Rectangle mapWorldClipToTileGridClip( Rectangle worldClip ) {
        tmpClip.x = (int) Math.floor( (double) ( worldClip.x - worldXPos ) / cellWidth );
        tmpClip.y = (int) Math.floor( (double) ( worldClip.y - worldYPos ) / cellHeight );
        tmpClip.width = (int) Math.ceil( (double) worldClip.width / cellWidth ) + 1;
        tmpClip.height = (int) Math.ceil( (double) worldClip.height / cellHeight ) + 1;
        return GeomUtils.intersection( tmpClip, normalisedBounds );
    }

    private void createGrid() {
        int[][] old = grid;
        grid = new int[ height ][ width ];
        
        for ( int y = 0; y < height; y++ ) {
            for ( int x = 0; x < width; x++ ) {
                grid[ y ][ x ] = NULL_VALUE;
            }
        }
        
        if ( old != null && old.length > 0 && old[ 0 ].length > 0 ) {
            int lowerHeight = ( old.length < height )? old.length : height;
            int lowerWidth = ( old[ 0 ].length < width )? old[ 0 ].length : width;
            for ( int y = 0; y < lowerHeight; y++ ) {
                System.arraycopy( old[ y ], 0, grid[ y ], 0, lowerWidth );
            }
        }
        
        normalisedBounds.width = width;
        normalisedBounds.height = height;
    }
    
    public final class TileIterator implements IntIterator {
        
        private final int xorig;
        private final int xsize;
        private final int ysize;
        private final Rectangle clip;
        private final Vector2f worldPosition;
        private boolean hasNext = true;
        
        private TileIterator( Rectangle clip ) {
            xorig = clip.x;
            xsize = clip.x + clip.width;
            ysize = clip.y + clip.height;
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
            calcWorldPosition();
            clip.x++;
            findNext();
            return result;
        }

        public final float getWorldXPos() {
            return worldPosition.dx;
        }
        
        public final float getWorldYPos() {
            return worldPosition.dy;
        }

        private void findNext() {
            while ( clip.y < ysize ) {
                while( clip.x < xsize ) {
                    if ( grid[ clip.y ][ clip.x ] != NULL_VALUE ) {
                        return;
                    }
                    clip.x++;
                }
                clip.x = xorig;
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
