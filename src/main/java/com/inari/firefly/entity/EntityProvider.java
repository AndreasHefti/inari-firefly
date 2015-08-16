package com.inari.firefly.entity;

import java.util.ArrayDeque;
import java.util.Set;

import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;

public final class EntityProvider implements FFContextInitiable  {

    final ArrayDeque<Entity> disposedEntities;
    final ArrayDeque<IndexedTypeSet> disposedComponentSets;
    final DynArray<ArrayDeque<EntityComponent>> disposedComponents;

    private int componentSetCapacity = 20;

    protected EntityProvider() {
        disposedEntities = new ArrayDeque<Entity>();
        disposedComponentSets = new ArrayDeque<IndexedTypeSet>();
        disposedComponents = new DynArray<ArrayDeque<EntityComponent>>();
    }

    @Override
    public final void init( FFContext context ) throws FFInitException {
        Integer compSetCap = context.getProperty( FFContext.Properties.ENTITY_COMPONENT_SET_CAPACITY );
        if ( compSetCap != null ) {
            componentSetCapacity = compSetCap;
        }
        if ( Indexer.getIndexedTypeSize( EntityComponent.class ) > componentSetCapacity ) {
            componentSetCapacity = Indexer.getIndexedTypeSize( EntityComponent.class );
        }

        disposedComponents.ensureCapacity( componentSetCapacity );

        for ( int i = 0; i < disposedComponents.capacity(); i++ ) {
            disposedComponents.add( new ArrayDeque<EntityComponent>() );
        }

        Integer cacheSize = context.getProperty( FFContext.Properties.ENTITY_BEANS_CACHE_SIZE );
        if ( cacheSize != null ) {
            createEntitiesForLaterUse( cacheSize );
            createComponentSetsForLaterUse( cacheSize );
            for ( int i = 0; i < Indexer.getIndexedTypeSize( EntityComponent.class ); i++ ) {
                Class<? extends EntityComponent> componentType = Indexer.getTypeForIndex( EntityComponent.class, i );
                createComponentsForLaterUse( cacheSize, componentType );
            }
        }
    }

    @Override
    public final void dispose( FFContext context ) {
        disposedEntities.clear();
        disposedComponentSets.clear();
        disposedComponents.clear();
    }
    
    public final Entity getEntity() {
        return getEntity( -1 );
    }

    public final Entity getEntity( int entityId ) {
        if ( disposedEntities.isEmpty() ) {
            return new Entity( entityId );
        }
        
        Entity result = disposedEntities.pop();
        if ( entityId < 0 ) {
            entityId = Indexer.nextObjectIndex( Entity.class );
        }
        result.setId( entityId );
        return result;
    }
    
    public <T extends EntityComponent> T getComponent( Class<T> componentType ) {
        int componentTypeId = Indexer.getIndexForType( componentType, EntityComponent.class );
        ArrayDeque<EntityComponent> componentsOfType = disposedComponents.get( componentTypeId );
        T component;
        if ( componentsOfType.isEmpty() ) {
            component = newComponent( componentType );
        } else {
            component = componentType.cast( componentsOfType.pop() );
        }

        return component;
    }
    
    public final void createEntitiesForLaterUse( int number ) {
        for ( int i = 0; i < number; i++ ) {
            disposedEntities.add( new Entity() );
        }
    }
    
    public final void createComponentSetsForLaterUse( int number ) {
        for ( int i = 0; i < number; i++ ) {
            disposedComponentSets.add( 
                new IndexedTypeSet( 
                    EntityComponent.class, 
                    componentSetCapacity 
                ) 
            );
        }
    }
    
    public final void createComponentsForLaterUse( int number, Class<? extends EntityComponent> componentType ) {
        int componentTypeId = Indexer.getIndexForType( componentType, EntityComponent.class );
        ArrayDeque<EntityComponent> componentsOfType = disposedComponents.get( componentTypeId );
        if ( componentsOfType == null ) {
            componentsOfType = new ArrayDeque<EntityComponent>();
            disposedComponents.set( componentTypeId, componentsOfType );
        }
        
        for ( int i = 0; i < number; i++ ) {
            componentsOfType.add( newComponent( componentType ) );
        }
    }

    public IndexedTypeSet getComponentTypeSet() {
        if ( disposedComponentSets.isEmpty() ) {
            return new IndexedTypeSet( EntityComponent.class, componentSetCapacity );
        }
        
        return disposedComponentSets.pop();
    }

    void dispose( Entity entity, IndexedTypeSet components ) {
        entity.dispose();
        disposedEntities.add( entity );

        disposeComponentSet( components );
    }

    void disposeComponentSet( IndexedTypeSet components ) {
        for ( int i = 0; i < components.length(); i++ ) {
            EntityComponent component = components.get( i );
            if ( component != null ) {
                ArrayDeque<EntityComponent> componentsOfType = disposedComponents.get( i );
                if ( componentsOfType == null ) {
                    componentsOfType = new ArrayDeque<EntityComponent>();
                    disposedComponents.set( i, componentsOfType );
                }
                componentsOfType.add( component );
            }
        }

        components.clear();
        disposedComponentSets.add( components );
    }

    void createComponents( IndexedTypeSet components, EntityAttributeMap attributes ) {
        Set<Class<? extends EntityComponent>> componentTypes = attributes.getEntityComponentTypes();
        for ( Class<? extends EntityComponent> componentType : componentTypes ) {
            EntityComponent component = getComponent( componentType );
            component.fromAttributes( attributes );
            components.set( component );
        }
    }

    private <C extends EntityComponent> C newComponent( Class<C> componentType ) {
        try {
            return componentType.newInstance();
        } catch ( Exception e ) {
            throw new ComponentCreationException( "Unknwon error while Component creation: " + componentType, e );
        }
    }

}
