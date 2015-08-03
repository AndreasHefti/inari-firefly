package com.inari.firefly.system.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.EventDispatcherMock;
import com.inari.firefly.LowerSystemFacadeMock;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextImpl;
import com.inari.firefly.system.FFContextImpl.InitMap;

public class ViewSystemTest {

    @Test
    public void testCreation() {
        Indexer.clear();
        FFContext context = createContext();
        IEventDispatcher eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        ViewSystem viewSystem = new ViewSystem();
        viewSystem.init( context );
        Attributes attrs = new Attributes();
        
        viewSystem.toAttributes( attrs );
        assertEquals( 
            "View(0)::" +
            "name:String=BASE_VIEW, " +
            "order:Integer=-1, " +
            "active:Boolean=true, " +
            "bounds:Rectangle=[x=0,y=0,width=100,height=100], " +
            "worldPosition:Position=[x=0,y=0], " +
            "clearColor:RGBColor=[r=0.0,g=0.0,b=0.0,a=1.0], " +
            "layeringEnabled:Boolean=false, " +
            "zoom:Float=1.0", 
            attrs.toString() 
        );
        assertEquals( 
            "TestEventDispatcher [events=[ViewEvent [eventType=VIEW_CREATED, view=0]]]", 
            eventDispatcher.toString() 
        );
    }
    
    @Test
    public void testCreateViews() {
        Indexer.clear();
        FFContext context = createContext();
        IEventDispatcher eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        ViewSystem viewSystem = new ViewSystem();
        viewSystem.init( context );
        Attributes attrs = new Attributes();
        
        viewSystem.getViewBuilder()
            .setAttribute( View.ORDER, 2 )
            .setAttribute( View.NAME, "Header" )
            .setAttribute( View.ACTIVE, true )
            .setAttribute( View.BOUNDS, new Rectangle( 0, 0, 10, 100 ) )
            .setAttribute( View.WORLD_POSITION, new Position( 0, 0 ) )
            .buildAndNext( 1 )
            .setAttribute( View.ORDER, 3 )
            .setAttribute( View.NAME, "Body" )
            .setAttribute( View.ACTIVE, true )
            .setAttribute( View.BOUNDS, new Rectangle( 0, 10, 90, 100 ) )
            .setAttribute( View.WORLD_POSITION, new Position( 0, 0 ) )
            .build( 2 );
        
        viewSystem.toAttributes( attrs );
        assertEquals( 
            "View(0)::" +
            "name:String=BASE_VIEW, " +
            "order:Integer=-1, " +
            "active:Boolean=true, " +
            "bounds:Rectangle=[x=0,y=0,width=100,height=100], " +
            "worldPosition:Position=[x=0,y=0], " +
            "clearColor:RGBColor=[r=0.0,g=0.0,b=0.0,a=1.0], " +
            "layeringEnabled:Boolean=false, " +
            "zoom:Float=1.0 " +
            "View(1)::" +
            "name:String=Header, " +
            "order:Integer=2, " +
            "active:Boolean=true, " +
            "bounds:Rectangle=[x=0,y=0,width=10,height=100], " +
            "worldPosition:Position=[x=0,y=0], " +
            "clearColor:RGBColor=[r=0.0,g=0.0,b=0.0,a=1.0], " +
            "layeringEnabled:Boolean=false, " +
            "zoom:Float=1.0 " +
            "View(2)::" +
            "name:String=Body, " +
            "order:Integer=3, " +
            "active:Boolean=true, " +
            "bounds:Rectangle=[x=0,y=10,width=90,height=100], " +
            "worldPosition:Position=[x=0,y=0], " +
            "clearColor:RGBColor=[r=0.0,g=0.0,b=0.0,a=1.0], " +
            "layeringEnabled:Boolean=false, " +
            "zoom:Float=1.0", 
            attrs.toString() 
        );
        assertEquals( 
            "TestEventDispatcher [events=[" +
            "ViewEvent [eventType=VIEW_CREATED, view=0], " +
            "ViewEvent [eventType=VIEW_CREATED, view=1], " +
            "ViewEvent [eventType=VIEW_CREATED, view=2]]]", 
            eventDispatcher.toString() 
        );
    }
    
    @Test
    public void testCreateLayersForBaseView() {
        Indexer.clear();
        FFContext context = createContext();
        IEventDispatcher eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        ViewSystem viewSystem = new ViewSystem();
        viewSystem.init( context );
        Attributes attrs = new Attributes();
        
        try {
            viewSystem.getLayerBuilder()
                .setAttribute( Layer.VIEW_ID, ViewSystem.BASE_VIEW_ID )
                .setAttribute( Layer.NAME, "Layer1" )
                .build();
            fail( "Exception expected here because base view has layering not enabled" );
        } catch ( Exception e ) {
            assertEquals( "Layering is not enabled for view with id: 0. Enable Layering for View first", e.getMessage() );
        }
        
        viewSystem.getView( ViewSystem.BASE_VIEW_ID ).setLayeringEnabled( true );
        
        viewSystem.getLayerBuilder()
            .setAttribute( Layer.VIEW_ID, ViewSystem.BASE_VIEW_ID )
            .setAttribute( Layer.NAME, "Layer1" )
            .buildAndNext()
            .setAttribute( Layer.VIEW_ID, ViewSystem.BASE_VIEW_ID )
            .setAttribute( Layer.NAME, "Layer2" )
            .buildAndNext()
            .setAttribute( Layer.VIEW_ID, ViewSystem.BASE_VIEW_ID )
            .setAttribute( Layer.NAME, "Layer3" )
            .build();
        
        viewSystem.toAttributes( attrs );
        assertEquals( 
            "View(0)::" +
            "name:String=BASE_VIEW, " +
            "order:Integer=-1, " +
            "active:Boolean=true, " +
            "bounds:Rectangle=[x=0,y=0,width=100,height=100], " +
            "worldPosition:Position=[x=0,y=0], " +
            "clearColor:RGBColor=[r=0.0,g=0.0,b=0.0,a=1.0], " +
            "layeringEnabled:Boolean=true, " +
            "zoom:Float=1.0 " +
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
                .setAttribute( Layer.VIEW_ID, 100 )
                .setAttribute( Layer.NAME, "Layer1" )
                .build();
            fail( "Exception expected here" );
        } catch ( IndexOutOfBoundsException e ) {
            assertEquals( "Index: 100, Size: 1", e.getMessage() );
        }
    }

    private FFContext createContext() {
        InitMap initMap = new InitMap();
        initMap.put( FFContext.EVENT_DISPATCHER, EventDispatcherMock.class );
        initMap.put( FFContext.LOWER_SYSTEM_FACADE, LowerSystemFacadeMock.class );
        return new FFContextImpl( initMap, true );
    }

}
