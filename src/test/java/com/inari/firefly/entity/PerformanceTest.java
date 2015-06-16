package com.inari.firefly.entity;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.commons.lang.indexed.IndexedTypeMap;
import com.inari.commons.lang.list.DynArray;

public class PerformanceTest {
    
    private static final int NUMBER_OF_ENTITIES = 100000;
    
    @Test
    public void testListCapacity() {
        ArrayList<String> list = new ArrayList<String>();
        list.add( "1" );
        list.add( "2" );
        list.add( "3" );
        list.add( "4" );
        list.add( "5" );
        
        assertEquals( 5, list.size() );
        
        list.set( 3, null );
        
        assertEquals( 5, list.size() );
    }

//    //@Test
//    public void testGetComponentPerformance() {
//        Old_EntityProvider provider = new Old_EntityProvider(
//            new IFFContext() {
//                @Override
//                public <T> T get( TypedKey<T> key ) {
//                    return null;
//                }
//            }
//        );
//        
//        provider.ensureCapacity( NUMBER_OF_ENTITIES );
//        
//        EntityBag entities = new EntityBag();
//        for ( int i = 0; i < NUMBER_OF_ENTITIES; i++ ) {
//            Entity entity = provider.newEntity().addComponent( new CPosition() ).build();
//            entities.setEntity( entity.id() );
//        }
//        
//        long startTime = System.currentTimeMillis();
//        IIntIterator entityIterator = entities.iterator();
//        while( entityIterator.hasNext() ) {
//            CPosition result = provider.getComponent( entityIterator.next(), CPosition.class );
//        }
//        long endTime = System.currentTimeMillis();
//        
//        System.out.println( "for " + NUMBER_OF_ENTITIES + " Components to get: " + ( endTime - startTime ) );
//    }
    
//    @Test
//    public void testGetComponentPerformance2() {
//        EntityProvider provider = new EntityProvider(
//            new IFFContext() {
//                @Override
//                public <T> T get( TypedKey<T> key ) {
//                    return null;
//                }
//            },
//            NUMBER_OF_ENTITIES
//        );
//        
//        //provider.ensureCapacity( NUMBER_OF_ENTITIES );
//        
//        EntityBag entities = new EntityBag();
//        for ( int i = 0; i < NUMBER_OF_ENTITIES; i++ ) {
//            Entity entity = provider.newEntity().addComponent( new CTransform() ).build();
//            entities.setEntity( entity.id() );
//        }
//        
//        long startTime = System.currentTimeMillis();
//        IntIterator entityIterator = entities.iterator();
//        int componentIndex = IndexProvider.getIndex( CTransform.class, EntityComponent.class );
//        while( entityIterator.hasNext() ) {
//            CTransform result = provider.getComponent( entityIterator.next(), componentIndex );
//        }
//        long endTime = System.currentTimeMillis();
//        
//        System.out.println( "for " + NUMBER_OF_ENTITIES + " Components to get: " + ( endTime - startTime ) );
//    }
    
    @Test
    public void testGetComponentPerformanceIndexedTypeMapDynArrayFacade() {
        IndexedTypeMapDynArrayFacade comps = new IndexedTypeMapDynArrayFacade();
        EntityBag entities = new EntityBag();
        for ( int i = 0; i < NUMBER_OF_ENTITIES; i++ ) {
            comps.put( i, new ETransform() );
            entities.setEntity( i );
        }
        
        long startTime = System.currentTimeMillis();
        IntIterator entityIterator = entities.iterator();
        int componentIndex = Indexer.getIndexForType( ETransform.class, EntityComponent.class );
        while( entityIterator.hasNext() ) {
            ETransform result = comps.get( entityIterator.next(), componentIndex );
        }
        entityIterator = entities.iterator();
        while( entityIterator.hasNext() ) {
            entityIterator.next();
            EntityComponent remove = comps.remove( 4, componentIndex );
            comps.put( 4, remove );
        }
        long endTime = System.currentTimeMillis();;
        
        System.out.println( "for " + NUMBER_OF_ENTITIES + " Components to get: " + ( endTime - startTime ) );
    }
    
    @Test
    public void testGetComponentPerformanceArrayList() {
        MapListFacade comps = new MapListFacade();
        EntityBag entities = new EntityBag();
        for ( int i = 0; i < NUMBER_OF_ENTITIES; i++ ) {
            comps.put( i, new ETransform() );
            entities.setEntity( i );
        }
        
        long startTime = System.currentTimeMillis();
        IntIterator entityIterator = entities.iterator();
        int componentIndex = Indexer.getIndexForType( ETransform.class, EntityComponent.class );
        while( entityIterator.hasNext() ) {
            ETransform result = comps.get( entityIterator.next(), componentIndex );
        }
        entityIterator = entities.iterator();
        while( entityIterator.hasNext() ) {
            entityIterator.next();
            EntityComponent remove = comps.remove( 4, componentIndex );
            comps.put( 4, remove );
        }
        long endTime = System.currentTimeMillis();;
        
        System.out.println( "JavaCollections: for " + NUMBER_OF_ENTITIES + " Components to get: " + ( endTime - startTime ) );
    }
    
    private class IndexedTypeMapDynArrayFacade {
        
        private IndexedTypeMap<DynArray<EntityComponent>> map = new IndexedTypeMap<DynArray<EntityComponent>>(
                EntityComponent.class,
                DynArray.class
        );
        
        public void put( int entityId, EntityComponent component ) {
            DynArray<EntityComponent> list = map.getValue( component.index() );
            if( list == null ) {
                list = new DynArray<EntityComponent>();
                map.put( component.index(), list );
            }
            list.set( entityId, component );
        }
        
        public <C extends EntityComponent> C get( int entityId, int componentIndex ) {
            DynArray<EntityComponent> list = map.getValue( componentIndex );
            return (C) list.get( entityId );
        }
        
        public <C extends EntityComponent> C remove( int entityId, int componentIndex ) {
            DynArray<EntityComponent> list = map.getValue( componentIndex );
            if ( list != null ) {
                return (C) list.remove( entityId );
            }
            return null;
        }
    }
    
    private class MapListFacade {
        
        private Map<Integer, ArrayList<EntityComponent>> map = new HashMap<Integer, ArrayList<EntityComponent>>();
        
        public void put( int entityId, EntityComponent component ) {
            ArrayList<EntityComponent> list = map.get( component.index() );
            if( list == null ) {
                list = new ArrayList<EntityComponent>( NUMBER_OF_ENTITIES );
                map.put( component.index(), list );
            }
            list.add( entityId, component );
        }
        
        public <C extends EntityComponent> C get( int entityId, int componentIndex ) {
            ArrayList<EntityComponent> list = map.get( componentIndex );
            return (C) list.get( entityId );
        }
        
        public <C extends EntityComponent> C remove( int entityId, int componentIndex ) {
            ArrayList<EntityComponent> list = map.get( componentIndex );
            if ( list != null ) {
                return (C) list.remove( entityId );
            }
            return null;
        }
    }

}
