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
import com.inari.firefly.system.external.FFTimer;

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

        ffContext.getComponentBuilder( Animation.TYPE_KEY, TestAnimation.class )
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
            animationSystem.animations.get( 0 ).toString()
        );
    }
    
    @Test
    public void testOneAnimationWithEntityAdapter() {
        AnimationSystem animationSystem = ffContext.getSystem( AnimationSystem.SYSTEM_KEY );
        FFTimer timer = ffContext.getTimer();
        
        assertEquals( "0", String.valueOf( animationSystem.activeMappings.size() ) );

        ffContext.getComponentBuilder( Animation.TYPE_KEY, TestIntAnimation.class )
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
        
        animationSystem.update( timer );
        assertEquals( "1", String.valueOf( sprite.getSpriteId() ) );
        animationSystem.update( timer );
        assertEquals( "2", String.valueOf( sprite.getSpriteId() ) );
        animationSystem.update( timer );
        assertEquals( "3", String.valueOf( sprite.getSpriteId() ) );
        animationSystem.update( timer );
        assertEquals( "4", String.valueOf( sprite.getSpriteId() ) );
    }

    @Test
    public void testUpdate() {
        AnimationSystem animationSystem = ffContext.getSystem( AnimationSystem.SYSTEM_KEY );

        ffContext.getComponentBuilder( Animation.TYPE_KEY, TestAnimation.class )
            .set( Animation.NAME, "testAnimation" )
            .set( Animation.START_TIME, 10l )
            .set( Animation.LOOPING, false )
            .build( 0 );

        assertEquals(
            "Animation{startTime=10, looping=false, active=false, finished=false}",
            animationSystem.animations.get( 0 ).toString()
        );

        TestTimer timer = new TestTimer();
        
        timer.tick();
        animationSystem.update( timer );

        assertEquals(
            "Animation{startTime=10, looping=false, active=false, finished=false}",
            animationSystem.animations.get( 0 ).toString()
        );
        
        timer.setTime( 9 );
        animationSystem.update( timer );

        assertEquals(
            "Animation{startTime=10, looping=false, active=false, finished=false}",
            animationSystem.animations.get( 0 ).toString()
        );

        timer.setTime( 10 );
        animationSystem.update( timer );

        assertEquals(
            "Animation{startTime=10, looping=false, active=true, finished=false}",
            animationSystem.animations.get( 0 ).toString()
        );

        timer.setTime( 11 );
        animationSystem.update( timer );

        // now the Animation should be active
        assertEquals(
            "Animation{startTime=10, looping=false, active=true, finished=false}",
            animationSystem.animations.get( 0 ).toString()
        );

        // the TestAnimation is finished just after the first getValue call
        animationSystem.getValue( 0, 11, 0f );

        // now the Animation should be finished...
        assertEquals(
            "Animation{startTime=10, looping=false, active=false, finished=true}",
            animationSystem.animations.get( 0 ).toString()
        );

        // ...and after next update be removed
        timer.setTime( 12 );
        animationSystem.update( timer );
        assertFalse( animationSystem.animations.map.contains( 0 ) );
    }

}
