package com.inari.firefly.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.sound.SoundController;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;

public class ControllerSystemTest {
    
    @Test
    public void testGetComponentBuilder() {
        ControllerSystem controllerSystem = new ControllerSystem();
        
        ComponentBuilder<TestController> componentBuilder = controllerSystem.getComponentBuilder( ControllerSystemTest.TestController.class );
        assertNotNull( componentBuilder );
        assertEquals( 
            "com.inari.firefly.control.ControllerSystem$ControllerBuilder", 
            componentBuilder.getClass().getName() 
        );
        
        ComponentBuilder<SoundController> componentBuilder1 = controllerSystem.getComponentBuilder( SoundController.class );
        assertNotNull( componentBuilder1 );
        assertEquals( 
            "com.inari.firefly.control.ControllerSystem$ControllerBuilder", 
            componentBuilder1.getClass().getName() 
        );
        
        try {
            controllerSystem.getComponentBuilder( String.class );
            fail( "Exception expected here" );
        } catch ( Exception e ) {
            assertEquals( 
                "Unsupported Component type for ControllerSystem Builder. Type: class java.lang.String", 
                e.getMessage() 
            );
        }
    }
    
    public class TestController extends Controller {

        protected TestController( int id ) {
            super( id );
        }

        @Override
        public void dispose( FFContext context ) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void update( final FFTimer timer ) {
            // TODO Auto-generated method stub
            
        }
        
    }

}
