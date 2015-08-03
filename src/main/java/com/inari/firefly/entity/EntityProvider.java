package com.inari.firefly.entity;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Set;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.system.FFComponent;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;

public class EntityProvider implements FFComponent  {

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
    public void init( FFContext context ) throws FFInitException {
        Integer compSetCap = context.getProperty( FFContext.System.Properties.ENTITY_COMPONENT_SET_CAPACITY );
        if ( compSetCap != null ) {
            componentSetCapacity = compSetCap;
        }

        disposedComponents.ensureCapacity( componentSetCapacity );

        for ( int i = 0; i < disposedComponents.capacity(); i++ ) {
            disposedComponents.add( new ArrayDeque<EntityComponent>() );
        }
    }

    @Override
    public void dispose( FFContext context ) {
        disposedEntities.clear();
        disposedComponentSets.clear();
        disposedComponents.clear();
    }

    Entity getEntity( int entityId ) {
        Entity result =disposedEntities.pop();
        if ( result != null ) {
            if ( entityId < 0 ) {
                entityId = Indexer.nextObjectIndex( Entity.class );
            }
            result.setId( entityId );
        }

        return result;
    }

    IndexedTypeSet getComponentTypeSet() {
        return disposedComponentSets.pop();
    }

    void dispose( Entity entity, IndexedTypeSet components ) {
        entity.dispose();
        disposedEntities.add( entity );

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
            EntityComponent component = newComponent( componentType, attributes );
            components.set( component );
        }
    }

    private <C extends EntityComponent> EntityComponent newComponent( Class<C> componentType ) {
        int componentTypeIndex = Indexer.getIndexForType( componentType, EntityComponent.class );

        ArrayDeque<EntityComponent> componentsOfType = disposedComponents.get( componentTypeIndex );
        EntityComponent result = componentsOfType.pop();
        if ( result != null ) {
            return result;
        }

        return createComponent( componentType );
    }

    private <C extends EntityComponent> EntityComponent newComponent( Class<C> componentType, AttributeMap attributes ) {
        EntityComponent newComponent = newComponent( componentType );
        newComponent.fromAttributes( attributes );
        return newComponent;
    }

    private EntityComponent createComponent( Class<? extends EntityComponent> componentType ) {
        try {
            return componentType.newInstance();
        } catch ( Exception e ) {
            throw new ComponentCreationException( "Unknwon error while Component creation: " + componentType, e );
        }
    }

}
