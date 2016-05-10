package com.inari.firefly.physics.collision;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import com.inari.commons.geom.Rectangle;
import com.inari.firefly.FFTest;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.graphics.tile.TileGrid;
import com.inari.firefly.graphics.tile.TileGridSystem;
import com.inari.firefly.physics.movement.EMovement;
import com.inari.firefly.physics.movement.MovementSystem;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.external.FFTimer;

public class SimpleCollisionTest extends FFTest {

    private static final Rectangle WORLD_BOUNDS = new Rectangle( 0, 0, 100, 100 );
    

    
    @Test @Ignore
    public void testSimpleCollision() {

        FFTimer timer = ffContext.getTimer();
        EntitySystem entitySystem = ffContext.getSystem( EntitySystem.SYSTEM_KEY );
        MovementSystem movementSystem = ffContext.getSystem( MovementSystem.SYSTEM_KEY );
        CollisionSystem collisionSystem = ffContext.getSystem( CollisionSystem.SYSTEM_KEY );
        UpdateEvent updateEvent = new UpdateEvent( timer );
        
        collisionSystem.getCollisionQuadTreeBuilder()
            .set( CollisionQuadTree.VIEW_ID, 0 )
            .set( CollisionQuadTree.LAYER_ID, 0 )
            .set( CollisionQuadTree.MAX_ENTRIES_OF_AREA, 10 )
            .set( CollisionQuadTree.MAX_LEVEL, 5 )
            .set( CollisionQuadTree.WORLD_AREA, WORLD_BOUNDS )
        .build();
        int constraintId = collisionSystem.getCollisionConstraintBuilder()
            .set( CollisionConstraint.NAME, "Test" )
        .build( CollisionConstraintImpl.class );
            
        entitySystem.getEntityBuilder()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.LAYER_ID, 0 )
            .set( ETransform.XPOSITION, 10 )
            .set( ETransform.YPOSITION, 10 )
            .set( ECollision.BOUNDING, new Rectangle( 0, 0, 10, 10 ) )
            .set( EMovement.VELOCITY_X, 3f )
            .set( EMovement.ACTIVE, true )
        .activateAndNext()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.LAYER_ID, 0 )
            .set( ETransform.XPOSITION, 30 )
            .set( ETransform.YPOSITION, 10 )
            .set( ECollision.BOUNDING, new Rectangle( 0, 0, 10, 10 ) )
            .set( ECollision.COLLISION_CONSTRAINT_ID, constraintId )
        .activate();
        
        assertEquals( 
            "EventLog [events=["
            + "EntityActivationEvent [eventType=ENTITY_ACTIVATED, entityId=0], "
            + "EntityActivationEvent [eventType=ENTITY_ACTIVATED, entityId=1]]]", 
            eventLog.toString() 
        );
        
        timer.tick();
        movementSystem.update( updateEvent );
        
        assertEquals( 
            "EventLog [events=["
            + "EntityActivationEvent [eventType=ENTITY_ACTIVATED, entityId=0], "
            + "EntityActivationEvent [eventType=ENTITY_ACTIVATED, entityId=1], "
            + "MoveEvent [entityIds=IntBag [nullValue=-1, expand=10, size=1, length=10, array=[0, -1, -1, -1, -1, -1, -1, -1, -1, -1]]]]]", 
            eventLog.toString() 
        );
        
        timer.tick();
        movementSystem.update( updateEvent );
        timer.tick();
        movementSystem.update( updateEvent );
        timer.tick();
        movementSystem.update( updateEvent );
        
        assertEquals( 
            "EventLog [events=["
            + "EntityActivationEvent [eventType=ENTITY_ACTIVATED, entityId=0], "
            + "EntityActivationEvent [eventType=ENTITY_ACTIVATED, entityId=1], "
            + "MoveEvent [entityIds=IntBag [nullValue=-1, expand=10, size=1, length=10, array=[0, -1, -1, -1, -1, -1, -1, -1, -1, -1]]], "
            + "MoveEvent [entityIds=IntBag [nullValue=-1, expand=10, size=1, length=10, array=[0, -1, -1, -1, -1, -1, -1, -1, -1, -1]]], "
            + "MoveEvent [entityIds=IntBag [nullValue=-1, expand=10, size=1, length=10, array=[0, -1, -1, -1, -1, -1, -1, -1, -1, -1]]], "
            + "MoveEvent [entityIds=IntBag [nullValue=-1, expand=10, size=1, length=10, array=[0, -1, -1, -1, -1, -1, -1, -1, -1, -1]]], "
            + "CollisionEvent [movedEntityId=0, collidingEntityId=1, collisionIntersectionBounds=[x=8,y=0,width=2,height=10], collisionIntersectionMask=null]]]", 
            eventLog.toString() 
        );
    }
    
    @Test @Ignore
    public void testSimpleTileCollision() {
        
        FFTimer timer = ffContext.getTimer();
        ffContext.loadSystem( CollisionSystem.SYSTEM_KEY );
        EntitySystem entitySystem = ffContext.getSystem( EntitySystem.SYSTEM_KEY );
        MovementSystem movementSystem = ffContext.getSystem( MovementSystem.SYSTEM_KEY );
        TileGridSystem tileGridSystem = ffContext.getSystem( TileGridSystem.SYSTEM_KEY );
        UpdateEvent updateEvent = new UpdateEvent( timer );
        
        tileGridSystem.getTileGridBuilder()
            .set( TileGrid.CELL_WIDTH, 16 )
            .set( TileGrid.CELL_HEIGHT, 16 )
            .set( TileGrid.WORLD_XPOS, 0 )
            .set( TileGrid.WORLD_YPOS, 0 )
            .set( TileGrid.WIDTH, 10 )
            .set( TileGrid.HEIGHT, 10 )
            .set( TileGrid.VIEW_ID, 0 )
            .set( TileGrid.LAYER_ID, 0 )
        .build();

        entitySystem.getEntityBuilder()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.LAYER_ID, 0 )
            .set( ETransform.XPOSITION, 10 )
            .set( ETransform.YPOSITION, 10 )
            .set( ECollision.BOUNDING, new Rectangle( 0, 0, 10, 10 ) )
            .set( EMovement.VELOCITY_X, 3f )
            .set( EMovement.ACTIVE, true )
        .activateAndNext()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.LAYER_ID, 0 )
            .set( ETile.GRID_X_POSITION, 2 )
            .set( ETile.GRID_Y_POSITION, 1 )
            .set( ECollision.BOUNDING, new Rectangle( 0, 0, 16, 16 ) )
        .activate();
        
        assertEquals( 
            "EventLog [events=["
            + "EntityActivationEvent [eventType=ENTITY_ACTIVATED, entityId=0], "
            + "EntityActivationEvent [eventType=ENTITY_ACTIVATED, entityId=1]]]", 
            eventLog.toString() 
        );
        
        timer.tick();
        movementSystem.update( updateEvent );
        timer.tick();
        movementSystem.update( updateEvent );
        timer.tick();
        movementSystem.update( updateEvent );
        timer.tick();
        movementSystem.update( updateEvent );
        
        assertEquals( 
            "EventLog [events=["
            + "EntityActivationEvent [eventType=ENTITY_ACTIVATED, entityId=0], "
            + "EntityActivationEvent [eventType=ENTITY_ACTIVATED, entityId=1], "
            + "MoveEvent [entityIds=IntBag [nullValue=-1, expand=10, size=1, length=10, array=[0, -1, -1, -1, -1, -1, -1, -1, -1, -1]]], "
            + "MoveEvent [entityIds=IntBag [nullValue=-1, expand=10, size=1, length=10, array=[0, -1, -1, -1, -1, -1, -1, -1, -1, -1]]], "
            + "MoveEvent [entityIds=IntBag [nullValue=-1, expand=10, size=1, length=10, array=[0, -1, -1, -1, -1, -1, -1, -1, -1, -1]]], "
            + "MoveEvent [entityIds=IntBag [nullValue=-1, expand=10, size=1, length=10, array=[0, -1, -1, -1, -1, -1, -1, -1, -1, -1]]], "
            + "CollisionEvent [movedEntityId=0, collidingEntityId=1, collisionIntersectionBounds=[x=10,y=6,width=0,height=4], collisionIntersectionMask=null]]]", 
            eventLog.toString()
        );
    }

}
