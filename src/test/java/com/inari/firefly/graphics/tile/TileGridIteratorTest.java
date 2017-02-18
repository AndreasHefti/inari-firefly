package com.inari.firefly.graphics.tile;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.inari.commons.geom.Rectangle;
import com.inari.firefly.FFTest;
import com.inari.firefly.graphics.tile.TileGrid.TileGridIterator;

public class TileGridIteratorTest extends FFTest {
    
    private TileGrid grid1;
    
    @Before
    public void init() {
        super.init();
        grid1 = new TileGrid( 0 );
    }
    
    @After
    public void cleanup() {
        grid1.dispose();
        super.cleanup();
    }
    
    @Test
    public void testMapWorldClipToTileMapClip() {
        grid1.setWorldXPos( 0f );
        grid1.setWorldYPos( 0f );
        grid1.setCellWidth( 10 );
        grid1.setCellHeight( 10 );
        grid1.setWidth( 10 );
        grid1.setHeight( 10 );
        
        TileGridIterator iterator = grid1.getTileGridIterator();
        
        Rectangle worldClip1 = new Rectangle( 0, 0, 100, 100 );
        Rectangle tileGridClip1 = iterator.mapWorldClipToTileGridClip( worldClip1, grid1 );
        assertEquals( "[x=0,y=0,width=10,height=10]", tileGridClip1.toString() );
        assertEquals( "100", String.valueOf( tileGridClip1.area() ) );
        
        Rectangle worldClip2 = new Rectangle( 5, 5, 95, 95 );
        Rectangle tileGridClip2 = iterator.mapWorldClipToTileGridClip( worldClip2, grid1 );
        assertEquals( "[x=0,y=0,width=10,height=10]", tileGridClip2.toString() );
        assertEquals( "100", String.valueOf( tileGridClip2.area() ) );
        
        Rectangle worldClip3 = new Rectangle( 9, 9, 91, 91 );
        Rectangle tileGridClip3 = iterator.mapWorldClipToTileGridClip( worldClip3, grid1 );
        assertEquals( "[x=0,y=0,width=10,height=10]", tileGridClip3.toString() );
        assertEquals( "100", String.valueOf( tileGridClip3.area() ) );
        
        Rectangle worldClip4 = new Rectangle( 10, 10, 90, 90 );
        Rectangle tileGridClip4 = iterator.mapWorldClipToTileGridClip( worldClip4, grid1 );
        assertEquals( "[x=1,y=1,width=9,height=9]", tileGridClip4.toString() );
        assertEquals( "81", String.valueOf( tileGridClip4.area() ) );
        
        Rectangle worldClip5 = new Rectangle( -50, -50, 100, 100 );
        Rectangle tileGridClip5 = iterator.mapWorldClipToTileGridClip( worldClip5, grid1 );
        assertEquals( "[x=0,y=0,width=5,height=5]", tileGridClip5.toString() );
        assertEquals( "25", String.valueOf( tileGridClip5.area() ) );
        
        Rectangle worldClip6 = new Rectangle( 50, 50, 100, 100 );
        Rectangle tileGridClip6 = iterator.mapWorldClipToTileGridClip( worldClip6, grid1 );
        assertEquals( "[x=5,y=5,width=5,height=5]", tileGridClip6.toString() );
        assertEquals( "25", String.valueOf( tileGridClip6.area() ) );
        
        Rectangle worldClip7 = new Rectangle( 100, 0, 100, 100 );
        Rectangle tileGridClip7 = iterator.mapWorldClipToTileGridClip( worldClip7, grid1 );
        assertEquals( "[x=10,y=0,width=0,height=10]", tileGridClip7.toString() );
        assertEquals( "0", String.valueOf( tileGridClip7.area() ) );
    }

}
