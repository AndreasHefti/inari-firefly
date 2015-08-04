package com.inari.firefly.entity;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Set;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.Disposable;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.entity.event.EntityPrefabActionEvent;
import com.inari.firefly.entity.event.EntityPrefabActionListener;
import com.inari.firefly.system.FFComponent;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;

public class EntityPrefabSystem implements FFComponent, EntityPrefabActionListener, ComponentSystem, ComponentBuilderFactory, Disposable {
    
    private DynArray<EntityPrefab> prefabs;
    private DynArray<String> prefabNames;
    private DynArray<IndexedTypeSet> prefabComponents;
    private DynArray<ArrayDeque<IndexedTypeSet>> components;
    
    private EntitySystem entitySystem;
    private EntityProvider entityProvider;
    private IEventDispatcher eventDispatcher;
    
    private final EntityAttributeMap attributeMap;
    
    
    EntityPrefabSystem() {
        attributeMap = new EntityAttributeMap();
    }
    
    @Override
    public void init( FFContext context ) throws FFInitException {
        prefabs = new DynArray<EntityPrefab>();
        prefabNames = new DynArray<String>();
        prefabComponents = new DynArray<IndexedTypeSet>();
        
        entitySystem = context.getComponent( FFContext.Systems.ENTITY_SYSTEM );
        entityProvider = context.getComponent( FFContext.ENTITY_PROVIDER );
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );

        eventDispatcher.register( EntityPrefabActionEvent.class, this );
    }
    
    @Override
    public void dispose( FFContext context ) {
        for ( EntityPrefab prefab : prefabs ) {
            prefab.dispose();
        }
        
        prefabNames.clear();
        prefabComponents.clear();

        eventDispatcher.unregister( EntityPrefabActionEvent.class, this );
    }

    @Override
    public void onPrefabAction( EntityPrefabActionEvent event ) {
        switch ( event.type ) {
            case CREATE_ENTITY: {
                activateOne( event.prefabId, event.attributes );
                break;
            }
            case CACHE_PREFABS: {
                cacheComponents( event.prefabId, event.number );
                break;
            }
        }
    }
    
    public final Iterator<String> getPrefabNames() {
        return prefabNames.iterator();
    }
    
    public final EntityPrefab getPrefab( String prefabName ) {
        return prefabs.get( prefabNames.indexOf( prefabName ) );
    }
    
    public final void cacheComponents( String prefabName, int number ) {
        cacheComponents( prefabNames.indexOf( prefabName ), number );
    }
    
    public final void cacheComponents( int prefabId, int number ) {
        if ( !prefabs.contains( prefabId ) ) {
            return;
        }
        
        ArrayDeque<IndexedTypeSet> instanceDeque = components.get( prefabId );
        if ( instanceDeque == null ) {
            instanceDeque = new ArrayDeque<IndexedTypeSet>();
            components.set( prefabId, instanceDeque );
        }
        
        IndexedTypeSet prefabComponentSet = prefabComponents.get( prefabId );
        for ( int i = 0; i < number; i++ ) {
            instanceDeque.add( copyComponents( prefabComponentSet ) );
        }
    }

    public final Entity buildOne( int prefabId, EntityAttributeMap attributes ) {
        return entitySystem.getEntityBuilder()
            .setPrefabComponents( getComponents( prefabId ) )
            .setAttributes( attributes )
            .build();
    }

    public final Entity activateOne( int prefabId, EntityAttributeMap attributes ) {
        Entity newEntity = buildOne( prefabId, attributes );
        if ( newEntity != null ) {
            entitySystem.activate( newEntity.getId() );
        }

        return newEntity;
    }

    private IndexedTypeSet getComponents( int prefabId ) {
        ArrayDeque<IndexedTypeSet> componentsOfType = components.get( prefabId );
        if ( componentsOfType == null ) {
            return copyComponents( prefabComponents.get( prefabId ) );
        }

        IndexedTypeSet result = componentsOfType.pop();
        if ( result == null ) {
            result = copyComponents( prefabComponents.get( prefabId ) );
        }

        return result;
    }
    
    private IndexedTypeSet copyComponents( IndexedTypeSet prefabComponentSet ) {
        IndexedTypeSet newComponents = entityProvider.getComponentTypeSet();
        
        for ( EntityComponent component : prefabComponentSet.<EntityComponent>getIterable() ) {
            component.toAttributes( attributeMap );
        }
        
        entityProvider.createComponents( newComponents, attributeMap );
        return newComponents;
    }

    
    @Override
    public <C> ComponentBuilder<C> getComponentBuilder( Class<C> type ) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Set<Class<?>> supportedComponentTypes() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void fromAttributes( Attributes attributes ) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void fromAttributes( Attributes attributes, BuildType buildType ) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void toAttributes( Attributes attributes ) {
        // TODO Auto-generated method stub
        
    }



}
