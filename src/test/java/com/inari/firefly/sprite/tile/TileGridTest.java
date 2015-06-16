package com.inari.firefly.sprite.tile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.inari.firefly.sprite.tile.TileGrid.TileGridIterator;

public class TileGridTest {

    @Test
    public void testCreation() {
        TileGrid grid1 = new TileGrid();
        TileGrid grid2 = new TileGrid( 1, 1, 10, 10 );
        TileGrid grid3 = new TileGrid( 1, 1, 10, 10, 10, 10, true );
        
        assertEquals( 
            "TileGrid [viewId=-1, layerId=-1, width=0, height=0, cellWidth=0, cellHeight=0, worldXPos=0.0, worldYPos=0.0, spherical=false, grid=[]]", 
            grid1.toString() 
        );
        assertEquals( 
            "TileGrid [viewId=1, layerId=1, width=10, height=10, cellWidth=0, cellHeight=0, worldXPos=0.0, worldYPos=0.0, spherical=false, " +
            "grid=" +
            "[[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]]]", 
            grid2.toString() 
        );
        assertEquals( 
            "TileGrid [viewId=1, layerId=1, width=10, height=10, cellWidth=0, cellHeight=0, worldXPos=10.0, worldYPos=10.0, spherical=true, " +
            "grid=" +
            "[[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]]]", 
            grid3.toString() 
        );
    }
    
    @Test
    public void testSetReset() {
        TileGrid grid = new TileGrid( 1, 1, 3, 3 );
        
        assertEquals( 
            "TileGrid [viewId=1, layerId=1, width=3, height=3, cellWidth=0, cellHeight=0, worldXPos=0.0, worldYPos=0.0, spherical=false, " +
            "grid=" +
            "[[-1, -1, -1]" +
            "[-1, -1, -1]" +
            "[-1, -1, -1]]]", 
            grid.toString() 
        );
        
        grid.set( 50, 0, 0 );
        grid.set( 50, 1, 1 );
        grid.set( 50, 2, 2 );
        
        assertEquals( 
            "TileGrid [viewId=1, layerId=1, width=3, height=3, cellWidth=0, cellHeight=0, worldXPos=0.0, worldYPos=0.0, spherical=false, " +
            "grid=" +
            "[[50, -1, -1]" +
            "[-1, 50, -1]" +
            "[-1, -1, 50]]]", 
            grid.toString() 
        );
        
        grid.set( 150, 2, 0 );
        grid.set( 150, 1, 1 );
        grid.set( 150, 0, 2 );
        
        assertEquals( 
            "TileGrid [viewId=1, layerId=1, width=3, height=3, cellWidth=0, cellHeight=0, worldXPos=0.0, worldYPos=0.0, spherical=false, " +
            "grid=" +
            "[[50, -1, 150]" +
            "[-1, 150, -1]" +
            "[150, -1, 50]]]", 
            grid.toString() 
        );
        
        grid.reset( 0, 0 );
        grid.reset( 1, 0 );
        grid.reset( 2, 0 );
        
        assertEquals( 
            "TileGrid [viewId=1, layerId=1, width=3, height=3, cellWidth=0, cellHeight=0, worldXPos=0.0, worldYPos=0.0, spherical=false, " +
            "grid=" +
            "[[-1, -1, -1]" +
            "[-1, 150, -1]" +
            "[150, -1, 50]]]", 
            grid.toString() 
        );
        
        try {
            grid.set( 10, 3, 2 );
            fail( "ArrayIndexOutOfBoundsException expected here" );
        } catch ( ArrayIndexOutOfBoundsException e ) {
            assertEquals( "3", e.getMessage() );
        }
        try {
            grid.set( 10, 2, 3 );
            fail( "ArrayIndexOutOfBoundsException expected here" );
        } catch ( ArrayIndexOutOfBoundsException e ) {
            assertEquals( "3", e.getMessage() );
        }
        try {
            grid.set( 10, -3, 2 );
            fail( "ArrayIndexOutOfBoundsException expected here" );
        } catch ( ArrayIndexOutOfBoundsException e ) {
            assertEquals( "-3", e.getMessage() );
        }
    }
    
