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
import com.inari.firefly.system.FFComponent;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;

public class EntityPrefabSystem implements FFComponent, ComponentSystem, ComponentBuilderFactory, Disposable {
    
    private DynArray<EntityPrefab> prefabs;
    private DynArray<String> prefabNames;
    private DynArray<IndexedTypeSet> prefabComponents;
    private DynArray<ArrayDeque<IndexedTypeSet>> instances;
    
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
        
        entitySystem = context.getComponent( FFContext.System.ENTITY_SYSTEM );
        entityProvider = context.getComponent( FFContext.ENTITY_PROVIDER );
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
    }
    
    @Override
    public void dispose( FFContext context ) {
        for ( EntityPrefab prefab : prefabs ) {
            prefab.dispose();
        }
        
        prefabNames.clear();
        prefabComponents.clear();
        
        entitySystem = null;
        eventDispatcher = null;
    }
    
    public final Iterator<String> getPrefabNames() {
        return prefabNames.iterator();
    }
    
    public final EntityPrefab getPrefab( String prefabName ) {
        return prefabs.get( prefabNames.indexOf( prefabName ) );
    }
    
    public final void preInstantiate( String prefabName, int number ) {
        preInstantiate( prefabNames.indexOf( prefabName ), number );
    }
    
    public final void preInstantiate( int prefabId, int number ) {
        if ( !prefabs.contains( prefabId ) ) {
            return;
        }
        
        ArrayDeque<IndexedTypeSet> instanceDeque = instances.get( prefabId );
        if ( instanceDeque == null ) {
            instanceDeque = new ArrayDeque<IndexedTypeSet>();
            instances.set( prefabId, instanceDeque );
        }
        
        IndexedTypeSet prefabComponentSet = prefabComponents.get( prefabId );
        for ( int i = 0; i < number; i++ ) {
            instanceDeque.add( copyComponents( prefabComponentSet ) );
        }
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
