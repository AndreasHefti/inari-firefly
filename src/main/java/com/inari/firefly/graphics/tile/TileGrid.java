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

import java.util.ArrayDeque;
import java.util.Set;

import com.inari.commons.GeomUtils;
import com.inari.commons.JavaUtils;
import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.geom.Vector2f;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.graphics.rendering.RenderingChain.RendererKey;
import com.inari.firefly.graphics.view.Layer;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.system.component.SystemComponent;

public final class TileGrid extends SystemComponent {
    
    public static final SystemComponentKey<TileGrid> TYPE_KEY = SystemComponentKey.create( TileGrid.class );
    
    public static final AttributeKey<RendererKey> RENDERER_KEY = new AttributeKey<>( "rendererKey", RendererKey.class, TileGrid.class );
    public static final AttributeKey<String> VIEW_NAME = AttributeKey.createString( "viewName", TileGrid.class );
    public static final AttributeKey<Integer> VIEW_ID = AttributeKey.createInt( "viewId", TileGrid.class );
    public static final AttributeKey<String> LAYER_NAME = AttributeKey.createString( "layerName", TileGrid.class );
    public static final AttributeKey<Integer> LAYER_ID = AttributeKey.createInt( "layerId", TileGrid.class );
    public static final AttributeKey<Integer> WIDTH = AttributeKey.createInt( "width", TileGrid.class );
    public static final AttributeKey<Integer> HEIGHT = AttributeKey.createInt( "height", TileGrid.class );
    public static final AttributeKey<Integer> CELL_WIDTH = AttributeKey.createInt( "cellWidth", TileGrid.class );
    public static final AttributeKey<Integer> CELL_HEIGHT = AttributeKey.createInt( "cellHeight", TileGrid.class );
    public static final AttributeKey<Float> WORLD_XPOS = AttributeKey.createFloat( "worldXPos", TileGrid.class );
    public static final AttributeKey<Float> WORLD_YPOS = AttributeKey.createFloat( "worldYPos", TileGrid.class );
    public static final AttributeKey<Boolean> SPHERICAL = AttributeKey.createBoolean( "spherical", TileGrid.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        RENDERER_KEY,
        VIEW_ID,
        LAYER_ID,
        WIDTH,
        HEIGHT,
        CELL_WIDTH,
        CELL_HEIGHT,
        WORLD_XPOS,
        WORLD_YPOS,
        SPHERICAL
    );
    
    public final static int NULL_VALUE = -1;

    private int viewId;
    private int layerId;
    private RendererKey rendererKey;
    
    private int width;
    private int height;
    int cellWidth;
    int cellHeight;
    float worldXPos;
    float worldYPos;
    private boolean spherical;
    
    int[][] grid;
    
    final Rectangle normalisedWorldBounds = new Rectangle( 0, 0, 0, 0 );

    protected TileGrid( int id ) {
        super( id );
        viewId = 0;
        layerId = 0;
        width = 0;
        height = 0;
        cellWidth = 0;
        cellHeight = 0;
        worldXPos = 0;
        worldYPos = 0;
        spherical = false;
        rendererKey = null;
        createGrid();
    }
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
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

    public final RendererKey getRendererKey() {
        return rendererKey;
    }

    public final void setRendererKey( RendererKey rendererKey ) {
        this.rendererKey = rendererKey;
    }
    