    @Test
    public void testSphericalSetReset() {
        TileGrid grid = new TileGrid( 1, 1, 3, 3 );
        grid.setSpherical( true );
        
        assertEquals( 
            "TileGrid [viewId=1, layerId=1, width=3, height=3, cellWidth=0, cellHeight=0, worldXPos=0.0, worldYPos=0.0, spherical=true, " +
            "grid=" +
            "[[-1, -1, -1]" +
            "[-1, -1, -1]" +
            "[-1, -1, -1]]]", 
            grid.toString() 
        );
        
        grid.set( 100, 4, 1 );
        
        assertEquals( 
            "TileGrid [viewId=1, layerId=1, width=3, height=3, cellWidth=0, cellHeight=0, worldXPos=0.0, worldYPos=0.0, spherical=true, " +
            "grid=" +
            "[[-1, -1, -1]" +
            "[-1, 100, -1]" +
            "[-1, -1, -1]]]", 
            grid.toString() 
        );
        
        grid.set( 100, 1, 5 );
        
        assertEquals( 
            "TileGrid [viewId=1, layerId=1, width=3, height=3, cellWidth=0, cellHeight=0, worldXPos=0.0, worldYPos=0.0, spherical=true, " +
            "grid=" +
            "[[-1, -1, -1]" +
            "[-1, 100, -1]" +
            "[-1, 100, -1]]]", 
            grid.toString() 
        );
        
        grid.set( 100, 8, 8 );
        
        assertEquals( 
            "TileGrid [viewId=1, layerId=1, width=3, height=3, cellWidth=0, cellHeight=0, worldXPos=0.0, worldYPos=0.0, spherical=true, " +
            "grid=" +
            "[[-1, -1, -1]" +
            "[-1, 100, -1]" +
            "[-1, 100, 100]]]", 
            grid.toString() 
        );
        
        grid.reset( 8, 8 );
        
        assertEquals( 
            "TileGrid [viewId=1, layerId=1, width=3, height=3, cellWidth=0, cellHeight=0, worldXPos=0.0, worldYPos=0.0, spherical=true, " +
            "grid=" +
            "[[-1, -1, -1]" +
            "[-1, 100, -1]" +
            "[-1, 100, -1]]]", 
            grid.toString() 
        );
    }
    
    @Test
    public void testResize() {
        TileGrid grid = new TileGrid( 1, 1, 3, 3 );
        grid.set( 100, 0, 0 );
        grid.set( 100, 1, 1 );
        grid.set( 100, 2, 2 );
        assertEquals( 
            "TileGrid [viewId=1, layerId=1, width=3, height=3, cellWidth=0, cellHeight=0, worldXPos=0.0, worldYPos=0.0, spherical=false, " +
            "grid=" +
            "[[100, -1, -1]" +
            "[-1, 100, -1]" +
            "[-1, -1, 100]]]", 
            grid.toString() 
        );
        
        grid.setWidth( 5 );
        
        assertEquals( 
            "TileGrid [viewId=1, layerId=1, width=5, height=3, cellWidth=0, cellHeight=0, worldXPos=0.0, worldYPos=0.0, spherical=false, grid=" +
            "[[100, -1, -1, -1, -1]" +
            "[-1, 100, -1, -1, -1]" +
            "[-1, -1, 100, -1, -1]]]", 
            grid.toString() 
        );
        
        grid.setHeight( 5 );
        
        assertEquals( 
            "TileGrid [viewId=1, layerId=1, width=5, height=5, cellWidth=0, cellHeight=0, worldXPos=0.0, worldYPos=0.0, spherical=false, grid=" +
            "[[100, -1, -1, -1, -1]" +
            "[-1, 100, -1, -1, -1]" +
            "[-1, -1, 100, -1, -1]" +
            "[-1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1]]]", 
            grid.toString() 
        );
        
        grid.set( 100, 3, 3 );
        grid.set( 100, 4, 4 );
        
        assertEquals( 
            "TileGrid [viewId=1, layerId=1, width=5, height=5, cellWidth=0, cellHeight=0, worldXPos=0.0, worldYPos=0.0, spherical=false, grid=" +
            "[[100, -1, -1, -1, -1]" +
            "[-1, 100, -1, -1, -1]" +
            "[-1, -1, 100, -1, -1]" +
            "[-1, -1, -1, 100, -1]" +
            "[-1, -1, -1, -1, 100]]]", 
            grid.toString() 
        );
        
        grid.setWidth( 3 );
        grid.setHeight( 3 );
        
        assertEquals( 
            "TileGrid [viewId=1, layerId=1, width=3, height=3, cellWidth=0, cellHeight=0, worldXPos=0.0, worldYPos=0.0, spherical=false, " +
            "grid=" +
            "[[100, -1, -1]" +
            "[-1, 100, -1]" +
            "[-1, -1, 100]]]", 
            grid.toString() 
        );
    }
    
    @Test
    public void testIterator() {
        TileGrid grid = new TileGrid( 1, 1, 3, 3 );
        grid.set( 100, 0, 0 );
        grid.set( 100, 1, 1 );
        grid.set( 100, 2, 2 );
        assertEquals( 
            "TileGrid [viewId=1, layerId=1, width=3, height=3, cellWidth=0, cellHeight=0, worldXPos=0.0, worldYPos=0.0, spherical=false, " +
            "grid=" +
            "[[100, -1, -1]" +
            "[-1, 100, -1]" +
            "[-1, -1, 100]]]", 
            grid.toString() 
        );
        
        TileGridIterator iterator = grid.iterator();
        assertNotNull( iterator );
        assertTrue( "should have next", iterator.hasNext() );
        assertEquals( "expected next", 100, iterator.next() );
        assertEquals( "expected next", 100, iterator.next() );
        assertEquals( "expected next", 100, iterator.next() );
        assertFalse( "expected no next", iterator.hasNext() );
    }

}
