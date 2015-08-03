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

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.aspect.IndexedAspect;
import com.inari.commons.lang.functional.Predicate;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.Disposable;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.attr.ComponentKey;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.entity.event.EntityActivationEvent.Type;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFSystem;

public final class EntitySystem implements FFSystem, ComponentSystem, ComponentBuilderFactory, Disposable, Iterable<Entity> {
    
    private static final int DEFAULT_CAPACITY = 1000;
    private static final int DEFAULT_COMPONENT_TYPE_CAPACITY = 20;

    private IEventDispatcher eventDispatcher;
    
    final DynArray<Entity> activeEntities;
    final DynArray<IndexedTypeSet> usedComponents;
    
    final ArrayDeque<Entity> inactiveEntities;
    final DynArray<ArrayDeque<EntityComponent>> unusedComponents;

    
    EntitySystem() {
        this( DEFAULT_CAPACITY );
    }
    
    EntitySystem( int initialCapacity ) {
        if ( initialCapacity < 0 ) {
            initialCapacity = DEFAULT_CAPACITY;
        }
        
        activeEntities = new DynArray<Entity>( initialCapacity );
        usedComponents = new DynArray<IndexedTypeSet>( initialCapacity );
        for ( int i = 0; i < usedComponents.capacity(); i++ ) {
            usedComponents.set( i, new IndexedTypeSet( EntityComponent.class ) );
        }

        inactiveEntities = new ArrayDeque<Entity>();
        int size = Indexer.getIndexedTypeSize( EntityComponent.class );
        if ( size < DEFAULT_COMPONENT_TYPE_CAPACITY ) {
            size = DEFAULT_COMPONENT_TYPE_CAPACITY;
        }
        unusedComponents = new DynArray<ArrayDeque<EntityComponent>>( size );
    }
    
    @Override
    public void init( FFContext context ) {
        eventDispatcher = context.get( FFContext.EVENT_DISPATCHER );
    }
    
    @Override
    public void dispose( FFContext context ) {
        activeEntities.clear();
        inactiveEntities.clear();
        usedComponents.clear();
        unusedComponents.clear();
    }

    public final void initEmptyEntities( int number ) {
        for ( int i = 0; i < number + 1; i++ ) {
            inactiveEntities.push( new Entity( -1, this ) );
        }
    }
    
    public final EntityBuilder createEntityBuilder() {
        return new EntityBuilder( this );
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
        return new EntityBuilder( this );
    }

    public final boolean isActive( int entityId ) {
        return activeEntities.get( entityId ) != null;
    }
    
    public final void activate( int entityId ) {
        activate( activeEntities.get( entityId ) );
    }
    
    public final void deactivate( int entityId ) {
        Entity entity = activeEntities.remove( entityId );
        if ( entity == null ) {
            return;
        }
        
        inactiveEntities.add( entity );
        
        if ( eventDispatcher != null ) {
            eventDispatcher.notify( 
                new EntityActivationEvent( entityId, getEntityAspect( entityId ), Type.ENTITY_DEACTIVATED ) 
            );
        }
    }

