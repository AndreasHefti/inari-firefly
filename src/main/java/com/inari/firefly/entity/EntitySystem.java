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
import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.IndexedType;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.commons.lang.list.IntBagRO;
import com.inari.firefly.component.ComponentId;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.control.Controller;
import com.inari.firefly.entity.EntityActivationEvent.Type;
import com.inari.firefly.entity.EntityComponent.EntityComponentTypeKey;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public final class EntitySystem extends ComponentSystem<EntitySystem> {
    
    public static final FFSystemTypeKey<EntitySystem> SYSTEM_KEY = FFSystemTypeKey.create( EntitySystem.class );
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        Entity.ENTITY_TYPE_KEY
    );

    private static final int INIT_SIZE = 1000;

    private EntityProvider entityProvider;
    
    final BitSet activeEntities;
    final BitSet inactiveEntities;
    final DynArray<IndexedTypeSet> components;
    
    EntitySystem() {
        super( SYSTEM_KEY );
        activeEntities = new BitSet( INIT_SIZE );
        inactiveEntities = new BitSet( INIT_SIZE );
        components = DynArray.create( IndexedTypeSet.class, INIT_SIZE, 100 );
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
        clearSystem();
    }
    
    public final EntityBuilder getEntityBuilder() {
        return new EntityBuilder();
    }

    public final boolean isActive( int entityId ) {
        if ( entityId < 0 ) {
            return false;
        }
        
        return activeEntities.get( entityId );
    }

    public final boolean isRestored( int entityId ) {
        if ( entityId < 0 ) {
            return false;
        }
        
        return !activeEntities.get( entityId ) && inactiveEntities.get( entityId );
    }
    
    public final void activateEntity( int entityId ) {
        if ( entityId < 0 ) {
            return;
        }
        
        if ( activeEntities.get( entityId ) || !inactiveEntities.get( entityId ) ) {
            return;
        }
        
        inactiveEntities.clear( entityId );
        activeEntities.set( entityId );
        final Aspects aspect = getEntityComponentAspects( entityId );
        if ( aspect.contains( EEntity.TYPE_KEY ) ) {
            notifyEntityController( entityId, true );
        }
        
        context.notify( EntityActivationEvent.create( entityId, Type.ENTITY_ACTIVATED, aspect ) );
    }
    
    public final void deactivateEntity( int entityId ) {
        if ( entityId < 0 ) {
            return;
        }
        
        if ( !activeEntities.get( entityId ) ) {
            return;
        }
        
        activeEntities.clear( entityId );
        inactiveEntities.set( entityId );
        
        final Aspects aspect = getEntityComponentAspects( entityId );
        if ( aspect.contains( EEntity.TYPE_KEY ) ) {
            notifyEntityController( entityId, false );
        }
        
        context.notify( EntityActivationEvent.create( entityId, Type.ENTITY_DEACTIVATED, getEntityComponentAspects( entityId ) ) );
    }
    
    
    
    public final void deleteEntity( int entityId ) {
        delete( entityId );
    }

    public final void delete( int entityId ) {
        if ( entityId < 0 ) {
            return;
        }
        
        if (  activeEntities.get( entityId ) ) {
            deactivateEntity( entityId );
        }
        
        if ( !inactiveEntities.get( entityId ) ) {
            return;
        }

        deleteSilently( entityId );
    }
    
    private final void deleteSilently( int entityId ) {
        activeEntities.clear( entityId );
        inactiveEntities.clear( entityId );
        IndexedTypeSet componentsToRestore = components.remove( entityId );
        entityProvider.disposeComponentSet( componentsToRestore );
        Indexer.disposeObjectIndex( Entity.class, entityId );
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

    public final void deleteAllActive() {
        for ( int i = activeEntities.nextSetBit( 0 ); i >= 0; i = activeEntities.nextSetBit( i+1 ) ) {
            delete( i );
        }
    }
    
    @Override
    public final void clearSystem() {
        for ( int i = activeEntities.nextSetBit( 0 ); i >= 0; i = activeEntities.nextSetBit( i+1 ) ) {
            deleteSilently( i );
        }
        
        for ( int i = inactiveEntities.nextSetBit( 0 ); i >= 0; i = inactiveEntities.nextSetBit( i+1 ) ) {
            deleteSilently( i );
        }
        
        activeEntities.clear();
        inactiveEntities.clear();
        components.clear();
    }
    
    public final int getEntityId( String name ) {
        if ( name == null ) {
            return -1;
        }
        
        int entityAspect = EEntity.TYPE_KEY.index();
        for ( int i = 0; i < components.capacity(); i++ ) {
            IndexedTypeSet comps = components.get( i );
            if ( comps == null || !comps.contains( entityAspect ) ) {
                continue;
            }
            
            EEntity entity = comps.get( entityAspect );
            if ( name.equals( entity.getEntityName() ) ) {
                return i;
            }
        }
        
        return -1;
    }

    public final Aspects getEntityComponentAspects( int entityId ) {
        IndexedTypeSet components = getComponents( entityId );
        if ( components == null ) {
            return null;
        }
        
        return components.getAspect();
    }

    public final EntityIterator entities() {
        return new EntityIterator( new ActiveEntityCondition() );
    }
    
    public final EntityIterator entities( boolean active ) {
        return ( active )?  new EntityIterator( new ActiveEntityCondition() ) : new EntityIterator( new InactiveEntityCondition() );
    }

    public final EntityIterator entities( final Aspects aspects ) {
        return new EntityIterator( new AspectedEntityCondition( aspects ) );
    }

    public final <T extends EntityComponent> T getComponent( int entityId, EntityComponentTypeKey<T> componentType ) {
        return components.get( entityId ).get( componentType );
    }
    
    public final <T extends EntityComponent> T getComponent( String entityName, EntityComponentTypeKey<T> componentType ) {
        int entityId = getEntityId( entityName );
        if ( entityId < 0 ) {
            return null;
        }
        return components.get( entityId ).get( componentType );
    }

    public final IndexedTypeSet getComponents( int entityId ) {
        return components.get( entityId );
    }
    
    // ---- ComponentSystem implementation --------------------------------------
    
    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            new EntityBuilderHelper()
        );
    }
    
    private void entityToAttribute( Attributes attributes, int entityId ) {
        ComponentId componentId = new ComponentId( Entity.ENTITY_TYPE_KEY, entityId );
        EntityAttributeMap attributeMap = new EntityAttributeMap( context );
        attributeMap.setComponentId( componentId );
        attributes.add( attributeMap );
        IndexedTypeSet components = getComponents( entityId );
        for ( EntityComponent component : components.<EntityComponent>getIterable() ) {
            component.toAttributes( attributeMap );
        }
    }
    
    private void notifyEntityController( int entityId, boolean activated ) {
        final IntBagRO controllerIds = getComponent( entityId, EEntity.TYPE_KEY ).getControllerIds();
        for ( int i = 0; i < controllerIds.length(); i++ ) {
            if ( controllerIds.isEmpty( i ) ) {
                continue;
            }
            
            final EntityController entityController = context.getSystemComponent( Controller.TYPE_KEY, controllerIds.get( i ), EntityController.class );
            if ( entityController != null ) {
                if ( activated ) {
                    entityController.entityActivated( entityId );
                } else {
                    entityController.entityDeactivated( entityId );
                }
            }
        }
    }
    
    // ---- Utilities --------------------------------------------------------
    
    private interface EntityIteratorCondition {
        
        int findNext( int currentIndex );
        
    }
    
    public final static class EntityIterator implements IntIterator {
        
        private int nextEntityId = -1;
        private final EntityIteratorCondition condition;
        
        EntityIterator( EntityIteratorCondition condition ) {
            this.condition = condition; 
            reset();
        }
        
        public final void reset() {
            nextEntityId = -1;
            nextEntityId = condition.findNext( nextEntityId );
        }

        @Override
        public final boolean hasNext() {
            return nextEntityId >= 0;
        }

        @Override
        public final int next() {
            int currentEntityId = nextEntityId;
            nextEntityId = condition.findNext( nextEntityId );
            return currentEntityId;
        }
    }

    private final class ActiveEntityCondition implements EntityIteratorCondition {
        @Override
        public int findNext( int currentIndex ) {
            return activeEntities.nextSetBit( currentIndex + 1 );
        }
    } 
    
    private final class InactiveEntityCondition implements EntityIteratorCondition {
        @Override
        public int findNext( int currentIndex ) {
            return inactiveEntities.nextSetBit( currentIndex + 1 );
        }
    } 
    
    private final class AspectedEntityCondition  implements EntityIteratorCondition {
        private final Aspects aspects;
        public AspectedEntityCondition( Aspects aspects ) {
            this.aspects = aspects;
        }
        @Override
        public int findNext( int currentIndex ) {
            while ( currentIndex < activeEntities.size() ) {
                currentIndex = activeEntities.nextSetBit( currentIndex + 1 );
                if ( currentIndex < 0 || getEntityComponentAspects( currentIndex ).include( aspects ) ) {
                    return currentIndex;
                }
            }
            
            return -1;
        }
    }
    
    public final class EntityBuilder extends SystemComponentBuilder {
        
        private IndexedTypeSet prefabComponents;

        private EntityBuilder() {
            super( EntitySystem.this.context, new EntityAttributeMap( EntitySystem.this.context ) );
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

            Aspects aspectToCheck;
            if ( prefabComponents != null ) {
                // if we have prefab components we use them
                components.set( entityId, prefabComponents );
                aspectToCheck = prefabComponents.getAspect();
            } else {
                IndexedTypeSet componentSet = entityProvider.getComponentTypeSet();
                components.set( entityId, componentSet );
                entityProvider.initAttributesOnController( (EntityAttributeMap) attributes );
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
        public final SystemComponentKey<Entity> systemComponentKey() {
            return Entity.ENTITY_TYPE_KEY;
        }
    }
    
    private final class EntityBuilderHelper extends SystemBuilderAdapter<Entity> {
        
        private EntityBuilderHelper() {
            super( EntitySystem.this, Entity.ENTITY_TYPE_KEY );
        }

        public final SystemComponentBuilder createComponentBuilder( Class<? extends Entity> componentType ) {
            return new EntityBuilder();
        }

        public final Entity get( int id ) {
            throw new UnsupportedOperationException();
        }

        public final void delete( int id ) {
            throw new UnsupportedOperationException();
        }

        public final Iterator<Entity> getAll() {
            throw new UnsupportedOperationException();
        }
        @Override
        public final void fromAttributes( Attributes attributes, BuildType buildType ) {
            if ( buildType == BuildType.CLEAR_OLD ) {
                deleteAllActive();
            }
            
            EntityBuilder entityBuilder = getEntityBuilder();
            for ( AttributeMap entityAttrs : attributes.getAllOfType( Entity.class ) ) {
                int entityId = entityAttrs.getComponentId().index();
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
                    deleteEntity( entityId );
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

            IntBag activeEntityIds = attributeMap.getValue( Entity.ACTIVE_ENTITY_IDS );
            for ( int i = 0; i < activeEntityIds.length(); i++ ) {
                if ( activeEntityIds.isEmpty( i ) ) {
                    continue;
                }
                activateEntity( activeEntityIds.get( i ) );
            }
        }

        @Override
        public final void toAttributes( Attributes attributes, FFContext context ) {
            IntBag activeEntityIds = new IntBag( activeEntities.cardinality(), -1 );
            IntIterator active = entities();
            while ( active.hasNext() ) {
                int entityId = active.next();
                entityToAttribute( attributes, entityId );
                activeEntityIds.add( entityId );
            }
            
            EntityAttributeMap attributeMap = new EntityAttributeMap( context );
            attributeMap.setComponentId( Entity.ACTIVE_ENTITIES_IDS_KEY );
            attributeMap.put( Entity.ACTIVE_ENTITY_IDS, activeEntityIds );
            attributes.add( attributeMap );
            
            IntIterator inactive = entities( false );
            while ( active.hasNext() ) {
                int entityId = inactive.next();
                entityToAttribute( attributes, entityId );
            }
        }
        
        public final int getId( String name ) {
            return getEntityId( name );
        }
        
        public final void activate( int id ) {
            activateEntity( id );
        }
        
        public final void deactivate( int id ) {
            deactivateEntity( id );
        }

        public final boolean isActive( int id ) {
            return EntitySystem.this.isActive( id );
        }
    }
    
    public final static class Entity extends SystemComponent implements IndexedType {
        
        public static final SystemComponentKey<Entity> ENTITY_TYPE_KEY = SystemComponentKey.create( Entity.class );
        static final AttributeKey<IntBag> ACTIVE_ENTITY_IDS = AttributeKey.createIntBag( "ACTIVE_ENTITY_IDS", Entity.class );
        static final ComponentId ACTIVE_ENTITIES_IDS_KEY = new ComponentId( ENTITY_TYPE_KEY, -1 );
        
        Entity() {
            super( 0 );
        }

        @Override
        public final IndexedTypeKey indexedTypeKey() {
            return ENTITY_TYPE_KEY;
        }
    }

}
