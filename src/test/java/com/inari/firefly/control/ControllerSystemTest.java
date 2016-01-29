package com.inari.firefly.control;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.inari.firefly.FFTest;
import com.inari.firefly.component.attr.Attributes;

public class ControllerSystemTest extends FFTest {
    
    @Test
    public void testCreationWithinContext() {
        ffContext.loadSystem( ControllerSystem.SYSTEM_KEY );
        
        Attributes attrs = new Attributes();
        ffContext.toAttributes( attrs, Controller.TYPE_KEY );
        
        assertEquals( 
            "", 
            attrs.toString()
        );
    }
    

}