    public final void delete( int entityId ) {
        if ( activeEntities.get( entityId ) != null ) {
            deactivate( entityId );
        }
        
        restoreEntityComponents( entityId );
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

    public final IndexedAspect getEntityAspect( int entityId ) {
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

    public final Iterable<Entity> entities( final IndexedAspect aspect ) {
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
    
    private final void activate( Entity entity ) {
        if ( entity == null || entity.isActive() ) {
            return;
        }
        int entityId = entity.index();
        
        IndexedAspect aspect = getEntityAspect( entityId );
        if ( aspect == null || !aspect.valid() ) {
            throw new IllegalStateException( 
                String.format( "The Entity with id: %s has no or an empty Aspect. This makes no sense and an empty Entity cannot activated", entityId ) 
            );
        }
        
        inactiveEntities.remove( entity );
        activeEntities.set( entity.index(), entity ) ;
        
        if ( eventDispatcher != null ) {
            eventDispatcher.notify( 
                new EntityActivationEvent( entity.index(), aspect, Type.ENTITY_ACTIVATED ) 
            );
        }
    }
    
    // ---- Components ------------------------------------------------
    
    public final void initEmptyComponents( Class<? extends EntityComponent> componentType, int number ) {
        int componentTypeIndex = Indexer.getIndexForType( componentType, EntityComponent.class );
        ArrayDeque<EntityComponent> components = (ArrayDeque<EntityComponent>) unusedComponents.get( componentTypeIndex );
        if ( components == null ) {
            components = new ArrayDeque<EntityComponent>();
            unusedComponents.set( componentTypeIndex, components );
        }
        
        for ( int i = 0; i < number + 1; i++ ) {
            components.push( createComponent( componentType ) );
        }
    }

    public final <T extends EntityComponent> T getComponent( int entityId, Class<T> componentType ) {
        return usedComponents.get( entityId ).get( componentType );
    }

    public final <T extends EntityComponent> T getComponent( int entityId, int componentId ) {
        return usedComponents.get( entityId ).get( componentId );
    }

    public final IndexedTypeSet getComponents( int entityId ) {
        if ( activeEntities.get( entityId ) == null ) {
            return null;
        }
        
        return usedComponents.get( entityId );
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
        for ( AttributeMap attrsOfView : attributes.getAllOfType( Entity.class ) ) {
            int entityId = attrsOfView.getComponentKey().getId();
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
                .setAttributes( attrsOfView )
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
    }
    
    // ---- Internal -----------------------------------------------------------
    
    private void restoreEntityComponents( int entityId ) {
        IndexedTypeSet componentsOfEntity = getComponents( entityId );
        if ( componentsOfEntity == null ) {
            return;
        }
        
        for ( int i = 0; i < componentsOfEntity.length(); i++ ) {
            EntityComponent component = componentsOfEntity.get( i );
            if ( component != null ) {
                componentsOfEntity.remove( i );
                ArrayDeque<EntityComponent> stack = unusedComponents.get( i );
                stack.push( component );
            }
        }
    }
    
    private <C extends EntityComponent> EntityComponent newComponent( Class<C> componentType ) {
        int componentTypeIndex = Indexer.getIndexForType( componentType, EntityComponent.class );
        
        EntityComponent result = getUnused( componentTypeIndex );
        if ( result != null ) {
            return result;
        }
        
        return createComponent( componentType );
    }
    
    private <C extends EntityComponent> EntityComponent newComponent( Class<C> componentType, AttributeMap attributes ) {
        EntityComponent newComponent = newComponent( componentType );
        newComponent.fromAttributes( attributes );
        return newComponent;
    }
    
    private EntityComponent createComponent( Class<? extends EntityComponent> componentType ) {
        try {
            return componentType.newInstance();
        } catch ( Exception e ) {
            throw new ComponentCreationException( "Unknwon error while Component creation: " + componentType, e );
        }
    }
    
    EntityComponent getUnused( int componentTypeIndex ) {
        ArrayDeque<EntityComponent> unusedStack = unusedComponents.get( componentTypeIndex );
        if ( unusedStack.isEmpty() ) {
            return null;
        }
        
        return unusedStack.pop();
    }
    
    void createComponents( IndexedTypeSet components, EntityAttributeMap attributes ) {
        Set<Class<? extends EntityComponent>> componentTypes = ( (EntityAttributeMap) attributes ).getEntityComponentTypes();
        for ( Class<? extends EntityComponent> componentType : componentTypes ) {
            EntityComponent component = newComponent( componentType, attributes );
            components.set( component );
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
        
        private final IndexedAspect aspect;
        
        public AspectedEntityIterator( IndexedAspect aspect ) {
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
                if ( next.getAspect().include( aspect ) ) {
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
    
    private final Entity createEntity( int componentId ) {
        if ( componentId >= 0 ) {
            return new Entity( componentId, this );
        }
        if ( inactiveEntities.isEmpty() ) {
            return new Entity( -1, this );
        }
        
        Entity entity = inactiveEntities.pop();
        entity.restore();
        return entity;
    }
    
    
    protected final class EntityBuilder extends BaseComponentBuilder<Entity> {
        
        private IndexedTypeSet prefabComponents;

        private EntityBuilder( EntitySystem system ) {
            super( system, new EntityAttributeMap() );
        }
        
        public EntityBuilder setPrefabComponents( IndexedTypeSet prefabComponents ) {
            this.prefabComponents = prefabComponents;
            return this;
        }
        
        @Override
        public Entity build( int componentId ) {
            Entity entity = createEntity( componentId );

            if ( prefabComponents != null ) {
                // if we have prefab components we use them
                usedComponents.set( entity.index(), prefabComponents );
            } else {
                // otherwise we get component which either gets unused or create new one
                IndexedTypeSet components = getComponents( entity.index() );
                if ( components == null ) {
                    components = new IndexedTypeSet( EntityComponent.class );
                    usedComponents.set( entity.index(), components );
                }
     
                createComponents( components, (EntityAttributeMap) attributes );
            }
            
            return entity;
        }

        
    }

}
