package com.inari.firefly.entity;

import java.util.BitSet;

import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.indexed.IndexProvider;

public final class EntityBag {
    
    private BitSet entites;
    
    public EntityBag() {
        this( IndexProvider.getIndexedObjectSize( Entity.class ) );
    }
    
    public EntityBag( int size ) {
        entites = new BitSet( size );
    }
    
    public EntityBag( EntityBag source ) {
        entites = new BitSet();
        entites.or( source.entites );
    }
    
    public final void setEntity( int entityId ) {
        entites.set( entityId );
    }
    
    public final void removeEntity( int entityId ) {
        entites.clear( entityId );
    }
    
    public final void setEntity( Entity entity ) {
        if ( entity == null ) {
            return;
        }
        this.setEntity( entity.indexedId() );
    }
    
    public final void removeEntity( Entity entity ) {
        if ( entity == null ) {
            return;
        }
        this.removeEntity( entity.indexedId() );
    }
    
    public final void clear() {
        entites.clear();
    }
    
    public IntIterator iterator() {
        return new EntityBagIterator();
    }
    
    private final class EntityBagIterator implements IntIterator {

        private int current;
        private BitSet bitset;
        
        private EntityBagIterator() {
            // TODO should I create a clone here? good for multi-threading bad for performance?
            bitset = entites;
            current = bitset.nextSetBit( 0 );
        }
        
        @Override
        public final boolean hasNext() {
            return current >= 0;
        }

        @Override
        public final int next() {
            int result = current;
            current = bitset.nextSetBit( current + 1 );
            return result;
        }
    }


}
