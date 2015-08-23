package com.inari.firefly.animation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.EventDispatcherMock;
import com.inari.firefly.TestTimer;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextImpl;
import com.inari.firefly.system.UpdateEvent;

public class AnimationSystemTest {
    
    @Before
    public void init() {
        Indexer.clear();
    }

    @Test
    public void testCreation() {
        FFContext ffContext = getTestFFContext();
        AnimationSystem animationSystem = new AnimationSystem();
        animationSystem.init( ffContext );

        Attributes attrs = new Attributes();
        animationSystem.toAttributes( attrs );

        assertEquals(
            "",
            attrs.toString()
        );
    }

    @Test
    public void testOneAnimation() {
        FFContext ffContext = getTestFFContext();
        AnimationSystem animationSystem = new AnimationSystem();
        animationSystem.init( ffContext );

        animationSystem.getAnimationBuilder( TestAnimation.class )
            .setAttribute( Animation.NAME, "testAnimation" )
            .setAttribute( Animation.START_TIME, 10l )
            .setAttribute( Animation.LOOPING, false )
            .build( 0 );

        Attributes attrs = new Attributes();
        animationSystem.toAttributes( attrs );

        assertEquals(
            "Animation(0)::name:String=testAnimation, startTime:Long=10, looping:Boolean=false",
            attrs.toString()
        );

        assertEquals(
            "Animation{startTime=10, looping=false, active=false, finished=false}",
            animationSystem.getAnimation( 0 ).toString()
        );
    }

    @Test
    public void testUpdate() {
        FFContext ffContext = getTestFFContext();
        AnimationSystem animationSystem = new AnimationSystem();
        animationSystem.init( ffContext );

        animationSystem.getAnimationBuilder( TestAnimation.class )
            .setAttribute( Animation.NAME, "testAnimation" )
            .setAttribute( Animation.START_TIME, 10l )
            .setAttribute( Animation.LOOPING, false )
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

    private FFContext getTestFFContext() {
        FFContextImpl.InitMap initMap = new FFContextImpl.InitMap();
        initMap.put( FFContext.EVENT_DISPATCHER, EventDispatcherMock.class );
        FFContext result = new FFContextImpl( initMap, true );
        return result;
    }
}