    public final boolean rendererMatch( RendererKey chainKey ) {
        return rendererKey == chainKey ;
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

    public final void setWorldXPos( float worldXPos ) {
        this.worldXPos = worldXPos;
    }

    public final float getWorldYPos() {
        return worldYPos;
    }

    public final void setWorldYPos( float worldYPos ) {
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
        return JavaUtils.unmodifiableSet( super.attributeKeys(), ATTRIBUTE_KEYS );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        viewId = attributes.getIdForName( VIEW_NAME, VIEW_ID, View.TYPE_KEY, viewId );
        layerId = attributes.getIdForName( LAYER_NAME, LAYER_ID, Layer.TYPE_KEY, layerId );
        rendererKey = attributes.getValue( RENDERER_KEY, rendererKey );
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

        attributes.put( VIEW_ID, viewId );
        attributes.put( LAYER_ID, layerId );
        attributes.put( RENDERER_KEY, rendererKey );
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
        
        if ( xpos < 0 || xpos >= width || ypos < 0 || ypos >= height ) {
            return -1;
        }
        
        return grid[ ypos ][ xpos ];
    }
    
    public final int getTileAt( final Position worldPos ) {
        int x = (int) Math.floor( (double) ( worldPos.x - worldXPos ) / cellWidth );
        int y = (int) Math.floor( (double) ( worldPos.y - worldYPos ) / cellHeight );

        return get( x, y );
    }
    
    public final int getTileAt( float xpos, float ypos ) {
        int x = (int) Math.floor( (double) ( xpos - worldXPos ) / cellWidth );
        int y = (int) Math.floor( (double) ( ypos - worldYPos ) / cellHeight );

        return get( x, y );
    }
    
    public final void set( int entityId, Position position ) {
        set( entityId, position.x, position.y );
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
    
    public final void resetIfMatch( int entityId, Position position ) {
        resetIfMatch( entityId, position.x, position.y );
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
    
    public final TileGridIterator getTileGridIterator() {
        return TileGridIterator.getInstance( this );
    }
    
    public final TileGridIterator getTileGridIterator( Rectangle worldClip ) {
        return TileGridIterator.getInstance( worldClip, this );
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
        
        normalisedWorldBounds.width = width;
        normalisedWorldBounds.height = height;
    }
    
    
    public final static class TileGridIterator implements IntIterator {
        
        private static final ArrayDeque<TileGridIterator> POOL = new ArrayDeque<TileGridIterator>( 5 );
    
        private final Rectangle tmpClip = new Rectangle();
        private final Vector2f worldPosition = new Vector2f();
        private final Rectangle clip = new Rectangle();
        
        private int xorig;
        private int xsize;
        private int ysize;
        private TileGrid tileGrid;
    
        private boolean hasNext;
        
        private TileGridIterator() {}

        @Override
        public final boolean hasNext() {
            return hasNext;
        }
    
        @Override
        public final int next() {
            int result = tileGrid.grid[ clip.y ][ clip.x ];
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
        
        private final void reset( final TileGrid tileGrid ) {
            clip.x = 0;
            clip.y = 0;
            clip.width = tileGrid.getWidth();
            clip.height = tileGrid.getHeight();
            init( tileGrid );
        }
        
        private final void reset( final Rectangle clip, final TileGrid tileGrid ) {
            mapWorldClipToTileGridClip( clip, tileGrid, this.clip );
            init( tileGrid );
        }
    
        private void init( final TileGrid tileGrid ) {
            xorig = clip.x;
            xsize = clip.x + clip.width;
            ysize = clip.y + clip.height;
    
            this.tileGrid = tileGrid;
            
            findNext();
        }
        
        final void mapWorldClipToTileGridClip( final Rectangle worldClip, TileGrid tileGrid, Rectangle result ) {
            tmpClip.x = (int) Math.floor( (double) ( worldClip.x - tileGrid.worldXPos ) / tileGrid.cellWidth );
            tmpClip.y = (int) Math.floor( (double) ( worldClip.y - tileGrid.worldYPos ) / tileGrid.cellHeight );
            int x2 = (int) Math.ceil( (double) ( worldClip.x - tileGrid.worldXPos + worldClip.width ) / tileGrid.cellWidth );
            int y2 = (int) Math.ceil( (double) ( worldClip.y - tileGrid.worldYPos + worldClip.height ) / tileGrid.cellHeight );
            tmpClip.width = x2 - tmpClip.x;
            tmpClip.height = y2 - tmpClip.y;
            GeomUtils.intersection( tmpClip, tileGrid.normalisedWorldBounds, result );
        }
    
        private void findNext() {
            while ( clip.y < ysize ) {
                while( clip.x < xsize ) {
                    if ( tileGrid.grid[ clip.y ][ clip.x ] != TileGrid.NULL_VALUE ) {
                        hasNext = true;
                        return;
                    }
                    clip.x++;
                }
                clip.x = xorig;
                clip.y++;
            }
            
            dispose();
        }
        
        private void dispose() {
            hasNext = false;
            tileGrid = null;
            xorig = -1;
            xsize = -1;
            ysize = -1;
            POOL.add( this );
        }

        private void calcWorldPosition() {
            worldPosition.dx = tileGrid.worldXPos + ( clip.x * tileGrid.cellWidth );
            worldPosition.dy = tileGrid.worldYPos + ( clip.y * tileGrid.cellHeight );
        }
        
        static final TileGridIterator getInstance( final Rectangle clip, final TileGrid tileGrid ) {
            TileGridIterator instance = getInstance();
            
            instance.reset( clip, tileGrid );
            return instance;
        }

        static final TileGridIterator getInstance( final TileGrid tileGrid ) {
            TileGridIterator instance = getInstance();
            
            instance.reset( tileGrid );
            return instance;
        }
        
        static final private TileGridIterator getInstance() {
            TileGridIterator instance;
            if ( POOL.isEmpty() ) {
                instance = new TileGridIterator();
            } else {
                instance = POOL.pollLast();
            }
            return instance;
        }
    }


    

}
