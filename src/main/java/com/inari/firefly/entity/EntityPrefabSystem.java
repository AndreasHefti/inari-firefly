package com.inari.firefly.entity;

import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.Disposable;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.FFComponent;

public class EntityPrefabSystem implements FFComponent, ComponentSystem, ComponentBuilderFactory, Disposable {
    
    private DynArray<EntityPrefab> prefabs;
    private DynArray<String> prefabNames;
    private DynArray<IndexedTypeSet> prefabComponents;
    private DynArray<Stack<IndexedTypeSet>> componentInstancePool;
    
    private EntitySystem entitySystem;
    private IEventDispatcher eventDispatcher;
    
    private final EntityAttributeMap attributeMap;
    
    
    EntityPrefabSystem() {
        attributeMap = new EntityAttributeMap();
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
        
        Stack<IndexedTypeSet> instanceStack = componentInstancePool.get( prefabId );
        if ( instanceStack == null ) {
            instanceStack = new Stack<IndexedTypeSet>();
            componentInstancePool.set( prefabId, instanceStack );
        }
        
        IndexedTypeSet prefabComponentSet = prefabComponents.get( prefabId );
        for ( int i = 0; i < number; i++ ) {
            IndexedTypeSet instanceSet = copyComponents( prefabComponentSet );
            instanceStack.push( instanceSet );
        }
    }
    
    private IndexedTypeSet copyComponents( IndexedTypeSet prefabComponentSet ) {
        IndexedTypeSet newComponents = new IndexedTypeSet( EntityComponent.class );
        
        for ( EntityComponent component : prefabComponentSet.<EntityComponent>getIterable() ) {
            component.toAttributes( attributeMap );
        }
        
        entitySystem.createComponents( newComponents, attributeMap );
        return newComponents;
    }

    @Override
    public void init( FFContext context ) throws FFInitException {
        prefabs = new DynArray<EntityPrefab>();
        prefabNames = new DynArray<String>();
        prefabComponents = new DynArray<IndexedTypeSet>();
        componentInstancePool = new DynArray<Stack<IndexedTypeSet>>();
        
        entitySystem = context.getComponent( FFContext.System.ENTITY_SYSTEM );
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
    }
    
    @Override
    public void dispose( FFContext context ) {
        for ( EntityPrefab prefab : prefabs ) {
            prefab.dispose();
        }
        
        prefabNames.clear();
        
        for( IndexedTypeSet prefabComponent : prefabComponents ) {
            prefabComponent.clear();
        }
        prefabComponents.clear();

        for ( Stack<IndexedTypeSet> instanceStack : componentInstancePool ) {
            for ( IndexedTypeSet instanceSet : instanceStack ) {
                instanceSet.clear();
            }
        }
        componentInstancePool.clear();
        
        entitySystem = null;
        eventDispatcher = null;
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
