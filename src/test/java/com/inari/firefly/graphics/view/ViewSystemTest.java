package com.inari.firefly.graphics.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.inari.commons.geom.PositionF;
import com.inari.commons.geom.Rectangle;
import com.inari.firefly.FFTest;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.ComponentCreationException;

public class ViewSystemTest extends FFTest {

    @Test
    public void testCreation() {
        ViewSystem viewSystem = ffContext.getSystem( ViewSystem.SYSTEM_KEY );
        
        Attributes attrs = new Attributes();
        
        ffContext.toAttributes( attrs, View.TYPE_KEY );
        assertEquals( 
            "SystemComponent:View(0)::" +
            "name:String=BASE_VIEW, " +
            "bounds:Rectangle=[x=0,y=0,width=100,height=100], " +
            "worldPosition:PositionF=[x=0.0,y=0.0], " +
            "clearColor:RGBColor=[r=0.0,g=0.0,b=0.0,a=1.0], " +
            "tintColor:RGBColor:ESprite=[r=1.0,g=1.0,b=1.0,a=1.0], " +
            "blendMode:BlendMode:ESprite=NONE, " +
            "layeringEnabled:Boolean=false, " +
            "zoom:Float=1.0, " +
            "fboScaler:Float=4.0, " +
            "controllerId:Integer=-1", 
            attrs.toString() 
        );
        assertEquals( 
            "EventLog [events=[]]", 
            eventLog.toString() 
        );
        
        assertFalse( viewSystem.hasViewports() );
        assertFalse( viewSystem.hasActiveViewports() );
        assertFalse( viewSystem.isLayeringEnabled( ViewSystem.BASE_VIEW_ID ) );
        assertFalse( viewSystem.hasLayer( ViewSystem.BASE_VIEW_ID, 0 ) );
        
        View baseView = viewSystem.getView( ViewSystem.BASE_VIEW_ID );
        assertNotNull( baseView );
        assertTrue( baseView.active );
        assertTrue( baseView.isBase() );
        assertTrue( -1 == baseView.order );
    }
    
    @Test
    public void testCreateViews() {
        ViewSystem viewSystem = ffContext.getSystem( ViewSystem.SYSTEM_KEY );

        Attributes attrs = new Attributes();
        
        viewSystem.getViewBuilder()
            .set( View.NAME, "Header" )
            .set( View.BOUNDS, new Rectangle( 0, 0, 10, 100 ) )
            .set( View.WORLD_POSITION, new PositionF( 0, 0 ) )
            .buildAndNext( 1 )
            .set( View.NAME, "Body" )
            .set( View.BOUNDS, new Rectangle( 0, 10, 90, 100 ) )
            .set( View.WORLD_POSITION, new PositionF( 0, 0 ) )
            .build( 2 );
        
        ffContext.toAttributes( attrs, View.TYPE_KEY );
        assertEquals( 
            "SystemComponent:View(0)::" +
            "name:String=BASE_VIEW, " +
            "bounds:Rectangle=[x=0,y=0,width=100,height=100], " +
            "worldPosition:PositionF=[x=0.0,y=0.0], " +
            "clearColor:RGBColor=[r=0.0,g=0.0,b=0.0,a=1.0], " +
            "tintColor:RGBColor:ESprite=[r=1.0,g=1.0,b=1.0,a=1.0], " +
            "blendMode:BlendMode:ESprite=NONE, " +
            "layeringEnabled:Boolean=false, " +
            "zoom:Float=1.0, " +
            "fboScaler:Float=4.0, " +
            "controllerId:Integer=-1 " +
            "SystemComponent:View(1)::" +
            "name:String=Header, " +
            "bounds:Rectangle=[x=0,y=0,width=10,height=100], " +
            "worldPosition:PositionF=[x=0.0,y=0.0], " +
            "clearColor:RGBColor=[r=0.0,g=0.0,b=0.0,a=1.0], " +
            "tintColor:RGBColor:ESprite=[r=1.0,g=1.0,b=1.0,a=1.0], " +
            "blendMode:BlendMode:ESprite=NONE, " +
            "layeringEnabled:Boolean=false, " +
            "zoom:Float=1.0, " +
            "fboScaler:Float=4.0, " +
            "controllerId:Integer=-1 " +
            "SystemComponent:View(2)::" +
            "name:String=Body, " +
            "bounds:Rectangle=[x=0,y=10,width=90,height=100], " +
            "worldPosition:PositionF=[x=0.0,y=0.0], " +
            "clearColor:RGBColor=[r=0.0,g=0.0,b=0.0,a=1.0], " +
            "tintColor:RGBColor:ESprite=[r=1.0,g=1.0,b=1.0,a=1.0], " +
            "blendMode:BlendMode:ESprite=NONE, " +
            "layeringEnabled:Boolean=false, " +
            "zoom:Float=1.0, " +
            "fboScaler:Float=4.0, " +
            "controllerId:Integer=-1", 
            attrs.toString() 
        );
        assertEquals( 
            "EventLog [events=[" +
            "ViewEvent [eventType=VIEW_CREATED, view=1], " +
            "ViewEvent [eventType=VIEW_CREATED, view=2]]]", 
            eventLog.toString() 
        );
        
        assertTrue( viewSystem.hasViewports() );
        assertFalse( viewSystem.hasActiveViewports() );

        
        View view1 = viewSystem.getView( 1 );
        assertNotNull( view1 );
        assertFalse( view1.active );
        assertFalse( view1.isBase() );
        assertTrue( 0 == view1.order );
        assertFalse( viewSystem.isLayeringEnabled( view1.index() ) );
        assertFalse( viewSystem.hasLayer( view1.index(), 0 ) );
        
        View view2 = viewSystem.getView( 2 );
        assertNotNull( view2 );
        assertFalse( view2.active );
        assertFalse( view2.isBase() );
        assertTrue( 1 == view2.order );
        assertFalse( viewSystem.isLayeringEnabled( view2.index() ) );
        assertFalse( viewSystem.hasLayer( view2.index(), 0 ) );
    }
    
