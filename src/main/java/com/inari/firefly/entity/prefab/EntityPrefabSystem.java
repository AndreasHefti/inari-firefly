package com.inari.firefly.entity.prefab;

import java.util.ArrayDeque;
import java.util.Iterator;

import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.EntityAttributeMap;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.entity.EntityProvider;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public class EntityPrefabSystem extends ComponentSystem<EntityPrefabSystem> {
    
    public static final FFSystemTypeKey<EntityPrefabSystem> SYSTEM_KEY = FFSystemTypeKey.create( EntityPrefabSystem.class );
    
    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        EntityPrefab.TYPE_KEY
    };

    private DynArray<EntityPrefab> prefabs;
    private DynArray<IndexedTypeSet> prefabComponents;
    private DynArray<ArrayDeque<IndexedTypeSet>> components;
    
    private EntitySystem entitySystem;
    private EntityProvider entityProvider;
    
    private EntityAttributeMap attributeMap;
    
    EntityPrefabSystem() {
        super( SYSTEM_KEY );
    }
    
    @Override
    public void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        prefabs = new DynArray<EntityPrefab>();
        prefabComponents = new DynArray<IndexedTypeSet>();
        components = new DynArray<ArrayDeque<IndexedTypeSet>>();
        
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        entityProvider = context.getSystem( EntityProvider.SYSTEM_KEY );
        context.registerListener( EntityPrefabSystemEvent.TYPE_KEY, this );
        attributeMap = new EntityAttributeMap( context );
    }
    
    @Override
    public void dispose( FFContext context ) {
        clear();
        
        context.disposeListener( EntityPrefabSystemEvent.TYPE_KEY, this );
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
        components.clear();
    }
    
    public final void deletePrefab( String prefabName ) {
        EntityPrefab prefab = getPrefab( prefabName );
        if ( prefab == null ) {
            return;
        }
        
        prefabs.remove( prefab.index() );
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
    
    public final EntityPrefab getPrefab( final String prefabName ) {
        for ( EntityPrefab prefab : prefabs ) {
            if ( prefabName.equals( prefab.getName() ) ) {
                return prefab;
            }
        }
        return null;
    }
    
    public final int getPrefabId( final String prefabName ) {
        for ( EntityPrefab prefab : prefabs ) {
            if ( prefabName.equals( prefab.getName() ) ) {
                return prefab.index();
            }
        }
        return -1;
    }
    
    public final void cacheComponents( String prefabName, int number ) {
        EntityPrefab prefab = getPrefab( prefabName );
        if ( prefab != null ) {
            cacheComponents( prefab.index(), number );
        }
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

    public final int buildOne( final int prefabId, final EntityAttributeMap attributes ) {
        return entitySystem.getEntityBuilder()
            .setPrefabComponents( getComponents( prefabId ) )
            .setAttributes( attributes )
            .build();
    }

    public final void rebuildEntity( final int prefabId, final int entityId, final EntityAttributeMap attributes, final boolean activation ) {
        if ( activation ) {
            entitySystem.deactivateEntity( entityId );
        }

        IndexedTypeSet components = entitySystem.getComponents( entityId );
        components.clear();
        IndexedTypeSet newComponents = copyComponents( prefabComponents.get( prefabId ) );
        components.setAll( newComponents );
        if ( attributes != null && ! attributes.isEmpty() ) {
            for ( EntityComponent component : newComponents.<EntityComponent>getIterable() ) {
                component.fromAttributes( attributes );
            }
        }

        if ( activation ) {
            entitySystem.activateEntity( entityId );
        }
    }

    public final int activateOne( final int prefabId, final EntityAttributeMap attributes ) {
        int newEntityId = buildOne( prefabId, attributes );
        if ( newEntityId >= 0 ) {
            entitySystem.activateEntity( newEntityId );
        }

        return newEntityId;
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
    public final SystemComponentKey<?>[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
            new EntityPrefabBuilderAdapter( this )
        };
    }

    
    public final class EntityPrefabBuilder extends SystemComponentBuilder {
        
        private EntityPrefabBuilder() {
            super( new EntityAttributeMap( context ) );
        }

        @Override
        public final SystemComponentKey<EntityPrefab> systemComponentKey() {
            return EntityPrefab.TYPE_KEY;
        }

        @Override
        public final int doBuild( int componentId, Class<?> subType, boolean activate ) {
            IndexedTypeSet components = entityProvider.getComponentTypeSet();
            entityProvider.initAttributesOnController( (EntityAttributeMap) attributes );
            entityProvider.createComponents( components, (EntityAttributeMap) attributes );

            EntityPrefab prefab = createSystemComponent( componentId, subType, context );
            checkName( prefab );
            
            int prefabId = prefab.index();
            prefabs.set( prefabId, prefab );
            prefabComponents.set( prefabId, components );
            
            if ( attributes.contains( EntityPrefab.INITIAL_CREATE_NUMBER ) ) {
                cacheComponents( 
                    prefabId, 
                    attributes.getValue( EntityPrefab.INITIAL_CREATE_NUMBER ) 
                );
            }
            
            return prefabId;
        }
    }
    
    private final class EntityPrefabBuilderAdapter extends SystemBuilderAdapter<EntityPrefab> {
        public EntityPrefabBuilderAdapter( EntityPrefabSystem system ) {
            super( system, new EntityPrefabBuilder() );
        }
        @Override
        public final SystemComponentKey<EntityPrefab> componentTypeKey() {
            return EntityPrefab.TYPE_KEY;
        }
        @Override
        public final EntityPrefab get( int id ) {
            return prefabs.get( id );
        }
        @Override
        public final Iterator<EntityPrefab> getAll() {
            return prefabs.iterator();
        }
        @Override
        public final void delete( int id ) {
            deletePrefab( id );
        }
        @Override
        public final int getId( String name ) {
            return getPrefabId( name );
        }
        @Override
        public final void activate( int id ) {
            throw new UnsupportedOperationException( componentTypeKey() + " is not activable" );
        }
        @Override
        public final void deactivate( int id ) {
            throw new UnsupportedOperationException( componentTypeKey() + " is not activable" );
        }
    }

}
