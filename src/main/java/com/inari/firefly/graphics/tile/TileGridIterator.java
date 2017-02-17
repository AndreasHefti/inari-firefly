package com.inari.firefly.graphics.tile;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.geom.Vector2f;
import com.inari.commons.lang.IntIterator;

public final class TileGridIterator implements IntIterator {
    
    private final Rectangle tmpClip = new Rectangle();
    private final Vector2f worldPosition = new Vector2f();
    private final Rectangle clip = new Rectangle();
    
    private int xorig;
    private int xsize;
    private int ysize;
    

    private int[][] grid;
    private int cellWidth;
    private int cellHeight;
    private float worldXPos;
    private float worldYPos;
    
    private boolean hasNext;
    
    public final void reset( final TileGrid tileGrid ) {
        clip.x = 0;
        clip.y = 0;
        clip.width = tileGrid.getWidth();
        clip.height = tileGrid.getHeight();
        init( tileGrid );
    }
    
    public final void reset( final Rectangle clip, final TileGrid tileGrid ) {
        this.clip.setFrom( mapWorldClipToTileGridClip( clip, tileGrid ) );
        init( tileGrid );
    }

    private void init( final TileGrid tileGrid ) {
        xorig = clip.x;
        xsize = clip.x + clip.width;
        ysize = clip.y + clip.height;

        grid = tileGrid.grid;
        cellWidth = tileGrid.getCellWidth();
        cellHeight = tileGrid.getCellHeight();
        worldXPos = tileGrid.getWorldXPos();
        worldYPos = tileGrid.getWorldYPos();
        
        findNext();
    }
    
    final Rectangle mapWorldClipToTileGridClip( final Rectangle worldClip, TileGrid tileGrid ) {
        tmpClip.x = (int) Math.floor( (double) ( worldClip.x - worldXPos ) / cellWidth );
        tmpClip.y = (int) Math.floor( (double) ( worldClip.y - worldYPos ) / cellHeight );
        int x2 = (int) Math.ceil( (double) ( worldClip.x - worldXPos + worldClip.width ) / cellWidth );
        int y2 = (int) Math.ceil( (double) ( worldClip.y - worldYPos + worldClip.height ) / cellHeight );
        tmpClip.width = x2 - tmpClip.x;
        tmpClip.height = y2 - tmpClip.y;
        return GeomUtils.intersection( tmpClip, tileGrid.normalisedWorldBounds );
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
                if ( grid[ clip.y ][ clip.x ] != TileGrid.NULL_VALUE ) {
                    hasNext = true;
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