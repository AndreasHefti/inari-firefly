package com.inari.firefly.control;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.FireFlyMock;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.system.FFContext;

public class ControllerSystemTest {
    
    @Test
    public void testCreationWithinContext() {
        Indexer.clear();
        FFContext ffContext = new FireFlyMock().getContext();
        ffContext.getSystem( ControllerSystem.SYSTEM_KEY );
        
        Attributes attrs = new Attributes();
        ffContext.toAttributes( attrs, Controller.TYPE_KEY );
        
        assertEquals( 
            "", 
            attrs.toString()
        );
    }
    

}
