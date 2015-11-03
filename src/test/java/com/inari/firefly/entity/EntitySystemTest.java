package com.inari.firefly.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.EventDispatcherMock;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextImpl;
import com.inari.firefly.system.FFContextImpl.InitMap;

public class EntitySystemTest {
    
    @Before
    public void init() {
        Indexer.clear();
    }
    
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
    
    @Test
    public void testCreateAndDelete() {
        FFContext testContext = getTestFFContext();
        
        EntitySystem entitySystem = new EntitySystem();
        entitySystem.init( testContext );
        
        Attributes attrs = new Attributes();
        entitySystem.toAttributes( attrs );
        
        assertEquals( 
            "ActiveEntitiesComponent(0)::ACTIVE_ENTITY_IDS:String=", 
            attrs.toString()
        );
        
        int entityId = entitySystem.getEntityBuilderWithAutoActivation()
            .set( ETransform.VIEW_ID, 1 )
            .set( ETransform.XPOSITION, 234 )
            .set( ETransform.YPOSITION, 134 )
            .set( ESprite.SPRITE_ID, 555 )
        .build().getId();
        
        assertEquals( "1", String.valueOf( entitySystem.activeEntities.size() ) );
        assertEquals( "0", String.valueOf( entitySystem.inactiveEntities.size() ) );
        assertEquals( "1", String.valueOf( entitySystem.components.size() ) );
        entitySystem.toAttributes( attrs );
        assertEquals(
            "ActiveEntitiesComponent(0)::ACTIVE_ENTITY_IDS:String=0 " +
            "Entity(0)::spriteId:Integer:ESprite=555, ordering:Integer:ESprite=0, tintColor:RGBColor:ESprite=[r=1.0,g=1.0,b=1.0,a=1.0], blendMode:BlendMode:ESprite=NONE, viewId:Integer:ETransform=1, layerId:Integer:ETransform=0, xpos:Float:ETransform=234.0, ypos:Float:ETransform=134.0, pivotx:Float:ETransform=0.0, pivoty:Float:ETransform=0.0, scalex:Float:ETransform=1.0, scaley:Float:ETransform=1.0, rotation:Float:ETransform=0.0, parentId:Integer:ETransform=-1", 
            attrs.toString()
        );
        
        
        entitySystem.delete( entityId );
        
        assertEquals( "0", String.valueOf( entitySystem.activeEntities.size() ) );
        assertEquals( "0", String.valueOf( entitySystem.inactiveEntities.size() ) );
        assertEquals( "0", String.valueOf( entitySystem.components.size() ) );
        attrs = new Attributes();
        entitySystem.toAttributes( attrs );
        assertEquals( 
            "ActiveEntitiesComponent(0)::ACTIVE_ENTITY_IDS:String=", 
            attrs.toString()
        );
    }
    
    private FFContext getTestFFContext() {
        InitMap initMap = new InitMap();
        initMap.put( FFContext.EVENT_DISPATCHER, EventDispatcherMock.class );
        initMap.put( FFContext.ENTITY_PROVIDER, EntityProvider.class );
        FFContext result = new FFContextImpl( initMap, true );
        return result;
    }

}
