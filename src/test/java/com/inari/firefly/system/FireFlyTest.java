package com.inari.firefly.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.inari.firefly.LowerSystemFacadeMock;
import com.inari.firefly.component.attr.Attributes;

public class FireFlyTest {
    
    @Test
    public void testCreation() {
        FireFly firefly = new FireFly( LowerSystemFacadeMock.class );
        Attributes attrs = new Attributes();
        
        FFContext context = firefly.getContext();
        
        assertNotNull( context );
        context.toAttributes( attrs );
        assertEquals( 
            "View(0)::" +
                "name:String=BASE_VIEW, " +
                "order:Integer=-1, " +
                "active:Boolean=true, " +
                "bounds:Rectangle=[x=0,y=0,width=100,height=100], " +
                "worldPosition:Position=[x=0,y=0], " +
                "clearColor:RGBColor=[r=0.0,g=0.0,b=0.0,a=1.0], " +
                "layeringEnabled:Boolean=false, " +
                "zoom:Float=1.0 " +
            "ActiveEntitiesComponent(0)::" +
                "ACTIVE_ENTITY_IDS:String=", 
            attrs.toString() 
        );
    }

}
