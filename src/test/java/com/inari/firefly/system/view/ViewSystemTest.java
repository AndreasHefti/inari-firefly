package com.inari.firefly.system.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.FireFlyMock;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.system.FFContext;

public class ViewSystemTest {

    @Test
    public void testCreation() {
        Indexer.clear();
        FFContext context = new FireFlyMock().getContext();
        IEventDispatcher eventDispatcher = context.getEventDispatcher();
        ViewSystem viewSystem = context.getSystem( ViewSystem.CONTEXT_KEY );
        
        Attributes attrs = new Attributes();
        
        context.toAttributes( attrs, View.TYPE_KEY );
        assertEquals( 
            "View(0)::" +
            "name:String=BASE_VIEW, " +
            "bounds:Rectangle=[x=0,y=0,width=100,height=100], " +
            "worldPosition:Position=[x=0,y=0], " +
            "clearColor:RGBColor=[r=0.0,g=0.0,b=0.0,a=1.0], " +
            "tintColor:RGBColor:ESprite=[r=1.0,g=1.0,b=1.0,a=1.0], " +
            "blendMode:BlendMode:ESprite=NONE, " +
            "layeringEnabled:Boolean=false, " +
            "zoom:Float=1.0, " +
            "controllerId:int[]=null", 
            attrs.toString() 
        );
        assertEquals( 
            "TestEventDispatcher [events=[ViewEvent [eventType=VIEW_CREATED, view=0]]]", 
            eventDispatcher.toString() 
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
        Indexer.clear();
        FFContext context = new FireFlyMock().getContext();
        IEventDispatcher eventDispatcher = context.getEventDispatcher();
        ViewSystem viewSystem = context.getSystem( ViewSystem.CONTEXT_KEY );

        Attributes attrs = new Attributes();
        
        viewSystem.getViewBuilder()
            .set( View.NAME, "Header" )
            .set( View.BOUNDS, new Rectangle( 0, 0, 10, 100 ) )
            .set( View.WORLD_POSITION, new Position( 0, 0 ) )
            .buildAndNext( 1 )
            .set( View.NAME, "Body" )
            .set( View.BOUNDS, new Rectangle( 0, 10, 90, 100 ) )
            .set( View.WORLD_POSITION, new Position( 0, 0 ) )
            .build( 2 );
        
        context.toAttributes( attrs, View.TYPE_KEY );
        assertEquals( 
            "View(0)::" +
            "name:String=BASE_VIEW, " +
            "bounds:Rectangle=[x=0,y=0,width=100,height=100], " +
            "worldPosition:Position=[x=0,y=0], " +
            "clearColor:RGBColor=[r=0.0,g=0.0,b=0.0,a=1.0], " +
            "tintColor:RGBColor:ESprite=[r=1.0,g=1.0,b=1.0,a=1.0], " +
            "blendMode:BlendMode:ESprite=NONE, " +
            "layeringEnabled:Boolean=false, " +
            "zoom:Float=1.0, " +
            "controllerId:int[]=null " +
            "View(1)::" +
            "name:String=Header, " +
            "bounds:Rectangle=[x=0,y=0,width=10,height=100], " +
            "worldPosition:Position=[x=0,y=0], " +
            "clearColor:RGBColor=[r=0.0,g=0.0,b=0.0,a=1.0], " +
            "tintColor:RGBColor:ESprite=[r=1.0,g=1.0,b=1.0,a=1.0], " +
            "blendMode:BlendMode:ESprite=NONE, " +
            "layeringEnabled:Boolean=false, " +
            "zoom:Float=1.0, " +
            "controllerId:int[]=null " +
            "View(2)::" +
            "name:String=Body, " +
            "bounds:Rectangle=[x=0,y=10,width=90,height=100], " +
            "worldPosition:Position=[x=0,y=0], " +
            "clearColor:RGBColor=[r=0.0,g=0.0,b=0.0,a=1.0], " +
            "tintColor:RGBColor:ESprite=[r=1.0,g=1.0,b=1.0,a=1.0], " +
            "blendMode:BlendMode:ESprite=NONE, " +
            "layeringEnabled:Boolean=false, " +
            "zoom:Float=1.0, " +
            "controllerId:int[]=null", 
            attrs.toString() 
        );
        assertEquals( 
            "TestEventDispatcher [events=[" +
            "ViewEvent [eventType=VIEW_CREATED, view=0], " +
            "ViewEvent [eventType=VIEW_CREATED, view=1], " +
            "ViewEvent [eventType=VIEW_CREATED, view=2]]]", 
            eventDispatcher.toString() 
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
        Indexer.clear();
        FFContext context = new FireFlyMock().getContext();
        IEventDispatcher eventDispatcher = context.getEventDispatcher();
        ViewSystem viewSystem = context.getSystem( ViewSystem.CONTEXT_KEY );
        
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
        
        context.toAttributes( attrs, View.TYPE_KEY, Layer.TYPE_KEY );
        assertEquals( 
            "View(0)::" +
            "name:String=BASE_VIEW, " +
            "bounds:Rectangle=[x=0,y=0,width=100,height=100], " +
            "worldPosition:Position=[x=0,y=0], " +
            "clearColor:RGBColor=[r=0.0,g=0.0,b=0.0,a=1.0], " +
            "tintColor:RGBColor:ESprite=[r=1.0,g=1.0,b=1.0,a=1.0], " +
            "blendMode:BlendMode:ESprite=NONE, " +
            "layeringEnabled:Boolean=true, " +
            "zoom:Float=1.0, " +
            "controllerId:int[]=null " +
            "Layer(1)::" +
            "name:String=Layer1, " +
            "viewId:Integer=0 " +
            "Layer(2)::" +
            "name:String=Layer2, " +
            "viewId:Integer=0 " +
            "Layer(3)::" +
            "name:String=Layer3, " +
            "viewId:Integer=0", 
            attrs.toString() 
        );
        assertEquals( 
            "TestEventDispatcher [events=[ViewEvent [eventType=VIEW_CREATED, view=0]]]", 
            eventDispatcher.toString() 
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
