package com.inari.firefly.physics.collision;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.inari.commons.geom.Rectangle;
import com.inari.firefly.FFTest;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;

public class CollisionQuadTreeTest extends FFTest {
    
    @Test
    public void testCreateAndAdd() {
        EntitySystem entitySystem = ffContext.getSystem( EntitySystem.SYSTEM_KEY );
        
        String entity1 = createEntity( 10, 10, entitySystem );
        String entity2 = createEntity( 60, 10, entitySystem );
        String entity3 = createEntity( 10, 60, entitySystem );
        String entity4 = createEntity( 60, 60, entitySystem );
        String entity5 = createEntity( 70, 70, entitySystem );
        
        CollisionQuadTree quadTree = new CollisionQuadTree( 0 );
        quadTree.injectContext( ffContext );
        quadTree.init();
        quadTree.setMaxEntities( 4 );
        quadTree.setMaxLevel( 4 );
        quadTree.setWorldArea( new Rectangle( 0, 0, 100, 100 ) );
        assertEquals( 
            "EntityCollisionQuadTree: maxEntities=4 maxLevel=4[\n" + 
            "  Node [level=0, area=[x=0,y=0,width=100,height=100], entities=IntBag [nullValue=-1, expand=10, size=0, length=5, array=[-1, -1, -1, -1, -1]]]\n" + 
            "]", 
            quadTree.toString() 
        );
        
        quadTree.add( entitySystem.getEntityId( entity1 ) );
        assertEquals( 
            "EntityCollisionQuadTree: maxEntities=4 maxLevel=4[\n" + 
            "  Node [level=0, area=[x=0,y=0,width=100,height=100], entities=IntBag [nullValue=-1, expand=10, size=1, length=5, array=[0, -1, -1, -1, -1]]]\n" + 
            "]", 
            quadTree.toString() 
        );
        
        quadTree.add( entitySystem.getEntityId( entity2 ) );
        assertEquals( 
            "EntityCollisionQuadTree: maxEntities=4 maxLevel=4[\n" + 
            "  Node [level=0, area=[x=0,y=0,width=100,height=100], entities=IntBag [nullValue=-1, expand=10, size=2, length=5, array=[0, 1, -1, -1, -1]]]\n" + 
            "]", 
            quadTree.toString() 
        );
        
        quadTree.add( entitySystem.getEntityId( entity3 ) );
        assertEquals( 
            "EntityCollisionQuadTree: maxEntities=4 maxLevel=4[\n" + 
            "  Node [level=0, area=[x=0,y=0,width=100,height=100], entities=IntBag [nullValue=-1, expand=10, size=3, length=5, array=[0, 1, 2, -1, -1]]]\n" + 
            "]", 
            quadTree.toString() 
        );
        
        quadTree.add( entitySystem.getEntityId( entity4 ) );
        assertEquals( 
            "EntityCollisionQuadTree: maxEntities=4 maxLevel=4[\n" + 
            "  Node [level=0, area=[x=0,y=0,width=100,height=100], entities=IntBag [nullValue=-1, expand=10, size=4, length=5, array=[0, 1, 2, 3, -1]]]\n" + 
            "]", 
            quadTree.toString() 
        );
        
        quadTree.add( entitySystem.getEntityId( entity5 ) );
        assertEquals( 
            "EntityCollisionQuadTree: maxEntities=4 maxLevel=4[\n" + 
            "  Node [level=0, area=[x=0,y=0,width=100,height=100], entities=IntBag [nullValue=-1, expand=10, size=5, length=5, array=[-1, -1, -1, -1, -1]]]\n" + 
            "    Node [level=1, area=[x=0,y=0,width=50,height=50], entities=IntBag [nullValue=-1, expand=10, size=1, length=5, array=[0, -1, -1, -1, -1]]]\n" + 
            "    Node [level=1, area=[x=50,y=0,width=50,height=50], entities=IntBag [nullValue=-1, expand=10, size=1, length=5, array=[1, -1, -1, -1, -1]]]\n" + 
            "    Node [level=1, area=[x=50,y=50,width=50,height=50], entities=IntBag [nullValue=-1, expand=10, size=2, length=5, array=[3, 4, -1, -1, -1]]]\n" + 
            "    Node [level=1, area=[x=0,y=50,width=50,height=50], entities=IntBag [nullValue=-1, expand=10, size=1, length=5, array=[2, -1, -1, -1, -1]]]\n" + 
            "]", 
            quadTree.toString() 
        );
    }
    
    private String createEntity( int x, int y, EntitySystem entitySystem ) {
        String name = "Entity("+x+","+y+")"; 
        entitySystem.getEntityBuilder()
            .set( EEntity.ENTITY_NAME, name )
            .set( ETransform.XPOSITION, x )
            .set( ETransform.YPOSITION, y )
            .set( ECollision.BOUNDING, new Rectangle( 0, 0, 10, 10 ) )
        .activate();
        
        return name;
    }

}
