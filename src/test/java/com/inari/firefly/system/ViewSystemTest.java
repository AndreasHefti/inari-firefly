package com.inari.firefly.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.EventDispatcherMock;
import com.inari.firefly.FFContext;
import com.inari.firefly.LowerSystemFacadeMock;
import com.inari.firefly.system.FFContextImpl.InitMap;

public class ViewSystemTest {

    @Test
    public void testCreation() {
        Indexer.clear();
        FFContext context = createContext();
        IEventDispatcher eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
        ViewSystem viewSystem = new ViewSystem( context );
        
        
        assertEquals( 
            "ViewSystem [views=DynArray [list=[" +
            "View [isBase=true, order=-1, name=BASE_VIEW, active=true, layeringEnabled=false, bounds=Rectangle: 0,0,100,100, worldPosition=Position: 0,0, clearColor=RGBColor [configId=null, r=0.0, g=0.0, b=0.0, a=1.0], zoom=1.0, indexedId()=0]], size()=1, capacity()=1], " +
            "layersOfView=DynArray [list=[], size()=0, capacity()=0]]", 
            viewSystem.toString() 
        );
        assertEquals( 
            "TestEventDispatcher [events=[" +
            "ViewEvent [type=VIEW_CREATED, view=0]]]", 
            eventDispatcher.toString() 
        );
    }
    
    @Test
    public void testCreateViews() {
        Indexer.clear();
        FFContext context = createContext();
        IEventDispatcher eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
        ViewSystem viewSystem = new ViewSystem( context );
        
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
        
        assertEquals( 
            "ViewSystem [views=DynArray [list=[" +
            "View [isBase=true, order=-1, name=BASE_VIEW, active=true, layeringEnabled=false, bounds=Rectangle: 0,0,100,100, worldPosition=Position: 0,0, clearColor=RGBColor [configId=null, r=0.0, g=0.0, b=0.0, a=1.0], zoom=1.0, indexedId()=0], " +
            "View [isBase=false, order=2, name=Header, active=true, layeringEnabled=false, bounds=Rectangle: 0,0,10,100, worldPosition=Position: 0,0, clearColor=RGBColor [configId=null, r=0.0, g=0.0, b=0.0, a=1.0], zoom=1.0, indexedId()=1], " +
            "View [isBase=false, order=3, name=Body, active=true, layeringEnabled=false, bounds=Rectangle: 0,10,90,100, worldPosition=Position: 0,0, clearColor=RGBColor [configId=null, r=0.0, g=0.0, b=0.0, a=1.0], zoom=1.0, indexedId()=2]], size()=3, capacity()=3], " +
            "layersOfView=DynArray [list=[], size()=0, capacity()=0]]", 
            viewSystem.toString() 
        );
        assertEquals( 
            "TestEventDispatcher [events=[" +
            "ViewEvent [type=VIEW_CREATED, view=0], " +
            "ViewEvent [type=VIEW_CREATED, view=1], " +
            "ViewEvent [type=VIEW_CREATED, view=2]]]", 
            eventDispatcher.toString() 
        );
    }
    
    @Test
    public void testCreateLayersForBaseView() {
        Indexer.clear();
        FFContext context = createContext();
        IEventDispatcher eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
        ViewSystem viewSystem = new ViewSystem( context );
        
        try {
            viewSystem.getLayerBuilder()
                .setAttribute( Layer.VIEW_ID, ViewSystem.BASE_VIEW_ID )
                .setAttribute( Layer.NAME, "Layer1" )
                .build();
            fail( "Exception expected here because base view has layering not enabled" );
        } catch ( Exception e ) {
            assertEquals( "Layering is not enabled for view with id: 0. Enable Layering for View first", e.getMessage() );
        }
        
        viewSystem.getView( 0 ).setLayeringEnabled( true );
        
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
        
        assertEquals( 
            "ViewSystem [views=DynArray [list=[" +
            "View [isBase=true, order=-1, name=BASE_VIEW, active=true, layeringEnabled=true, bounds=Rectangle: 0,0,100,100, worldPosition=Position: 0,0, clearColor=RGBColor [configId=null, r=0.0, g=0.0, b=0.0, a=1.0], zoom=1.0, indexedId()=0]], size()=1, capacity()=1], " +
            "layersOfView=DynArray [list=[[" +
            "Layer [viewId=0, name=Layer1, indexedId=1], " +
            "Layer [viewId=0, name=Layer2, indexedId=2], " +
            "Layer [viewId=0, name=Layer3, indexedId=3]]], size()=1, capacity()=1]]", 
            viewSystem.toString() 
        );
        assertEquals( 
            "TestEventDispatcher [events=[" +
            "ViewEvent [type=VIEW_CREATED, view=0]]]", 
            eventDispatcher.toString() 
        );
        
        // try to build a layer for an inexistent view
        try {
            viewSystem.getLayerBuilder()
                .setAttribute( Layer.VIEW_ID, 100 )
                .setAttribute( Layer.NAME, "Layer1" )
                .build();
            fail( "Exception expected here" );
        } catch ( Exception e ) {
            assertEquals( "The View with id: 100. dont exists.", e.getMessage() );
        }
    }

    private FFContext createContext() {
        InitMap initMap = new FFContextImpl.InitMap();
        initMap.put( FFContext.System.EVENT_DISPATCHER, EventDispatcherMock.class );
        initMap.put( FFContext.System.LOWER_SYSTEM_FACADE, LowerSystemFacadeMock.class );
        return new FFContextImpl( initMap, true );
    }

}
