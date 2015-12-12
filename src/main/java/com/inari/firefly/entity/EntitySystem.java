/*******************************************************************************
 * Copyright (c) 2015 - 2016, Andreas Hefti, inarisoft@yahoo.de 
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

import java.util.BitSet;
import java.util.Iterator;

import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.aspect.AspectBitSet;
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

public final class EntitySystem extends ComponentSystem<EntitySystem> {
    
    public static final FFSystemTypeKey<EntitySystem> SYSTEM_KEY = FFSystemTypeKey.create( EntitySystem.class );
    public static final SystemComponentKey ENTITY_TYPE_KEY = SystemComponentKey.create( Entity.class );

    private static final SystemComponentKey[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        ENTITY_TYPE_KEY
    };

    private static final int INIT_SIZE = 1000;

    private EntityProvider entityProvider;
    
    final BitSet activeEntities;
    final BitSet inactiveEntities;
    final DynArray<IndexedTypeSet> components;
    
    EntitySystem() {
        super( SYSTEM_KEY );
        activeEntities = new BitSet( INIT_SIZE );
        inactiveEntities = new BitSet( INIT_SIZE );
        components = new DynArray<IndexedTypeSet>( INIT_SIZE );
    }
    
    @Override
    public void init( FFContext context ) {
        super.init( context );
        
        entityProvider = context.getSystem( EntityProvider.SYSTEM_KEY );

        Integer entityCapacity = context.getProperty( FFContext.Properties.ENTITY_MAP_CAPACITY );
        if ( entityCapacity != null ) {
            activeEntities.flip( entityCapacity );
            activeEntities.flip( entityCapacity );
            inactiveEntities.flip( entityCapacity );
            inactiveEntities.flip( entityCapacity );
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
        return activeEntities.get( entityId );
    }

    public final boolean isRestored( int entityId ) {
        return !activeEntities.get( entityId ) && inactiveEntities.get( entityId );
    }
    
    public final void activateEntity( int entityId ) {
        if ( activeEntities.get( entityId ) || !inactiveEntities.get( entityId ) ) {
            return;
        }
        
        inactiveEntities.clear( entityId );
        activeEntities.set( entityId ) ;
        
        AspectBitSet aspect = getAspect( entityId );
        context.notify( 
            new EntityActivationEvent( entityId, aspect, Type.ENTITY_ACTIVATED ) 
        );
    }
    
    public final void deactivateEntity( int entityId ) {
        if ( !activeEntities.get( entityId ) ) {
            return;
        }
        
        activeEntities.clear( entityId );
        inactiveEntities.set( entityId );
        
        context.notify( 
            new EntityActivationEvent( entityId, getAspect( entityId ), Type.ENTITY_DEACTIVATED ) 
        );
    }
    
    public final void deleteEtity( int entityId ) {
        delete( entityId );
    }

    public final void delete( int entityId ) {
        if ( activeEntities.get( entityId ) ) {
            deactivateEntity( entityId );
        }
        
        if ( !inactiveEntities.get( entityId ) ) {
            return;
        }

        inactiveEntities.clear( entityId );
        IndexedTypeSet componentsToRestore = components.remove( entityId );

        entityProvider.disposeComponentSet( componentsToRestore );
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
        for ( int i = activeEntities.nextSetBit( 0 ); i >= 0; i = activeEntities.nextSetBit( i+1 ) ) {
            delete( i );
        }
    }
    
    @Override
    public final void clear() {
        deleteAll();
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
    
    public final IntIterator entities() {
        return new ActiveEntityIterator();
    }
    
    public final IntIterator entities( boolean active ) {
        return ( active )?  new ActiveEntityIterator() : new InactiveEntityIterator();
    }

    public final IntIterator entities( final AspectBitSet aspect ) {
        return new AspectedEntityIterator( aspect );
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
    
    private void entityToAttribute( Attributes attributes, int entityId ) {
        ComponentKey key = new ComponentKey( Entity.class, entityId );
        EntityAttributeMap attributeMap = new EntityAttributeMap();
        attributeMap.setComponentKey( key );
        attributes.add( attributeMap );
        IndexedTypeSet components = getComponents( entityId );
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
                if ( onlyActive && !activeEntities.get( nextEntityIndex ) ) {
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
    
    private abstract class EntityIterator implements IntIterator {
        
        protected int nextEntityId = -1;
        protected int currentEntityId = -1;

        @Override
        public boolean hasNext() {
            return nextEntityId >= 0;
        }

        @Override
        public int next() {
            currentEntityId = nextEntityId;
            findNext();
            return currentEntityId;
        }

        protected abstract void findNext();
    }
    
    private final class ActiveEntityIterator extends EntityIterator {
        ActiveEntityIterator() { findNext(); }
        @Override
        protected void findNext() {
            nextEntityId = activeEntities.nextSetBit( nextEntityId + 1 );
        }
    } 
    
    private final class InactiveEntityIterator extends EntityIterator {
        InactiveEntityIterator() { findNext(); }
        @Override
        protected void findNext() {
            nextEntityId = inactiveEntities.nextSetBit( nextEntityId + 1 );
        }
    } 
    
    private final class AspectedEntityIterator extends EntityIterator {
        
        private final AspectBitSet aspect;
        
        public AspectedEntityIterator( AspectBitSet aspect ) {
            this.aspect = aspect;
            findNext();
        }

        @Override
        protected void findNext() {
            while ( nextEntityId < activeEntities.size() ) {
                nextEntityId++;
                if ( getAspect( nextEntityId ).include( aspect ) ) {
                    return;
                }
            }
            nextEntityId = -1;
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
            int entityId = componentId;
            if ( entityId < 0 ) {
                entityId = Indexer.nextObjectIndex( Entity.class );
            }

            IndexedTypeAspectSet aspectToCheck;
            if ( prefabComponents != null ) {
                // if we have prefab components we use them
                components.set( entityId, prefabComponents );
                aspectToCheck = prefabComponents.getAspect();
            } else {
                IndexedTypeSet componentSet = entityProvider.getComponentTypeSet();
                components.set( entityId, componentSet );
                entityProvider.createComponents( componentSet, (EntityAttributeMap) attributes );
                aspectToCheck = componentSet.getAspect();
            }

            if ( aspectToCheck == null || !aspectToCheck.valid() ) {
                throw new IllegalStateException( 
                    String.format( "The Entity %s has no or an empty Aspect. This makes no sense and an empty Entity cannot activated", entityId ) 
                );
            }
            
            inactiveEntities.set( entityId );
            
            if ( activate ) {
                activateEntity( entityId );
            }
            
            return entityId;
        }

        @Override
        public final SystemComponentKey systemComponentKey() {
            return ENTITY_TYPE_KEY;
        }
    }
    
    private final class EntityBuilderHelper extends SystemBuilderAdapter<Entity> {
        
        public EntityBuilderHelper( EntitySystem system ) {
            super( system, getEntityBuilder() );
        }
        @Override
        public final SystemComponentKey componentTypeKey() {
            return ENTITY_TYPE_KEY;
        }
        @Override
        public Entity get( int id, Class<? extends Entity> subtype ) {
            return null;
        }
        @Override
        public void delete( int id, Class<? extends Entity> subtype ) {
        }
        @Override
        public final Iterator<Entity> getAll() {
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
                if ( entityId < 0 ) {
                    continue;
                }
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
                    .buildAndNext()
                    .clear();
            }
            
            AttributeMap attributeMap = attributes.get( Entity.ACTIVE_ENTITIES_IDS_KEY );
            if ( attributeMap == null ) {
                return;
            }
            String activeEntityIdsString = attributeMap.getValue( Entity.ACTIVE_ENTITY_IDS );
            IntBag activeEntityIds = new IntBag();
            activeEntityIds.fromConfigString( activeEntityIdsString );
            IntIterator iterator = activeEntityIds.iterator();
            while ( iterator.hasNext() ) {
                activateEntity( iterator.next() );
            }
        }

        @Override
        public final void toAttributes( Attributes attributes ) {
            IntBag activeEntityIds = new IntBag( activeEntities.cardinality(), -1 );
            IntIterator active = entities();
            while ( active.hasNext() ) {
                int entityId = active.next();
                entityToAttribute( attributes, entityId );
                activeEntityIds.add( entityId );
            }
            
            EntityAttributeMap attributeMap = new EntityAttributeMap();
            attributeMap.setComponentKey( Entity.ACTIVE_ENTITIES_IDS_KEY );
            attributeMap.put( Entity.ACTIVE_ENTITY_IDS, activeEntityIds.toConfigString() );
            attributes.add( attributeMap );
            
            IntIterator inactive = entities( false );
            while ( active.hasNext() ) {
                int entityId = inactive.next();
                entityToAttribute( attributes, entityId );
            }
        }
    }
    
    final static class Entity extends SystemComponent implements IndexedType {
        
        static final AttributeKey<String> ACTIVE_ENTITY_IDS = new AttributeKey<String>( "ACTIVE_ENTITY_IDS", String.class, Entity.class );
        static final ComponentKey ACTIVE_ENTITIES_IDS_KEY = new ComponentKey( Entity.class, -1 );
        
        protected Entity() {
            super( 0 );
        }

        @Override
        public final IndexedTypeKey indexedTypeKey() {
            return ENTITY_TYPE_KEY;
        }
    }

}
