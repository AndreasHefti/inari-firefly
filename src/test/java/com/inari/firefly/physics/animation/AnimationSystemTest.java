package com.inari.firefly.physics.animation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.inari.firefly.FFTest;
import com.inari.firefly.TestTimer;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.graphics.sprite.ESprite;
import com.inari.firefly.system.UpdateEvent;

public class AnimationSystemTest extends FFTest {

    @Test
    public void testCreationWithinContext() {
        ffContext.getSystem( AnimationSystem.SYSTEM_KEY );

        Attributes attrs = new Attributes();
        ffContext.toAttributes( attrs, Animation.TYPE_KEY );

        assertEquals(
            "",
            attrs.toString()
        );
    }

    @Test
    public void testOneAnimation() {
        AnimationSystem animationSystem = ffContext.getSystem( AnimationSystem.SYSTEM_KEY );

        animationSystem.getAnimationBuilder( TestAnimation.class )
            .set( Animation.NAME, "testAnimation" )
            .set( Animation.START_TIME, 10l )
            .set( Animation.LOOPING, false )
            .build( 0 );

        Attributes attrs = new Attributes();
        ffContext.toAttributes( attrs, Animation.TYPE_KEY );

        assertEquals(
            "SystemComponent:Animation(0)::name:String=testAnimation, startTime:Long=10, looping:Boolean=false",
            attrs.toString()
        );

        assertEquals(
            "Animation{startTime=10, looping=false, active=false, finished=false}",
            animationSystem.getAnimation( 0 ).toString()
        );
    }
    
    @Test
    public void testOneAnimationWithEntityAdapter() {
        AnimationSystem animationSystem = ffContext.getSystem( AnimationSystem.SYSTEM_KEY );
        
        assertEquals( "0", String.valueOf( animationSystem.activeMappings.size() ) );

        animationSystem.getAnimationBuilder( TestIntAnimation.class )
            .set( Animation.NAME, "testAnimation" )
            .set( Animation.LOOPING, false )
        .activate( 0 );
        
        ffContext.getEntityBuilder()
            .set( EEntity.ENTITY_NAME, "TestEntity" )
            .set( ESprite.SPRITE_ID, 0 )
            .add( 
                EAnimation.ANIMATION_MAPPING, 
                ESprite.AnimationAdapter.SPRITE_ID.createAnimationMapping( "testAnimation" )
            )
        .build();
        
        ESprite sprite = ffContext.getEntityComponent( "TestEntity", ESprite.TYPE_KEY );
        
        assertNotNull( sprite );
        assertEquals( "0", String.valueOf( sprite.getSpriteId() ) );
        
        ffContext.activateEntity( "TestEntity" );
        
        assertEquals( "1", String.valueOf( animationSystem.activeMappings.size() ) );
        
        animationSystem.update( new UpdateEvent( ffContext.getTimer() ) );
        assertEquals( "1", String.valueOf( sprite.getSpriteId() ) );
        animationSystem.update( new UpdateEvent( ffContext.getTimer() ) );
        assertEquals( "2", String.valueOf( sprite.getSpriteId() ) );
        animationSystem.update( new UpdateEvent( ffContext.getTimer() ) );
        assertEquals( "3", String.valueOf( sprite.getSpriteId() ) );
        animationSystem.update( new UpdateEvent( ffContext.getTimer() ) );
        assertEquals( "4", String.valueOf( sprite.getSpriteId() ) );
    }

    @Test
    public void testUpdate() {
        AnimationSystem animationSystem = ffContext.getSystem( AnimationSystem.SYSTEM_KEY );

        animationSystem.getAnimationBuilder( TestAnimation.class )
            .set( Animation.NAME, "testAnimation" )
            .set( Animation.START_TIME, 10l )
            .set( Animation.LOOPING, false )
            .build( 0 );

        assertEquals(
            "Animation{startTime=10, looping=false, active=false, finished=false}",
            animationSystem.getAnimation( 0 ).toString()
        );

        TestTimer timer = new TestTimer();
        UpdateEvent updateEvent = new UpdateEvent( timer );
        
        timer.tick();
        animationSystem.update( updateEvent );

        assertEquals(
            "Animation{startTime=10, looping=false, active=false, finished=false}",
            animationSystem.getAnimation( 0 ).toString()
        );
        
        timer.setTime( 9 );
        animationSystem.update( updateEvent );

        assertEquals(
            "Animation{startTime=10, looping=false, active=false, finished=false}",
            animationSystem.getAnimation( 0 ).toString()
        );

        timer.setTime( 10 );
        animationSystem.update( updateEvent );

        assertEquals(
            "Animation{startTime=10, looping=false, active=true, finished=false}",
            animationSystem.getAnimation( 0 ).toString()
        );

        timer.setTime( 11 );
        animationSystem.update( updateEvent );

        // now the Animation should be active
        assertEquals(
            "Animation{startTime=10, looping=false, active=true, finished=false}",
            animationSystem.getAnimation( 0 ).toString()
        );

        // the TestAnimation is finished just after the first getValue call
        animationSystem.getValue( 0, 11, 0f );

        // now the Animation should be finished...
        assertEquals(
            "Animation{startTime=10, looping=false, active=false, finished=true}",
            animationSystem.getAnimation( 0 ).toString()
        );

        // ...and after next update be removed
        timer.setTime( 12 );
        animationSystem.update( updateEvent );
        assertFalse( animationSystem.exists( 0 ) );
    }

}
