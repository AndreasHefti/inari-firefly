package com.inari.firefly.renderer.tile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.inari.commons.StringUtils;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.ComponentAttributeMap;
import com.inari.firefly.renderer.tile.TileGrid.TileGridIterator;
public class TileGridTest {
    
    @Before
    public void init() {
        Indexer.clear();
    }

    @Test
    public void testCreation() {
        TileGrid grid1 = new TileGrid( 0 );
        TileGrid grid2 = new TileGrid( 1 );
        grid2.setViewId( 123 );
        grid2.setLayerId( 1 );
        grid2.setWidth( 10 );
        grid2.setHeight( 10 );
        TileGrid grid3 = new TileGrid( 2 );
        grid3.setViewId( 123 );
        grid3.setLayerId( 1 );
        grid3.setWidth( 10 );
        grid3.setHeight( 10 );
        grid3.setWorldXPos( 10f );
        grid3.setWorldYPos( 10f );
        grid3.setSpherical( true );
        
        AttributeMap attrs = new ComponentAttributeMap();
        

        grid1.toAttributes( attrs );
        assertEquals(
            "name:String=null, " +
            "viewId:Integer=0, " +
            "layerId:Integer=0, " +
            "width:Integer=0, " +
            "height:Integer=0, " +
            "cellWidth:Integer=0, " +
            "cellHeight:Integer=0, " +
            "worldXPos:Float=0.0, " +
            "worldYPos:Float=0.0, " +
            "spherical:Boolean=false, " +
            "renderMode:TileRenderMode=FULL_RENDERING",
            attrs.toString()
        );

        attrs.clear();
        grid2.toAttributes( attrs );
        assertEquals(
            "name:String=null, " +
            "viewId:Integer=123, " +
            "layerId:Integer=1, " +
            "width:Integer=10, " +
            "height:Integer=10, " +
            "cellWidth:Integer=0, " +
            "cellHeight:Integer=0, " +
            "worldXPos:Float=0.0, " +
            "worldYPos:Float=0.0, " +
            "spherical:Boolean=false, " +
            "renderMode:TileRenderMode=FULL_RENDERING",
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
            "viewId:Integer=123, " +
            "layerId:Integer=1, " +
            "width:Integer=10, " +
            "height:Integer=10, " +
            "cellWidth:Integer=0, " +
            "cellHeight:Integer=0, " +
            "worldXPos:Float=10.0, " +
            "worldYPos:Float=10.0, " +
            "spherical:Boolean=true, " +
            "renderMode:TileRenderMode=FULL_RENDERING",
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
        TileGrid grid = new TileGrid( 1 );
        grid.setViewId( 1 );
        grid.setLayerId( 1 );
        grid.setWidth( 3 );
        grid.setHeight( 3 );
        AttributeMap attrs = new ComponentAttributeMap();

        grid.toAttributes( attrs );
        assertEquals(
            "name:String=null, "
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=3, "
            + "height:Integer=3, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false, "
            + "renderMode:TileRenderMode=FULL_RENDERING",
            attrs.toString()
        );
        assertEquals(
            "[[-1, -1, -1]" +
            "[-1, -1, -1]" +
            "[-1, -1, -1]]",
            StringUtils.array2DToString( grid.grid )
        );
        
        grid.set( 50, 0, 0 );
        grid.set( 50, 1, 1 );
        grid.set( 50, 2, 2 );

        attrs.clear();
        grid.toAttributes( attrs );
        assertEquals(
            "name:String=null, "
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=3, "
            + "height:Integer=3, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false, "
            + "renderMode:TileRenderMode=FULL_RENDERING",
            attrs.toString()
        );
        assertEquals(
            "[[50, -1, -1]" +
            "[-1, 50, -1]" +
            "[-1, -1, 50]]",
            StringUtils.array2DToString( grid.grid )
        );
        
        grid.set( 150, 2, 0 );
        grid.set( 150, 1, 1 );
        grid.set( 150, 0, 2 );

        attrs.clear();
        grid.toAttributes( attrs );
        assertEquals(
            "name:String=null, "
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=3, "
            + "height:Integer=3, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false, "
            + "renderMode:TileRenderMode=FULL_RENDERING",
            attrs.toString()
        );
        assertEquals(
            "[[50, -1, 150]" +
            "[-1, 150, -1]" +
            "[150, -1, 50]]",
            StringUtils.array2DToString( grid.grid )
        );
        
        grid.reset( 0, 0 );
        grid.reset( 1, 0 );
        grid.reset( 2, 0 );

        attrs.clear();
        grid.toAttributes( attrs );
        assertEquals(
            "name:String=null, "
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=3, "
            + "height:Integer=3, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false, "
            + "renderMode:TileRenderMode=FULL_RENDERING",
            attrs.toString()
        );
        assertEquals(
            "[[-1, -1, -1]" +
            "[-1, 150, -1]" +
            "[150, -1, 50]]",
            StringUtils.array2DToString( grid.grid )
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
        TileGrid grid = new TileGrid( 1 );
        grid.setViewId( 1 );
        grid.setLayerId( 1 );
        grid.setWidth( 3 );
        grid.setHeight( 3 );
        grid.setSpherical( true );
        AttributeMap attrs = new ComponentAttributeMap();

        attrs.clear();
        grid.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
                        + "viewId:Integer=1, "
                + "layerId:Integer=1, "
                + "width:Integer=3, "
                + "height:Integer=3, "
                + "cellWidth:Integer=0, "
                + "cellHeight:Integer=0, "
                + "worldXPos:Float=0.0, "
                + "worldYPos:Float=0.0, "
                + "spherical:Boolean=true, "
                + "renderMode:TileRenderMode=FULL_RENDERING",
            attrs.toString()
        );
        assertEquals(
            "[[-1, -1, -1]" +
            "[-1, -1, -1]" +
            "[-1, -1, -1]]",
            StringUtils.array2DToString( grid.grid )
        );
        
        grid.set( 100, 4, 1 );

        attrs.clear();
        grid.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
                        + "viewId:Integer=1, "
                + "layerId:Integer=1, "
                + "width:Integer=3, "
                + "height:Integer=3, "
                + "cellWidth:Integer=0, "
                + "cellHeight:Integer=0, "
                + "worldXPos:Float=0.0, "
                + "worldYPos:Float=0.0, "
                + "spherical:Boolean=true, "
                + "renderMode:TileRenderMode=FULL_RENDERING",
            attrs.toString()
        );
        assertEquals(
            "[[-1, -1, -1]"
            + "[-1, 100, -1]"
            + "[-1, -1, -1]]",
            StringUtils.array2DToString( grid.grid )
        );
        
        grid.set( 100, 1, 5 );

        attrs.clear();
        grid.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
                        + "viewId:Integer=1, "
                + "layerId:Integer=1, "
                + "width:Integer=3, "
                + "height:Integer=3, "
                + "cellWidth:Integer=0, "
                + "cellHeight:Integer=0, "
                + "worldXPos:Float=0.0, "
                + "worldYPos:Float=0.0, "
                + "spherical:Boolean=true, "
                + "renderMode:TileRenderMode=FULL_RENDERING",
            attrs.toString()
        );
        assertEquals(
            "[[-1, -1, -1]"
            + "[-1, 100, -1]"
            + "[-1, 100, -1]]",
            StringUtils.array2DToString( grid.grid )
        );
        
