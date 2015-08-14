package com.inari.firefly.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.inari.firefly.EventDispatcherMock;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextImpl;
import com.inari.firefly.system.FFContextImpl.InitMap;

public class EntitySystemTest {
    
    @Test
    public void testCreation() {
        FFContext testContext = getTestFFContext();
        
        EntitySystem entitySystem = new EntitySystem();
        entitySystem.init( testContext );
        
        Attributes attrs = new Attributes();
        entitySystem.toAttributes( attrs );
        
        assertEquals( 
            "ActiveEntitiesComponent(0)::ACTIVE_ENTITY_IDS:String=", 
            attrs.toString()
        );
        assertEquals( "1001", String.valueOf( entitySystem.activeEntities.capacity() ) );
        assertEquals( "0", String.valueOf( entitySystem.activeEntities.size() ) );
        assertEquals( "1001", String.valueOf( entitySystem.inactiveEntities.capacity() ) );
        assertEquals( "0", String.valueOf( entitySystem.inactiveEntities.size() ) );
        assertEquals( "1001", String.valueOf( entitySystem.components.capacity() ) );
        assertEquals( "0", String.valueOf( entitySystem.components.size() ) );
        
        
        entitySystem = new EntitySystem();
        entitySystem.init( testContext );
        
        attrs = new Attributes();
        entitySystem.toAttributes( attrs );
        
        assertEquals( 
            "ActiveEntitiesComponent(0)::ACTIVE_ENTITY_IDS:String=", 
            attrs.toString()
        );
    }
    
    @Test
    public void testCreationWith() {
        FFContext testContext = getTestFFContext();
        
        EntitySystem entitySystem = new EntitySystem();
        entitySystem.init( testContext );
        
        Attributes attrs = new Attributes();
        entitySystem.toAttributes( attrs );
        
        assertEquals( 
            "ActiveEntitiesComponent(0)::ACTIVE_ENTITY_IDS:String=", 
            attrs.toString()
        );
        

    }
    
    private FFContext getTestFFContext() {
        InitMap initMap = new InitMap();
        initMap.put( FFContext.EVENT_DISPATCHER, EventDispatcherMock.class );
        FFContext result = new FFContextImpl( initMap, true );
        return result;
    }

}
