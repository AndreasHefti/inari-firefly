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
import com.inari.firefly.FFTest;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.ComponentAttributeMap;
import com.inari.firefly.graphics.tile.TileGrid.TileGridIterator;
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
            "viewId:Integer=0, " +
            "layerId:Integer=0, " +
            "rendererKey:RendererKey=null, " +
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
            "viewId:Integer=123, " +
            "layerId:Integer=1, " +
            "rendererKey:RendererKey=null, " +
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
            "viewId:Integer=123, " +
            "layerId:Integer=1, " +
            "rendererKey:RendererKey=null, " +
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
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "rendererKey:RendererKey=null, "
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
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "rendererKey:RendererKey=null, "
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
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "rendererKey:RendererKey=null, "
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
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "rendererKey:RendererKey=null, "
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
                + "viewId:Integer=1, "
                + "layerId:Integer=1, "
                + "rendererKey:RendererKey=null, "
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
                + "viewId:Integer=1, "
                + "layerId:Integer=1, "
                + "rendererKey:RendererKey=null, "
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
                + "viewId:Integer=1, "
                + "layerId:Integer=1, "
                + "rendererKey:RendererKey=null, "
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
                + "viewId:Integer=1, "
                + "layerId:Integer=1, "
                + "rendererKey:RendererKey=null, "
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
                + "viewId:Integer=1, "
                + "layerId:Integer=1, "
                + "rendererKey:RendererKey=null, "
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
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "rendererKey:RendererKey=null, "
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
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "rendererKey:RendererKey=null, "
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
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "rendererKey:RendererKey=null, "
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
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "rendererKey:RendererKey=null, "
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
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "rendererKey:RendererKey=null, "
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
            + "viewId:Integer=1, "
            + "layerId:Integer=1, "
            + "rendererKey:RendererKey=null, "
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
        
        
        TileGridIterator tileGridIterator = grid1.getTileGridIterator();
        assertNotNull( tileGridIterator );
        assertTrue( "should have next", tileGridIterator.hasNext() );
        assertEquals( "expected next", 100, tileGridIterator.next() );
        assertEquals( "expected next", 100, tileGridIterator.next() );
        assertEquals( "expected next", 100, tileGridIterator.next() );
        assertFalse( "expected no next", tileGridIterator.hasNext() );
    }
    
    

}