        grid.set( 100, 8, 8 );

        attrs.clear();
        grid.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
                        + "viewId:Integer=1, "
                + "layerId:Integer=1, "
                + "width:Integer=3, "
                + "height:Integer=3, "
                + "cellWidth:Integer=0, "
                + "cellHeight:Integer=0, "
                + "worldXPos:Float=0.0, "
                + "worldYPos:Float=0.0, "
                + "spherical:Boolean=true, "
                + "renderMode:TileRenderMode=FULL_RENDERING",
            attrs.toString()
        );
        assertEquals(
            "[[-1, -1, -1]"
            + "[-1, 100, -1]"
            + "[-1, 100, 100]]",
            StringUtils.array2DToString( grid.grid )
        );
        
        grid.reset( 8, 8 );

        attrs.clear();
        grid.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
                        + "viewId:Integer=1, "
                + "layerId:Integer=1, "
                + "width:Integer=3, "
                + "height:Integer=3, "
                + "cellWidth:Integer=0, "
                + "cellHeight:Integer=0, "
                + "worldXPos:Float=0.0, "
                + "worldYPos:Float=0.0, "
                + "spherical:Boolean=true, "
                + "renderMode:TileRenderMode=FULL_RENDERING",
            attrs.toString()
        );
        assertEquals(
            "[[-1, -1, -1]"
            + "[-1, 100, -1]"
            + "[-1, 100, -1]]",
            StringUtils.array2DToString( grid.grid )
        );
    }
    
    @Test
    public void testResize() {
        TileGrid grid = new TileGrid( 1 );
        grid.setViewId( 1 );
        grid.setLayerId( 1 );
        grid.setWidth( 3 );
        grid.setHeight( 3 );
        AttributeMap attrs = new ComponentAttributeMap();

        grid.set( 100, 0, 0 );
        grid.set( 100, 1, 1 );
        grid.set( 100, 2, 2 );

        attrs.clear();
        grid.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
                        + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=3, "
            + "height:Integer=3, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false, "
            + "renderMode:TileRenderMode=FULL_RENDERING",
            attrs.toString()
        );
        assertEquals(
            "[[100, -1, -1]"
            + "[-1, 100, -1]"
            + "[-1, -1, 100]]",
            StringUtils.array2DToString( grid.grid )
        );
        
        grid.setWidth( 5 );

        attrs.clear();
        grid.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
                        + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=5, "
            + "height:Integer=3, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false, "
            + "renderMode:TileRenderMode=FULL_RENDERING",
            attrs.toString()
        );
        assertEquals(
            "[[100, -1, -1, -1, -1]" +
            "[-1, 100, -1, -1, -1]" +
            "[-1, -1, 100, -1, -1]]",
            StringUtils.array2DToString( grid.grid )
        );
        
        grid.setHeight( 5 );

        attrs.clear();
        grid.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
                        + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=5, "
            + "height:Integer=5, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false, "
            + "renderMode:TileRenderMode=FULL_RENDERING",
            attrs.toString()
        );
        assertEquals(
            "[[100, -1, -1, -1, -1]" +
            "[-1, 100, -1, -1, -1]" +
            "[-1, -1, 100, -1, -1]" +
            "[-1, -1, -1, -1, -1]" +
            "[-1, -1, -1, -1, -1]]",
            StringUtils.array2DToString( grid.grid )
        );
        
        grid.set( 100, 3, 3 );
        grid.set( 100, 4, 4 );

        attrs.clear();
        grid.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
                        + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=5, "
            + "height:Integer=5, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false, "
            + "renderMode:TileRenderMode=FULL_RENDERING",
            attrs.toString()
        );
        assertEquals(
            "[[100, -1, -1, -1, -1]" +
            "[-1, 100, -1, -1, -1]" +
            "[-1, -1, 100, -1, -1]" +
            "[-1, -1, -1, 100, -1]" +
            "[-1, -1, -1, -1, 100]]",
            StringUtils.array2DToString( grid.grid )
        );
        
        grid.setWidth( 3 );
        grid.setHeight( 3 );

        attrs.clear();
        grid.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
                        + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=3, "
            + "height:Integer=3, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false, "
            + "renderMode:TileRenderMode=FULL_RENDERING",
            attrs.toString()
        );
        assertEquals(
            "[[100, -1, -1]" +
            "[-1, 100, -1]" +
            "[-1, -1, 100]]",
            StringUtils.array2DToString( grid.grid )
        );
    }
    
    @Test
    public void testIterator() {
        TileGrid grid = new TileGrid( 1 );
        grid.setViewId( 1 );
        grid.setLayerId( 1 );
        grid.setWidth( 3 );
        grid.setHeight( 3 );
        AttributeMap attrs = new ComponentAttributeMap();

        grid.set( 100, 0, 0 );
        grid.set( 100, 1, 1 );
        grid.set( 100, 2, 2 );

        attrs.clear();
        grid.toAttributes( attrs );
        assertEquals(
                "name:String=null, "
                        + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "width:Integer=3, "
            + "height:Integer=3, "
            + "cellWidth:Integer=0, "
            + "cellHeight:Integer=0, "
            + "worldXPos:Float=0.0, "
            + "worldYPos:Float=0.0, "
            + "spherical:Boolean=false, "
            + "renderMode:TileRenderMode=FULL_RENDERING",
            attrs.toString()
        );
        assertEquals(
            "[[100, -1, -1]" +
            "[-1, 100, -1]" +
            "[-1, -1, 100]]",
            StringUtils.array2DToString( grid.grid )
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
