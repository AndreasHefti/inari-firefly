package com.inari.firefly.entity;

import java.util.ArrayDeque;
import java.util.Iterator;

import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.entity.event.EntityPrefabActionEvent;
import com.inari.firefly.entity.event.EntityPrefabActionListener;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public class EntityPrefabSystem extends ComponentSystem implements EntityPrefabActionListener {
    
    private static final SystemComponentKey[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        EntityPrefab.TYPE_KEY
    };
    
    public static final TypedKey<EntityPrefabSystem> CONTEXT_KEY = TypedKey.create( "ENTITY_PREFAB_SYSTEM", EntityPrefabSystem.class );
    
    private DynArray<EntityPrefab> prefabs;
    private DynArray<String> prefabNames;
    private DynArray<IndexedTypeSet> prefabComponents;
    private DynArray<ArrayDeque<IndexedTypeSet>> components;
    
    private EntitySystem entitySystem;
    private EntityProvider entityProvider;
    
    private final EntityAttributeMap attributeMap;
    
    
    EntityPrefabSystem() {
        attributeMap = new EntityAttributeMap();
    }
    
    @Override
    public void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        prefabs = new DynArray<EntityPrefab>();
        prefabNames = new DynArray<String>();
        prefabComponents = new DynArray<IndexedTypeSet>();
        components = new DynArray<ArrayDeque<IndexedTypeSet>>();
        
        entitySystem = context.getSystem( EntitySystem.CONTEXT_KEY );
        entityProvider = context.getSystem( EntityProvider.CONTEXT_KEY );
        context.registerListener( EntityPrefabActionEvent.class, this );
    }
    
    @Override
    public void dispose( FFContext context ) {
        clear();
        
        context.disposeListener( EntityPrefabActionEvent.class, this );
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
        components.clear();
    }
    
    public final void deletePrefab( String prefabName ) {
        EntityPrefab prefab = getPrefab( prefabName );
        if ( prefab == null ) {
            return;
        }
        
        deletePrefab( prefab );
    }
    
    public final void deletePrefab( int prefabId ) {
        if ( !prefabs.contains( prefabId ) ) {
            return;
        }
        
        EntityPrefab prefab = prefabs.remove( prefabId );
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
        int prefabId = prefabNames.indexOf( prefabName );
        if ( prefabId < 0 ) {
            return null;
        }
        return prefabs.get( prefabId );
    }
    
    public final void cacheComponents( String prefabName, int number ) {
        cacheComponents( prefabNames.indexOf( prefabName ), number );
    }
    
    public final void cacheComponents( int prefabId, int number ) {
        if ( !prefabs.contains( prefabId ) ) {
            return;
        }
        
        ArrayDeque<IndexedTypeSet> instanceDeque;
        if ( !components.contains( prefabId ) ) {
            instanceDeque = new ArrayDeque<IndexedTypeSet>();
            components.set( prefabId, instanceDeque );
        } else {
            instanceDeque = components.get( prefabId );
        }
        
        IndexedTypeSet prefabComponentSet = prefabComponents.get( prefabId );
        for ( int i = 0; i < number; i++ ) {
            instanceDeque.add( copyComponents( prefabComponentSet ) );
        }
    }
    
    public final int buildOne( int prefabId ) {
        return entitySystem.getEntityBuilder()
            .setPrefabComponents( getComponents( prefabId ) )
            .build();
    }

    public final int buildOne( int prefabId, EntityAttributeMap attributes ) {
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

    public final int activateOne( int prefabId, EntityAttributeMap attributes ) {
        int newEntity = buildOne( prefabId, attributes );
        if ( newEntity >= 0 ) {
            entitySystem.activate( newEntity );
        }

        return newEntity;
    }

    private IndexedTypeSet getComponents( int prefabId ) {
        ArrayDeque<IndexedTypeSet> componentsOfType = components.get( prefabId );
        if ( componentsOfType == null || componentsOfType.isEmpty() ) {
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
        int prefabId = prefab.index();
        
        entityProvider.disposeComponentSet( prefabComponents.get( prefabId ) );
        ArrayDeque<IndexedTypeSet> componentsOfPrefab = components.get( prefab.index() );
        for ( IndexedTypeSet componentSet : componentsOfPrefab ) {
            entityProvider.disposeComponentSet( componentSet );
        }
        
        componentsOfPrefab.clear();
        prefabComponents.remove( prefabId );
        prefab.dispose();
    }
    
    public final EntityPrefabBuilder getEntityPrefabBuilder() {
        return new EntityPrefabBuilder();
    }
    @Override
    public final SystemComponentKey[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
            new EntityPrefabBuilderAdapter( this )
        };
    }

    
    public final class EntityPrefabBuilder extends SystemComponentBuilder {

        @Override
        public final SystemComponentKey systemComponentKey() {
            return EntityPrefab.TYPE_KEY;
        }

        @Override
        public final int doBuild( int componentId, Class<?> subType ) {
            IndexedTypeSet components = entityProvider.getComponentTypeSet();
            entityProvider.createComponents( components, (EntityAttributeMap) attributes );

            EntityPrefab prefab = new EntityPrefab( componentId );
            prefab.fromAttributes( attributes );
            
            checkName( prefab );
            
            int prefabId = prefab.index();
            prefabs.set( prefabId, prefab );
            prefabNames.set( prefabId, prefab.getName() );
            prefabComponents.set( prefabId, components );
            
            return prefab.getId();
        }
    }
    
    private final class EntityPrefabBuilderAdapter extends SystemBuilderAdapter<EntityPrefab> {
        public EntityPrefabBuilderAdapter( EntityPrefabSystem system ) {
            super( system, new EntityPrefabBuilder() );
        }
        @Override
        public final SystemComponentKey componentTypeKey() {
            return EntityPrefab.TYPE_KEY;
        }
        @Override
        public final EntityPrefab get( int id, Class<? extends EntityPrefab> subtype ) {
            return prefabs.get( id );
        }
        @Override
        public final Iterator<EntityPrefab> getAll() {
            return prefabs.iterator();
        }
        @Override
        public final void delete( int id, Class<? extends EntityPrefab> subtype ) {
            deletePrefab( id );
        }
    }
}
