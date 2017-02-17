package com.inari.firefly.graphics.tile;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.geom.Vector2f;
import com.inari.commons.lang.IntIterator;

public final class TileGridIterator implements IntIterator {
    
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
    
    private boolean hasNext = true;
    
    public final void reset( TileGrid tileGrid ) {
        clip.x = 0;
        clip.y = 0;
        clip.width = tileGrid.getWidth();
        clip.height = tileGrid.getHeight();
        init( tileGrid );
    }
    
    public final void reset( Rectangle clip, TileGrid tileGrid ) {
        this.clip.setFrom( clip );
        init( tileGrid );
    }

    private void init( TileGrid tileGrid ) {
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