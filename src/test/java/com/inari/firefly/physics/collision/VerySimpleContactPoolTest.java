package com.inari.firefly.physics.collision;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.IntIterator;
import com.inari.firefly.FFTest;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.sprite.ESprite;

public class VerySimpleContactPoolTest extends FFTest {
    
    @Test
    public void test() {
        ffContext.loadSystem( CollisionSystem.SYSTEM_KEY );
        
        int e1 = ffContext.getEntityBuilder()
            .set( ETransform.VIEW_ID, 1 )
            .set( ESprite.SPRITE_ID, 1 )
            .set( ECollision.COLLISION_BOUNDS, new Rectangle( 0, 0, 10, 10 ) )
            .activate();
        int e2 = ffContext.getEntityBuilder()
            .set( ETransform.VIEW_ID, 1 )
            .set( ESprite.SPRITE_ID, 2 )
            .set( ECollision.COLLISION_BOUNDS, new Rectangle( 0, 0, 10, 10 ) )
            .activate();
        int e3 = ffContext.getEntityBuilder()
            .set( ETransform.VIEW_ID, 1 )
            .set( ESprite.SPRITE_ID, 3 )
            .set( ECollision.COLLISION_BOUNDS, new Rectangle( 0, 0, 10, 10 ) )
            .activate();
        
        int id = ffContext.getComponentBuilder( ContactPool.TYPE_KEY, VerySimpleContactPool.class )
            .set( ContactPool.VIEW_ID, 1 )
            .build();
        
        VerySimpleContactPool contactPool = ffContext.getSystemComponent( ContactPool.TYPE_KEY, id, VerySimpleContactPool.class );
        contactPool.add( e1 );
        contactPool.add( e2 );
        contactPool.add( e3 );
        
        IntIterator intIterator = contactPool.get( null );
        assertTrue( intIterator.hasNext() );
        assertEquals( "0", String.valueOf( intIterator.next() ) );
        assertTrue( intIterator.hasNext() );
        assertEquals( "1", String.valueOf( intIterator.next() ) );
        assertTrue( intIterator.hasNext() );
        assertEquals( "2", String.valueOf( intIterator.next() ) );
        assertFalse( intIterator.hasNext() );
        
        intIterator = contactPool.get( null );
        assertTrue( intIterator.hasNext() );
        assertEquals( "0", String.valueOf( intIterator.next() ) );
        assertTrue( intIterator.hasNext() );
        assertEquals( "1", String.valueOf( intIterator.next() ) );
        assertTrue( intIterator.hasNext() );
        assertEquals( "2", String.valueOf( intIterator.next() ) );
        assertFalse( intIterator.hasNext() );
        
        contactPool.remove( e2 );
        
        intIterator = contactPool.get( null );
        assertTrue( intIterator.hasNext() );
        assertEquals( "0", String.valueOf( intIterator.next() ) );
        assertTrue( intIterator.hasNext() );
        assertEquals( "2", String.valueOf( intIterator.next() ) );
        assertFalse( intIterator.hasNext() );
        
        contactPool.add( e1 );
        
        intIterator = contactPool.get( null );
        assertTrue( intIterator.hasNext() );
        assertEquals( "0", String.valueOf( intIterator.next() ) );
        assertTrue( intIterator.hasNext() );
        assertEquals( "2", String.valueOf( intIterator.next() ) );
        assertFalse( intIterator.hasNext() );
        
        contactPool.clear();
        intIterator = contactPool.get( null );
        assertFalse( intIterator.hasNext() );
    }

}
