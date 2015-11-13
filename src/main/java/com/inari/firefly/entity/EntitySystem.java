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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.aspect.AspectBitSet;
import com.inari.commons.lang.functional.Predicate;
import com.inari.commons.lang.indexed.IndexedTypeAspectSet;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.attr.ComponentKey;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.entity.event.EntityActivationEvent.Type;
import com.inari.firefly.system.FFContext;

public final class EntitySystem implements ComponentSystem, Iterable<Entity> {
    
    public static final TypedKey<EntitySystem> CONTEXT_KEY = TypedKey.create( "FF_ENTITY_SYSTEM", EntitySystem.class );

    private IEventDispatcher eventDispatcher;
    private EntityProvider entityProvider;
    
    final DynArray<Entity> activeEntities;
    final DynArray<Entity> inactiveEntities;
    final DynArray<IndexedTypeSet> components;
    
    EntitySystem() {
        activeEntities = new DynArray<Entity>();
        inactiveEntities = new DynArray<Entity>();
        components = new DynArray<IndexedTypeSet>();
    }
    
    @Override
    public void init( FFContext context ) {
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        entityProvider = context.getComponent( FFContext.ENTITY_PROVIDER );

        Integer entityCapacity = context.getProperty( FFContext.Properties.ENTITY_MAP_CAPACITY );
        if ( entityCapacity != null ) {
            activeEntities.ensureCapacity( entityCapacity );
            inactiveEntities.ensureCapacity( entityCapacity );
            components.ensureCapacity( entityCapacity );
        }
    }
    
    @Override
    public void dispose( FFContext context ) {
        eventDispatcher = null;
        entityProvider = null;

        activeEntities.clear();
        inactiveEntities.clear();
        components.clear();
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public final <C> ComponentBuilder<C> getComponentBuilder( Class<C> type ) {
        if ( type != Entity.class ) {
            throw new IllegalArgumentException( "The IComponentType is not supported: " + type );
        }
        
        return (ComponentBuilder<C>) getEntityBuilder();
    }
    
    public final EntityBuilder getEntityBuilder() {
        return new EntityBuilder( this, false );
    }
    
    public final EntityBuilder getEntityBuilderWithAutoActivation() {
        return new EntityBuilder( this, true );
    }

    public final boolean isActive( int entityId ) {
        return activeEntities.get( entityId ) != null;
    }

    public final boolean isRestored( int entityId ) {
        return !( activeEntities.contains( entityId ) && inactiveEntities.contains( entityId ) );
    }
    
    public final void activate( int entityId ) {
        if ( activeEntities.contains( entityId ) || !inactiveEntities.contains( entityId ) ) {
            return;
        }
        
        Entity entity = inactiveEntities.remove( entityId );
        activeEntities.set( entityId, entity ) ;
        
        if ( eventDispatcher != null ) {
            AspectBitSet aspect = getAspect( entityId );
            eventDispatcher.notify( 
                new EntityActivationEvent( entity.index(), aspect, Type.ENTITY_ACTIVATED ) 
            );
        }
    }
    
    public final void deactivate( int entityId ) {
        if ( !activeEntities.contains( entityId ) ) {
            return;
        }
        
        Entity entity = activeEntities.remove( entityId );
        inactiveEntities.set( entity.index(), entity );
        
        if ( eventDispatcher != null ) {
            eventDispatcher.notify( 
                new EntityActivationEvent( entityId, getAspect( entityId ), Type.ENTITY_DEACTIVATED ) 
            );
        }
    }

    public final void delete( int entityId ) {
        if ( activeEntities.contains( entityId ) ) {
            deactivate( entityId );
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

    public final Entity getEntity( int entityId ) {
        return activeEntities.get( entityId );
    }
    
    public final int getEntityId( String name ) {
        if ( name == null ) {
            return -1;
        }
        
        for ( Entity activeEntity : activeEntities ) {
            if ( name.equals( activeEntity.getName() ) ) {
                return activeEntity.getId();
            }
        }
        for ( Entity inactiveEntity : inactiveEntities ) {
            if ( name.equals( inactiveEntity.getName() ) ) {
                return inactiveEntity.getId();
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

    public final IndexedTypeSet getComponents( int entityId ) {
        return components.get( entityId );
    }
    
    // ---- ComponentSystem implementation --------------------------------------
    
    private static final Set<Class<?>> SUPPORTED_COMPONENT_TYPES = new HashSet<Class<?>>();
    @Override
    public final Set<Class<?>> supportedComponentTypes() {
        if ( SUPPORTED_COMPONENT_TYPES.isEmpty() ) {
            SUPPORTED_COMPONENT_TYPES.add( Entity.class );
        }
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final void fromAttributes( Attributes attributes ) {
        fromAttributes( attributes, BuildType.CLEAR_OLD );
    }
    
    static final AttributeKey<String> ACTIVE_ENTITY_IDS = new AttributeKey<String>( "ACTIVE_ENTITY_IDS", String.class, ActiveEntitiesComponent.class );
    static interface ActiveEntitiesComponent extends Component{}
    static final ComponentKey ACTIVE_ENTITIES_IDS_KEY = new ComponentKey( ActiveEntitiesComponent.class, 0 );

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
                delete( entityId );
            }
            entityBuilder
                .setAttributes( entityAttrs )
                .buildAndNext()
                .clear();
        }
        
        AttributeMap attributeMap = attributes.get( ACTIVE_ENTITIES_IDS_KEY );
        if ( attributeMap == null ) {
            return;
        }
        String activeEntityIdsString = attributeMap.getValue( ACTIVE_ENTITY_IDS );
        IntBag activeEntityIds = new IntBag();
        activeEntityIds.fromConfigString( activeEntityIdsString );
        IntIterator iterator = activeEntityIds.iterator();
        while ( iterator.hasNext() ) {
            activate( iterator.next() );
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
        attributeMap.setComponentKey( ACTIVE_ENTITIES_IDS_KEY );
        attributes.add( attributeMap );
        attributeMap.put( ACTIVE_ENTITY_IDS, activeEntityIds.toConfigString() );
        
        for ( Entity entity : inactiveEntities ) {
            entityToAttribute( attributes, entity );
        }
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
        
        if ( entity.getName() != null ) {
            attributeMap.put( Entity.NAME, entity.getName() );
        }
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
    
    
    public final class EntityBuilder extends BaseComponentBuilder<Entity> {
        
        private IndexedTypeSet prefabComponents;
        private final boolean autoActivate;

        private EntityBuilder( EntitySystem system, boolean autoActivate ) {
            super( system, new EntityAttributeMap() );
            this.autoActivate = autoActivate;
        }
        
        public EntityBuilder setPrefabComponents( IndexedTypeSet prefabComponents ) {
            this.prefabComponents = prefabComponents;
            return this;
        }
        
        @Override
        public Entity build( int componentId ) {
            Entity entity = entityProvider.getEntity( componentId );
            
            if ( attributes.contains( Entity.NAME ) ) {
                entity.setName( attributes.getValue( Entity.NAME ) );
            }
            
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
            
            if ( autoActivate ) {
                activate( entity.index() );
            }
            
            return entity;
        }
        
    }

}
