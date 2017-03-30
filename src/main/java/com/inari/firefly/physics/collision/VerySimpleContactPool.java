package com.inari.firefly.physics.collision;

import java.util.ArrayDeque;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.list.IntBag;

/** This is a very simple ContactPool implementation that just collect the id's of all Entities of View and Layer.
 *  And gives them all back for iteration on get.
 *  
 *  Use this if you just have a view Entities that can collide (having a ECollision component) either if they are static 
 *  or none static Entities.
 *
 */
public final class VerySimpleContactPool extends ContactPool {
    
    private final IntBag entities = new IntBag( 50, -1 );
    
    private final ArrayDeque<EntityIdIterator> iteratorPool;

    protected VerySimpleContactPool( int index ) {
        super( index );
        
        iteratorPool = new ArrayDeque<EntityIdIterator>( 2 );
        iteratorPool.add( new EntityIdIterator() );
        iteratorPool.add( new EntityIdIterator() );
    }

    @Override
    public final void add( int entityId ) {
        if ( !entities.contains( entityId ) ) {
            entities.add( entityId );
        }
    }

    @Override
    public final void remove( int entityId ) {
        entities.remove( entityId );
    }

    /** This does nothing for this implementation of ContactPool */
    @Override
    public final void update( int entityId ) {
        // not needed here
    }

    /** Get an IntIterator of all registered entity id's */
    @Override
    public final IntIterator get( Rectangle region ) {
        if ( iteratorPool.isEmpty() ) {
            iteratorPool.add( new EntityIdIterator()  );
        }
        
        EntityIdIterator it = iteratorPool.pop();
        it.reset();
        return it;
    }

    @Override
    public final void clear() {
        entities.clear();
    }
    
    private final class EntityIdIterator implements IntIterator {
        
        int index;
        
        @Override
        public final boolean hasNext() {
            return index >= 0;
        }

        @Override
        public final int next() {
            int result = entities.get( index );
            findNext();
            return result;
        }
        
        final void findNext() {
            index++;
            while( index < entities.length() && entities.isEmpty( index ) ) {
                index++;
            }
            
            if ( index == entities.length() ) {
                index = -1;
                iteratorPool.add( this );
            }
        }
        
        final void reset() {
            index = -1;
            findNext();
        }
        
    }

}
