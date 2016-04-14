package com.inari.firefly.physics.animation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.inari.firefly.FFTest;
import com.inari.firefly.TestTimer;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.physics.animation.Animation;
import com.inari.firefly.physics.animation.AnimationSystem;
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

        animationSystem.getAnimationBuilder()
            .set( Animation.NAME, "testAnimation" )
            .set( Animation.START_TIME, 10l )
            .set( Animation.LOOPING, false )
            .build( 0, TestAnimation.class );

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
    public void testUpdate() {
        AnimationSystem animationSystem = ffContext.getSystem( AnimationSystem.SYSTEM_KEY );

        animationSystem.getAnimationBuilder(  )
            .set( Animation.NAME, "testAnimation" )
            .set( Animation.START_TIME, 10l )
            .set( Animation.LOOPING, false )
            .build( 0, TestAnimation.class );

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
