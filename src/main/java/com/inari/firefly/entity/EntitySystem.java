/*******************************************************************************
 * Copyright (c) 2015, Andreas Hefti, inarisoft@yahoo.de 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/ 
package com.inari.firefly.entity;

import java.util.Iterator;

import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.aspect.AspectBitSet;
import com.inari.commons.lang.functional.Predicate;
import com.inari.commons.lang.indexed.Indexed;
import com.inari.commons.lang.indexed.IndexedType;
import com.inari.commons.lang.indexed.IndexedTypeAspectSet;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.Component.ComponentKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.entity.event.EntityActivationEvent.Type;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public final class EntitySystem extends ComponentSystem<EntitySystem> implements Iterable<Entity> {
    
    public static final FFSystemTypeKey<EntitySystem> SYSTEM_KEY = FFSystemTypeKey.create( EntitySystem.class );
    public static final SystemComponentKey ENTITY_TYPE_KEY = SystemComponentKey.create( EntityComponentAdapter.class );

    private static final SystemComponentKey[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        ENTITY_TYPE_KEY
    };

    private static final int INIT_SIZE = 1000;

    private EntityProvider entityProvider;
    
    final DynArray<Entity> activeEntities;
    final DynArray<Entity> inactiveEntities;
    final DynArray<IndexedTypeSet> components;
    
    EntitySystem() {
        super( SYSTEM_KEY );
        activeEntities = new DynArray<Entity>( INIT_SIZE );
        inactiveEntities = new DynArray<Entity>( INIT_SIZE );
        components = new DynArray<IndexedTypeSet>( INIT_SIZE );
    }
    
    @Override
    public void init( FFContext context ) {
        super.init( context );
        
        entityProvider = context.getSystem( EntityProvider.SYSTEM_KEY );

        Integer entityCapacity = context.getProperty( FFContext.Properties.ENTITY_MAP_CAPACITY );
        if ( entityCapacity != null ) {
            activeEntities.ensureCapacity( entityCapacity );
            inactiveEntities.ensureCapacity( entityCapacity );
            components.ensureCapacity( entityCapacity );
        }
    }
    
    @Override
    public void dispose( FFContext context ) {
        activeEntities.clear();
        inactiveEntities.clear();
        components.clear();
    }
    
    public final EntityBuilder getEntityBuilder() {
        return new EntityBuilder();
    }

    public final boolean isActive( int entityId ) {
        return activeEntities.get( entityId ) != null;
    }

    public final boolean isRestored( int entityId ) {
        return !( activeEntities.contains( entityId ) && inactiveEntities.contains( entityId ) );
    }
    
    public final void activateEntity( int entityId ) {
        if ( activeEntities.contains( entityId ) || !inactiveEntities.contains( entityId ) ) {
            return;
        }
        
        Entity entity = inactiveEntities.remove( entityId );
        activeEntities.set( entityId, entity ) ;
        
        AspectBitSet aspect = getAspect( entityId );
        context.notify( 
            new EntityActivationEvent( entity.index(), aspect, Type.ENTITY_ACTIVATED ) 
        );
    }
    
    public final void deactivateEntity( int entityId ) {
        if ( !activeEntities.contains( entityId ) ) {
            return;
        }
        
        Entity entity = activeEntities.remove( entityId );
        inactiveEntities.set( entity.index(), entity );
        
        context.notify( 
            new EntityActivationEvent( entityId, getAspect( entityId ), Type.ENTITY_DEACTIVATED ) 
        );
    }
    
    public final void deleteEtity( int entityId ) {
        delete( entityId );
    }

    public final void delete( int entityId ) {
        if ( activeEntities.contains( entityId ) ) {
            deactivateEntity( entityId );
        }
        
        if ( !inactiveEntities.contains( entityId ) ) {
            return;
        }

        Entity entityToRestore = inactiveEntities.remove( entityId );
        IndexedTypeSet componentsToRestore = components.remove( entityId );

        entityProvider.dispose( entityToRestore, componentsToRestore );
    }
    
    public final void delete( String entityName ) {
        int entityId = getEntityId( entityName );
        if ( entityId >= 0 ) {
            delete( entityId );
        }
    }
    
    public final void deleteAll( IntIterator iterator ) {
        while( iterator.hasNext() ) {
            delete( iterator.next() );
        }
    }

    public final void deleteAll() {
        for ( Entity entity : activeEntities ) {
            delete( entity.index() );
        }
    }
    
    @Override
    public final void clear() {
        deleteAll();
    }
    
    public final Entity getEntityActive( int entityId ) {
        return activeEntities.get( entityId );
    }

    public final Entity getEntity( int entityId ) {
        if ( activeEntities.contains( entityId ) ) {
            return activeEntities.get( entityId );
        }
        return inactiveEntities.get( entityId );
    }
    
    public final int getEntityId( String name ) {
        if ( name == null ) {
            return -1;
        }
        
        ComponentIterator<EEntity> iterator = new ComponentIterator<EEntity>( EEntity.TYPE_KEY.index(), false );
        while ( iterator.hasNext() ) {
            EEntity entityComponent = iterator.next();
            if ( name.equals( entityComponent.getEntityName() ) ) {
                return iterator.entityId();
            }
        }
 
        
        return -1;
    }

    public final AspectBitSet getAspect( int entityId ) {
        IndexedTypeSet components = getComponents( entityId );
        if ( components == null ) {
            return null;
        }
        
        return components.getAspect();
    }

    @Override
    public final Iterator<Entity> iterator() {
        return activeEntities.iterator();
    }
    
    public final <T extends EntityComponent> Iterable<T> components( final Class<T> componentType ) {
        return components( componentType, true );
    }
    
    public final <T extends EntityComponent> Iterable<T> components( final Class<T> componentType, final boolean onlyActive ) {
        final int componentIndex = Indexer.getIndexedTypeKey( EntityComponent.EntityComponentTypeKey.class, componentType ).index();
        return new Iterable<T>() {
            @Override
            public final Iterator<T> iterator() {
                return new ComponentIterator<T>( componentIndex, onlyActive );
            }
        };
    }

    public final Iterable<Entity> entities( final AspectBitSet aspect ) {
        return new Iterable<Entity>() {
            @Override
            public final Iterator<Entity> iterator() {
                return new AspectedEntityIterator( aspect );
            }
        };
    }
    

    public final Iterable<Entity> entities( final Predicate<Entity> predicate ) {
        return new Iterable<Entity>() {
            @Override
            public final Iterator<Entity> iterator() {
                return new PredicatedEntityIterator( predicate );
            }
        };
    }

    public final <T extends EntityComponent> T getComponent( int entityId, Class<T> componentType ) {
        return components.get( entityId ).get( componentType );
    }

    public final <T extends EntityComponent> T getComponent( int entityId, int componentId ) {
        return components.get( entityId ).get( componentId );
    }
    
    public final <T extends EntityComponent> T getComponent( int entityId, Indexed indexed ) {
        return getComponent( entityId, indexed.index() );
    }

    public final IndexedTypeSet getComponents( int entityId ) {
        return components.get( entityId );
    }
    
    // ---- ComponentSystem implementation --------------------------------------
    
    @Override
    public final SystemComponentKey[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }
    
    private void entityToAttribute( Attributes attributes, Entity entity ) {
        ComponentKey key = new ComponentKey( Entity.class, entity.getId() );
        EntityAttributeMap attributeMap = new EntityAttributeMap();
        attributeMap.setComponentKey( key );
        attributes.add( attributeMap );
        IndexedTypeSet components = getComponents( entity.getId() );
        for ( EntityComponent component : components.<EntityComponent>getIterable() ) {
            component.toAttributes( attributeMap );
        }
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
            new EntityBuilderHelper( this )
        };
    }
    
    // ---- Utilities --------------------------------------------------------

    private abstract class EntityIterator implements Iterator<Entity> {
        
        protected final Iterator<Entity> delegate;
        protected Entity current = null;
        
        protected EntityIterator( Iterator<Entity> delegate ) {
            this.delegate = delegate;
        }
        
        @Override
        public final boolean hasNext() {
            return current != null;
        }

        @Override
        public final Entity next() {
            Entity result = current;
            findNext();
            return result;
        }

        @Override
        public final void remove() {
            delegate.remove();
        }
        
        protected abstract void findNext();
    }
    
    public final class ComponentIterator<C extends EntityComponent> implements Iterator<C> {
        
        private final int componentIndex;
        private final boolean onlyActive;
        private int currentEntityIndex = -1;
        private int nextEntityIndex = -1;
        
        ComponentIterator( int componentIndex, boolean onlyActive ) {
            this.componentIndex = componentIndex;
            this.onlyActive = onlyActive;
            findNext();
        }
        
        private final void findNext() {
            while ( nextEntityIndex < components.capacity() ) {
                nextEntityIndex++;
                if ( onlyActive && !activeEntities.contains( nextEntityIndex ) ) {
                    continue;
                }
                if ( components.contains( nextEntityIndex ) && components.get( nextEntityIndex ).contains( componentIndex ) ) {
                    return;
                }
            }
            nextEntityIndex = -1;
        }

        @Override
        public final boolean hasNext() {
            return nextEntityIndex >= 0 ;
        }
        
        public final int entityId() {
            return currentEntityIndex;
        }

        @Override
        public final C next() {
            C component = components.get( nextEntityIndex ).get( componentIndex );
            currentEntityIndex = nextEntityIndex;
            findNext();
            return component;
        }

        @Override
        public final void remove() {
            delete( currentEntityIndex );
        }
    }
    
    private final class AspectedEntityIterator extends EntityIterator {
        
        private final AspectBitSet aspect;
        
        public AspectedEntityIterator( AspectBitSet aspect ) {
            super( activeEntities.iterator() );
            this.aspect = aspect;
            findNext();
        }

        @Override
        protected void findNext() {
            current = null;
            boolean foundNext = false;
            while ( !foundNext && delegate.hasNext() ) {
                Entity next = delegate.next();
                if ( getAspect( next.getId() ).include( aspect ) ) {
                    foundNext = true;
                    current = next;
                }
            }
        }
    }
    
    private final class PredicatedEntityIterator extends EntityIterator {
        
        private final Predicate<Entity> predicate;

        private PredicatedEntityIterator( Predicate<Entity> predicate ) {
            super( activeEntities.iterator() );
            this.predicate = predicate;
            findNext();
        }

        @Override
        protected void findNext() {
            current = null;
            boolean foundNext = false;
            while ( !foundNext && delegate.hasNext() ) {
                Entity next = delegate.next();
                if ( predicate.apply( next ) ) {
                    foundNext = true;
                    current = next;
                }
            }
        }
    }
    
    public final class EntityBuilder extends SystemComponentBuilder {
        
        private IndexedTypeSet prefabComponents;

        private EntityBuilder() {
            super( new EntityAttributeMap() );
        }
        
        public EntityBuilder setPrefabComponents( IndexedTypeSet prefabComponents ) {
            this.prefabComponents = prefabComponents;
            return this;
        }
        
        @Override
        public int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            Entity entity = entityProvider.getEntity( componentId );

            IndexedTypeAspectSet aspectToCheck;
            if ( prefabComponents != null ) {
                // if we have prefab components we use them
                components.set( entity.index(), prefabComponents );
                aspectToCheck = prefabComponents.getAspect();
            } else {
                IndexedTypeSet componentSet = entityProvider.getComponentTypeSet();
                components.set( entity.index(), componentSet );
                entityProvider.createComponents( componentSet, (EntityAttributeMap) attributes );
                aspectToCheck = componentSet.getAspect();
            }

            if ( aspectToCheck == null || !aspectToCheck.valid() ) {
                throw new IllegalStateException( 
                    String.format( "The Entity %s has no or an empty Aspect. This makes no sense and an empty Entity cannot activated", entity ) 
                );
            }
            
            inactiveEntities.set( entity.getId(), entity );
            
            if ( activate ) {
                activateEntity( entity.index() );
            }
            
            return entity.index();
        }

        @Override
        public final SystemComponentKey systemComponentKey() {
            return ENTITY_TYPE_KEY;
        }
    }
    
    private final class EntityBuilderHelper extends SystemBuilderAdapter<EntityComponentAdapter> {
        
        public EntityBuilderHelper( EntitySystem system ) {
            super( system, getEntityBuilder() );
        }
        @Override
        public final SystemComponentKey componentTypeKey() {
            return ENTITY_TYPE_KEY;
        }
        @Override
        public EntityComponentAdapter get( int id, Class<? extends EntityComponentAdapter> subtype ) {
            return null;
        }
        @Override
        public void delete( int id, Class<? extends EntityComponentAdapter> subtype ) {
        }
        @Override
        public final Iterator<EntityComponentAdapter> getAll() {
            return null;
        }
        @Override
        public final void fromAttributes( Attributes attributes, BuildType buildType ) {
            if ( buildType == BuildType.CLEAR_OLD ) {
                deleteAll();
            }
            
            EntityBuilder entityBuilder = getEntityBuilder();
            for ( AttributeMap entityAttrs : attributes.getAllOfType( Entity.class ) ) {
                int entityId = entityAttrs.getComponentKey().getId();
                if ( buildType == BuildType.MERGE_ATTRIBUTES ) {
                    IndexedTypeSet componentsOfEntity = getComponents( entityId );
                    if ( componentsOfEntity != null ) {
                        for ( EntityComponent comp : componentsOfEntity.<EntityComponent>getIterable() ) {
                            comp.toAttributes( entityBuilder.getAttributes() );
                        }
                    }
                } else if ( buildType == BuildType.OVERWRITE ) {
                    deleteEtity( entityId );
                }
                entityBuilder
                    .setAttributes( entityAttrs )
                    .buildAndNext( Entity.class )
                    .clear();
            }
            
            AttributeMap attributeMap = attributes.get( EntityComponentAdapter.ACTIVE_ENTITIES_IDS_KEY );
            if ( attributeMap == null ) {
                return;
            }
            String activeEntityIdsString = attributeMap.getValue( EntityComponentAdapter.ACTIVE_ENTITY_IDS );
            IntBag activeEntityIds = new IntBag();
            activeEntityIds.fromConfigString( activeEntityIdsString );
            IntIterator iterator = activeEntityIds.iterator();
            while ( iterator.hasNext() ) {
                activateEntity( iterator.next() );
            }
        }

        @Override
        public final void toAttributes( Attributes attributes ) {
            IntBag activeEntityIds = new IntBag( activeEntities.size() );
            for ( Entity entity : activeEntities ) {
                entityToAttribute( attributes, entity );
                activeEntityIds.add( entity.getId() );
            }
            
            EntityAttributeMap attributeMap = new EntityAttributeMap();
            attributeMap.setComponentKey( EntityComponentAdapter.ACTIVE_ENTITIES_IDS_KEY );
            attributes.add( attributeMap );
            attributeMap.put( EntityComponentAdapter.ACTIVE_ENTITY_IDS, activeEntityIds.toConfigString() );
            
            for ( Entity entity : inactiveEntities ) {
                entityToAttribute( attributes, entity );
            }
        }
    }
    
    final static class EntityComponentAdapter extends SystemComponent implements IndexedType {
        
        static final AttributeKey<String> ACTIVE_ENTITY_IDS = new AttributeKey<String>( "ACTIVE_ENTITY_IDS", String.class, EntityComponentAdapter.class );
        static final ComponentKey ACTIVE_ENTITIES_IDS_KEY = new ComponentKey( EntityComponentAdapter.class, 0 );
        
        protected EntityComponentAdapter() {
            super( 0 );
        }

        @Override
        public final IndexedTypeKey indexedTypeKey() {
            return ENTITY_TYPE_KEY;
        }
    }

}
