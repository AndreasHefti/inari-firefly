package com.inari.firefly.graphics.tile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.inari.commons.StringUtils;
import com.inari.commons.geom.Rectangle;
import com.inari.firefly.FFTest;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.ComponentAttributeMap;
import com.inari.firefly.graphics.tile.TileGrid.TileIterator;
public class TileGridTest extends FFTest {
    
    private TileGrid grid1;
    private TileGrid grid2;
    private TileGrid grid3;
    
    @Before
    public void init() {
        super.init();
        grid1 = new TileGrid( 0 );
        grid2 = new TileGrid( 1 );
        grid3 = new TileGrid( 2 );
    }
    
    @After
    public void cleanup() {
        grid1.dispose();
        grid2.dispose();
        grid3.dispose();
        super.cleanup();
    }
    
    
    @Test
    public void testCreation() {
        grid2.setViewId( 123 );
        grid2.setLayerId( 1 );
        grid2.setWidth( 10 );
        grid2.setHeight( 10 );
        
        grid3.setViewId( 123 );
        grid3.setLayerId( 1 );
        grid3.setWidth( 10 );
        grid3.setHeight( 10 );
        grid3.setWorldXPos( 10f );
        grid3.setWorldYPos( 10f );
        grid3.setSpherical( true );
        
        AttributeMap attrs = new ComponentAttributeMap( null );
        

        grid1.toAttributes( attrs );
        assertEquals(
            "name:String=null, " +
            "rendererId:Integer=-1, " +
            "viewId:Integer=0, " +
            "layerId:Integer=0, " +
            "width:Integer=0, " +
            "height:Integer=0, " +
            "cellWidth:Integer=0, " +
            "cellHeight:Integer=0, " +
            "worldXPos:Float=0.0, " +
            "worldYPos:Float=0.0, " +
            "spherical:Boolean=false",
            attrs.toString()
        );

        attrs.clear();
        grid2.toAttributes( attrs );
        assertEquals(
            "name:String=null, " +
            "rendererId:Integer=-1, " +
            "viewId:Integer=123, " +
            "layerId:Integer=1, " +
            "width:Integer=10, " +
            "height:Integer=10, " +
            "cellWidth:Integer=0, " +
            "cellHeight:Integer=0, " +
            "worldXPos:Float=0.0, " +
            "worldYPos:Float=0.0, " +
            "spherical:Boolean=false",
            attrs.toString()
        );
        assertEquals(
            "[[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
            + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
            + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
            + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
            + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
            + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
            + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
            + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
            + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
            + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]]",
            StringUtils.array2DToString( grid2.grid )
        );

        attrs.clear();
        grid3.toAttributes( attrs );
        assertEquals(
            "name:String=null, " +
            "rendererId:Integer=-1, " +
            "viewId:Integer=123, " +
            "layerId:Integer=1, " +
            "width:Integer=10, " +
            "height:Integer=10, " +
            "cellWidth:Integer=0, " +
            "cellHeight:Integer=0, " +
            "worldXPos:Float=10.0, " +
            "worldYPos:Float=10.0, " +
            "spherical:Boolean=true",
            attrs.toString()
        );
        assertEquals(
            "[[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
                + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
                + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
                + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
                + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
                + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
                + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
                + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
                + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]"
                + "[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]]",
            StringUtils.array2DToString( grid3.grid )
        );
    }
    
    @Test
    public void testSetReset() {
        grid1.setViewId( 1 );
        grid1.setLayerId( 1 );
        grid1.setWidth( 3 );
        grid1.setHeight( 3 );
        AttributeMap attrs = new ComponentAttributeMap( null );

        grid1.toAttributes( attrs );
        assertEquals(
            "name:String=null, "
            + "rendererId:Integer=-1, "
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=3, "
            + "height:Integer=3, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false",
            attrs.toString()
        );
        assertEquals(
            "[[-1, -1, -1]" +
            "[-1, -1, -1]" +
            "[-1, -1, -1]]",
            StringUtils.array2DToString( grid1.grid )
        );
        
        grid1.set( 50, 0, 0 );
        grid1.set( 50, 1, 1 );
        grid1.set( 50, 2, 2 );

        attrs.clear();
        grid1.toAttributes( attrs );
        assertEquals(
            "name:String=null, "
            + "rendererId:Integer=-1, "
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=3, "
            + "height:Integer=3, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false",
            attrs.toString()
        );
        assertEquals(
            "[[50, -1, -1]" +
            "[-1, 50, -1]" +
            "[-1, -1, 50]]",
            StringUtils.array2DToString( grid1.grid )
        );
        
        grid1.set( 150, 2, 0 );
        grid1.set( 150, 1, 1 );
        grid1.set( 150, 0, 2 );

        attrs.clear();
        grid1.toAttributes( attrs );
        assertEquals(
            "name:String=null, "
            + "rendererId:Integer=-1, "
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=3, "
            + "height:Integer=3, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false",
            attrs.toString()
        );
        assertEquals(
            "[[50, -1, 150]" +
            "[-1, 150, -1]" +
            "[150, -1, 50]]",
            StringUtils.array2DToString( grid1.grid )
        );
        
        grid1.reset( 0, 0 );
        grid1.reset( 1, 0 );
        grid1.reset( 2, 0 );

        attrs.clear();
        grid1.toAttributes( attrs );
        assertEquals(
            "name:String=null, "
            + "rendererId:Integer=-1, "
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=3, "
            + "height:Integer=3, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false",
            attrs.toString()
        );
        assertEquals(
            "[[-1, -1, -1]" +
            "[-1, 150, -1]" +
            "[150, -1, 50]]",
            StringUtils.array2DToString( grid1.grid )
        );
        
        try {
            grid1.set( 10, 3, 2 );
            fail( "ArrayIndexOutOfBoundsException expected here" );
        } catch ( ArrayIndexOutOfBoundsException e ) {
            assertEquals( "3", e.getMessage() );
        }
        try {
            grid1.set( 10, 2, 3 );
            fail( "ArrayIndexOutOfBoundsException expected here" );
        } catch ( ArrayIndexOutOfBoundsException e ) {
            assertEquals( "3", e.getMessage() );
        }
        try {
            grid1.set( 10, -3, 2 );
            fail( "ArrayIndexOutOfBoundsException expected here" );
        } catch ( ArrayIndexOutOfBoundsException e ) {
            assertEquals( "-3", e.getMessage() );
        }
    }
    
    @Test
    public void testSphericalSetReset() {
        grid1.setViewId( 1 );
        grid1.setLayerId( 1 );
        grid1.setWidth( 3 );
        grid1.setHeight( 3 );
        grid1.setSpherical( true );
        AttributeMap attrs = new ComponentAttributeMap( null );

        attrs.clear();
        grid1.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
                + "rendererId:Integer=-1, "
                + "viewId:Integer=1, "
                + "layerId:Integer=1, "
                + "width:Integer=3, "
                + "height:Integer=3, "
                + "cellWidth:Integer=0, "
                + "cellHeight:Integer=0, "
                + "worldXPos:Float=0.0, "
                + "worldYPos:Float=0.0, "
                + "spherical:Boolean=true",
            attrs.toString()
        );
        assertEquals(
            "[[-1, -1, -1]" +
            "[-1, -1, -1]" +
            "[-1, -1, -1]]",
            StringUtils.array2DToString( grid1.grid )
        );
        
        grid1.set( 100, 4, 1 );

        attrs.clear();
        grid1.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
                + "rendererId:Integer=-1, "
                + "viewId:Integer=1, "
                + "layerId:Integer=1, "
                + "width:Integer=3, "
                + "height:Integer=3, "
                + "cellWidth:Integer=0, "
                + "cellHeight:Integer=0, "
                + "worldXPos:Float=0.0, "
                + "worldYPos:Float=0.0, "
                + "spherical:Boolean=true",
            attrs.toString()
        );
        assertEquals(
            "[[-1, -1, -1]"
            + "[-1, 100, -1]"
            + "[-1, -1, -1]]",
            StringUtils.array2DToString( grid1.grid )
        );
        
        grid1.set( 100, 1, 5 );

        attrs.clear();
        grid1.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
                + "rendererId:Integer=-1, "
                + "viewId:Integer=1, "
                + "layerId:Integer=1, "
                + "width:Integer=3, "
                + "height:Integer=3, "
                + "cellWidth:Integer=0, "
                + "cellHeight:Integer=0, "
                + "worldXPos:Float=0.0, "
                + "worldYPos:Float=0.0, "
                + "spherical:Boolean=true",
            attrs.toString()
        );
        assertEquals(
            "[[-1, -1, -1]"
            + "[-1, 100, -1]"
            + "[-1, 100, -1]]",
            StringUtils.array2DToString( grid1.grid )
        );
        
        grid1.set( 100, 8, 8 );

        attrs.clear();
        grid1.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
                + "rendererId:Integer=-1, "
                + "viewId:Integer=1, "
                + "layerId:Integer=1, "
                + "width:Integer=3, "
                + "height:Integer=3, "
                + "cellWidth:Integer=0, "
                + "cellHeight:Integer=0, "
                + "worldXPos:Float=0.0, "
                + "worldYPos:Float=0.0, "
                + "spherical:Boolean=true",
            attrs.toString()
        );
        assertEquals(
            "[[-1, -1, -1]"
            + "[-1, 100, -1]"
            + "[-1, 100, 100]]",
            StringUtils.array2DToString( grid1.grid )
        );
        
        grid1.reset( 8, 8 );

        attrs.clear();
        grid1.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
                + "rendererId:Integer=-1, "
                + "viewId:Integer=1, "
                + "layerId:Integer=1, "
                + "width:Integer=3, "
                + "height:Integer=3, "
                + "cellWidth:Integer=0, "
                + "cellHeight:Integer=0, "
                + "worldXPos:Float=0.0, "
                + "worldYPos:Float=0.0, "
                + "spherical:Boolean=true",
            attrs.toString()
        );
        assertEquals(
            "[[-1, -1, -1]"
            + "[-1, 100, -1]"
            + "[-1, 100, -1]]",
            StringUtils.array2DToString( grid1.grid )
        );
    }
    
    @Test
    public void testResize() {
        grid1.setViewId( 1 );
        grid1.setLayerId( 1 );
        grid1.setWidth( 3 );
        grid1.setHeight( 3 );
        AttributeMap attrs = new ComponentAttributeMap( null );

        grid1.set( 100, 0, 0 );
        grid1.set( 100, 1, 1 );
        grid1.set( 100, 2, 2 );

        attrs.clear();
        grid1.toAttributes( attrs );
        assertEquals(
            "name:String=null, "
            + "rendererId:Integer=-1, "
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=3, "
            + "height:Integer=3, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false",
            attrs.toString()
        );
        assertEquals(
            "[[100, -1, -1]"
            + "[-1, 100, -1]"
            + "[-1, -1, 100]]",
            StringUtils.array2DToString( grid1.grid )
        );
        
        grid1.setWidth( 5 );

        attrs.clear();
        grid1.toAttributes( attrs );
        assertEquals(
            "name:String=null, "
            + "rendererId:Integer=-1, "
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=5, "
            + "height:Integer=3, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false",
            attrs.toString()
        );
        assertEquals(
            "[[100, -1, -1, -1, -1]" +
            "[-1, 100, -1, -1, -1]" +
            "[-1, -1, 100, -1, -1]]",
            StringUtils.array2DToString( grid1.grid )
        );
        
        grid1.setHeight( 5 );

        attrs.clear();
        grid1.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
            + "rendererId:Integer=-1, "
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=5, "
            + "height:Integer=5, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false",
            attrs.toString()
        );
        assertEquals(
            "[[100, -1, -1, -1, -1]" +
            "[-1, 100, -1, -1, -1]" +
            "[-1, -1, 100, -1, -1]" +
            "[-1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1]]",
            StringUtils.array2DToString( grid1.grid )
        );
        
        grid1.set( 100, 3, 3 );
        grid1.set( 100, 4, 4 );

        attrs.clear();
        grid1.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
            + "rendererId:Integer=-1, "
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=5, "
            + "height:Integer=5, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false",
            attrs.toString()
        );
        assertEquals(
            "[[100, -1, -1, -1, -1]" +
            "[-1, 100, -1, -1, -1]" +
            "[-1, -1, 100, -1, -1]" +
            "[-1, -1, -1, 100, -1]" +
            "[-1, -1, -1, -1, 100]]",
            StringUtils.array2DToString( grid1.grid )
        );
        
        grid1.setWidth( 3 );
        grid1.setHeight( 3 );

        attrs.clear();
        grid1.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
            + "rendererId:Integer=-1, "
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=3, "
            + "height:Integer=3, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false",
            attrs.toString()
        );
        assertEquals(
            "[[100, -1, -1]" +
            "[-1, 100, -1]" +
            "[-1, -1, 100]]",
            StringUtils.array2DToString( grid1.grid )
        );
    }
    
    @Test
    public void testIterator() {
        grid1.setViewId( 1 );
        grid1.setLayerId( 1 );
        grid1.setWidth( 3 );
        grid1.setHeight( 3 );
        AttributeMap attrs = new ComponentAttributeMap( null );

        grid1.set( 100, 0, 0 );
        grid1.set( 100, 1, 1 );
        grid1.set( 100, 2, 2 );

        attrs.clear();
        grid1.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
            + "rendererId:Integer=-1, "
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=3, "
            + "height:Integer=3, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false",
            attrs.toString()
        );
        assertEquals(
            "[[100, -1, -1]" +
            "[-1, 100, -1]" +
            "[-1, -1, 100]]",
            StringUtils.array2DToString( grid1.grid )
        );
        
        TileIterator iterator = grid1.iterator();
        assertNotNull( iterator );
        assertTrue( "should have next", iterator.hasNext() );
        assertEquals( "expected next", 100, iterator.next() );
        assertEquals( "expected next", 100, iterator.next() );
        assertEquals( "expected next", 100, iterator.next() );
        assertFalse( "expected no next", iterator.hasNext() );
    }
    
    @Test
    public void testMapWorldClipToTileMapClip() {
        grid1.setWorldXPos( 0f );
        grid1.setWorldYPos( 0f );
        grid1.setCellWidth( 10 );
        grid1.setCellHeight( 10 );
        grid1.setWidth( 10 );
        grid1.setHeight( 10 );
        
        Rectangle worldClip1 = new Rectangle( 0, 0, 100, 100 );
        Rectangle tileGridClip1 = grid1.mapWorldClipToTileGridClip( worldClip1 );
        assertEquals( "[x=0,y=0,width=10,height=10]", tileGridClip1.toString() );
        assertEquals( "100", String.valueOf( tileGridClip1.area() ) );
        
        Rectangle worldClip2 = new Rectangle( 5, 5, 95, 95 );
        Rectangle tileGridClip2 = grid1.mapWorldClipToTileGridClip( worldClip2 );
        assertEquals( "[x=0,y=0,width=10,height=10]", tileGridClip2.toString() );
        assertEquals( "100", String.valueOf( tileGridClip2.area() ) );
        
        Rectangle worldClip3 = new Rectangle( 9, 9, 91, 91 );
        Rectangle tileGridClip3 = grid1.mapWorldClipToTileGridClip( worldClip3 );
        assertEquals( "[x=0,y=0,width=10,height=10]", tileGridClip3.toString() );
        assertEquals( "100", String.valueOf( tileGridClip3.area() ) );
        
        Rectangle worldClip4 = new Rectangle( 10, 10, 90, 90 );
        Rectangle tileGridClip4 = grid1.mapWorldClipToTileGridClip( worldClip4 );
        assertEquals( "[x=1,y=1,width=9,height=9]", tileGridClip4.toString() );
        assertEquals( "81", String.valueOf( tileGridClip4.area() ) );
        
        Rectangle worldClip5 = new Rectangle( -50, -50, 100, 100 );
        Rectangle tileGridClip5 = grid1.mapWorldClipToTileGridClip( worldClip5 );
        assertEquals( "[x=0,y=0,width=5,height=5]", tileGridClip5.toString() );
        assertEquals( "25", String.valueOf( tileGridClip5.area() ) );
        
        Rectangle worldClip6 = new Rectangle( 50, 50, 100, 100 );
        Rectangle tileGridClip6 = grid1.mapWorldClipToTileGridClip( worldClip6 );
        assertEquals( "[x=5,y=5,width=5,height=5]", tileGridClip6.toString() );
        assertEquals( "25", String.valueOf( tileGridClip6.area() ) );
        
        Rectangle worldClip7 = new Rectangle( 100, 0, 100, 100 );
        Rectangle tileGridClip7 = grid1.mapWorldClipToTileGridClip( worldClip7 );
        assertEquals( "[x=10,y=0,width=0,height=10]", tileGridClip7.toString() );
        assertEquals( "0", String.valueOf( tileGridClip7.area() ) );
    }

}