    @Test
    public void testCreateLayersForBaseView() {
        ViewSystem viewSystem = ffContext.getSystem( ViewSystem.SYSTEM_KEY );
        
        Attributes attrs = new Attributes();
        
        try {
            viewSystem.getLayerBuilder()
                .set( Layer.VIEW_ID, ViewSystem.BASE_VIEW_ID )
                .set( Layer.NAME, "Layer1" )
                .build();
            fail( "Exception expected here because base view has layering not enabled" );
        } catch ( Exception e ) {
            assertEquals( "Layering is not enabled for view with id: 0. Enable Layering for View first", e.getMessage() );
        }
        
        viewSystem.getView( ViewSystem.BASE_VIEW_ID ).setLayeringEnabled( true );
        
        viewSystem.getLayerBuilder()
            .set( Layer.VIEW_ID, ViewSystem.BASE_VIEW_ID )
            .set( Layer.NAME, "Layer1" )
            .buildAndNext()
            .set( Layer.VIEW_ID, ViewSystem.BASE_VIEW_ID )
            .set( Layer.NAME, "Layer2" )
            .buildAndNext()
            .set( Layer.VIEW_ID, ViewSystem.BASE_VIEW_ID )
            .set( Layer.NAME, "Layer3" )
            .build();
        
        ffContext.toAttributes( attrs, View.TYPE_KEY, Layer.TYPE_KEY );
        assertEquals( 
            "SystemComponent:View(0)::" +
            "name:String=BASE_VIEW, " +
            "bounds:Rectangle=[x=0,y=0,width=100,height=100], " +
            "worldPosition:PositionF=[x=0.0,y=0.0], " +
            "clearColor:RGBColor=[r=0.0,g=0.0,b=0.0,a=1.0], " +
            "tintColor:RGBColor:ESprite=[r=1.0,g=1.0,b=1.0,a=1.0], " +
            "blendMode:BlendMode:ESprite=NONE, " +
            "layeringEnabled:Boolean=true, " +
            "zoom:Float=1.0, " +
            "fboScaler:Float=4.0, " +
            "controllerId:Integer=-1 " +
            "SystemComponent:Layer(1)::" +
            "name:String=Layer1, " +
            "viewId:Integer=0 " +
            "SystemComponent:Layer(2)::" +
            "name:String=Layer2, " +
            "viewId:Integer=0 " +
            "SystemComponent:Layer(3)::" +
            "name:String=Layer3, " +
            "viewId:Integer=0", 
            attrs.toString() 
        );
        assertEquals( 
            "EventLog [events=[]]", 
            eventLog.toString() 
        );
        
        // try to build a layer for an inexistent view
        try {
            viewSystem.getLayerBuilder()
                .set( Layer.VIEW_ID, 100 )
                .set( Layer.NAME, "Layer1" )
                .build();
            fail( "Exception expected here" );
        } catch ( ComponentCreationException e ) {
            assertEquals( "The View with id: 100. dont exists.", e.getMessage() );
        }
    }

}
