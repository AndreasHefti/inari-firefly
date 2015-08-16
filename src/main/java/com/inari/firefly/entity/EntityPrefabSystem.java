package com.inari.firefly.entity;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.Disposable;
import com.inari.firefly.component.ComponentBuilderHelper;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.entity.event.EntityPrefabActionEvent;
import com.inari.firefly.entity.event.EntityPrefabActionListener;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.view.View;

public class EntityPrefabSystem implements FFContextInitiable, EntityPrefabActionListener, ComponentSystem, ComponentBuilderFactory, Disposable {
    
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
        clear();
        
        eventDispatcher.unregister( EntityPrefabActionEvent.class, this );
    }
    
    public final  void clear() {
        for ( int i = 0; i < prefabs.capacity(); i++ ) {
            EntityPrefab prefab = prefabs.get( i );
            if ( prefab != null ) {
                deletePrefab( i );
            }
        }
        
        prefabs.clear();
        prefabComponents.clear();
        prefabNames.clear();
        prefabComponents.clear();
    }
    
    public final void deletePrefab( String prefabName ) {
        EntityPrefab prefab = getPrefab( prefabName );
        if ( prefab == null ) {
            return;
        }
        
        deletePrefab( prefab );
    }
    
    public final void deletePrefab( int prefabId ) {
        EntityPrefab prefab = prefabs.get( prefabId );
        if ( prefab == null ) {
            return;
        }
        
        deletePrefab( prefab );
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
            case REBUILD_ENTITY: {
                rebuildEntity( event.prefabId, event.entityId, event.attributes, event.activation );
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

    public final void rebuildEntity( int prefabId, int entityId, EntityAttributeMap attributes, boolean activation ) {
        if ( activation ) {
            entitySystem.deactivate( entityId );
        }

        IndexedTypeSet components = entitySystem.getComponents( entityId );
        components.clear();
        IndexedTypeSet newComponents = getComponents( prefabId );
        if ( attributes != null && ! attributes.isEmpty() ) {
            for( EntityComponent component : newComponents.<EntityComponent>getIterable() ) {
                component.toAttributes( attributeMap );
            }
        }
        entitySystem.components.set( entityId, newComponents );

        if ( activation ) {
            entitySystem.activate( entityId );
        }
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
    
    private void deletePrefab( EntityPrefab prefab ) {
        prefab.dispose();
        entityProvider.disposeComponentSet( prefabComponents.get( prefab.index() ) );
        ArrayDeque<IndexedTypeSet> componentsOfPrefab = components.get( prefab.index() );
        for ( IndexedTypeSet componentSet : componentsOfPrefab ) {
            entityProvider.disposeComponentSet( componentSet );
        }
    }

    
    @SuppressWarnings( "unchecked" )
    @Override
    public final <C> ComponentBuilder<C> getComponentBuilder( Class<C> type ) {
        if ( type == View.class ) {
            return (ComponentBuilder<C>) getEntityPrefabBuilder();
        }
        
        throw new IllegalArgumentException( "Unsupported IComponent type for EntityPrefabSystem. Type: " + type );
    }
    
    public final EntityPrefabBuilder getEntityPrefabBuilder() {
        return new EntityPrefabBuilder( this );
    }

    private static final Set<Class<?>> SUPPORTED_COMPONENT_TYPES = new HashSet<Class<?>>();
    @Override
    public final Set<Class<?>> supportedComponentTypes() {
        if ( SUPPORTED_COMPONENT_TYPES.isEmpty() ) {
            SUPPORTED_COMPONENT_TYPES.add( EntityPrefab.class );
        }
        return SUPPORTED_COMPONENT_TYPES;
    }
    
    @Override
    public final void fromAttributes( Attributes attributes ) {
        fromAttributes( attributes, BuildType.CLEAR_OLD );
    }
    @Override
    public final void fromAttributes( Attributes attributes, BuildType buildType ) {
        if ( buildType == BuildType.CLEAR_OLD ) {
            clear();
        }
        
        new ComponentBuilderHelper<EntityPrefab>() {
            @Override
            public EntityPrefab get( int id ) {
                return prefabs.get( id );
            }
            @Override
            public void delete( int id ) {
                delete( id );
            }
        }.buildComponents( EntityPrefab.class, buildType, getEntityPrefabBuilder(), attributes );
    }
    

    @Override
    public final void toAttributes( Attributes attributes ) {
        ComponentBuilderHelper.toAttributes( attributes, EntityPrefab.class, prefabs );
    }

    protected final class EntityPrefabBuilder extends BaseComponentBuilder<EntityPrefab> {
        
        private IndexedTypeSet components;

        private EntityPrefabBuilder( EntityPrefabSystem system ) {
            super( system, new EntityAttributeMap() );
        }

        @Override
        public EntityPrefab build( int componentId ) {
            components = entityProvider.getComponentTypeSet();
            entityProvider.createComponents( components, (EntityAttributeMap) attributes );

            EntityPrefab prefab = new EntityPrefab( componentId );
            prefab.fromAttributes( attributes );
            
            checkName( prefab );
            
            int prefabId = prefab.index();
            prefabs.set( prefabId, prefab );
            prefabNames.set( prefabId, prefab.getName() );
            prefabComponents.set( prefabId, components );
            
            return prefab;
        }
    }

}
