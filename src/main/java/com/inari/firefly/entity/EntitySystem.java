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
import java.util.Stack;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.functional.Matcher;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFContext;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.attr.ComponentKey;
import com.inari.firefly.component.attr.EntityAttributeMap;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.entity.event.EntityActivationEvent.Type;

public class EntitySystem implements IEntitySystem {
    
    private static final int DEFAULT_CAPACITY = 100;
    private static final int DEFAULT_COMPONENT_TYPE_CAPACITY = 10;

    private IEventDispatcher eventDispatcher;
    
    private final DynArray<Entity> activeEntities;
    private final DynArray<IndexedTypeSet> usedComponents;
    
    private final Stack<Entity> inactiveEntities;
    private final DynArray<Stack<EntityComponent>> unusedComponents;

    
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

        inactiveEntities = new Stack<Entity>();
        int size = Indexer.getIndexedTypeSize( EntityComponent.class );
        if ( size < DEFAULT_COMPONENT_TYPE_CAPACITY ) {
            size = DEFAULT_COMPONENT_TYPE_CAPACITY;
        }
        unusedComponents = new DynArray<Stack<EntityComponent>>( size );
    }
    
    @Override
    public void init( FFContext context ) {
        eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
    }
    
    @Override
    public void dispose( FFContext context ) {
        activeEntities.clear();
        inactiveEntities.clear();
        usedComponents.clear();
        unusedComponents.clear();
    }

    @Override
    public void initEmptyEntities( int number ) {
        for ( int i = 0; i < number + 1; i++ ) {
            inactiveEntities.push( new Entity( -1, this ) );
        }
    }
    
    @Override
    public EntityBuilder createEntityBuilder() {
        return new EntityBuilder( this );
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public <C> ComponentBuilder<C> getComponentBuilder( Class<C> type ) {
        if ( type != Entity.class ) {
            throw new IllegalArgumentException( "The IComponentType is not a subtype of Asset." + type );
        }
        
        return (ComponentBuilder<C>) getEntityBuilder();
    }
    
    public final EntityBuilder getEntityBuilder() {
        return new EntityBuilder( this );
    }

    @Override
    public final boolean isActive( int entityId ) {
        return activeEntities.get( entityId ) != null;
    }
    
    @Override
    public void activate( int entityId ) {
        activate( activeEntities.get( entityId ) );
    }
    
    @Override
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

    @Override
    public final void restore( int entityId ) {
        if ( activeEntities.get( entityId ) != null ) {
            deactivate( entityId );
        }
        
        restoreEntityComponents( entityId );
    }
    
    @Override
    public final void restoreAll( IntIterator iterator ) {
        while( iterator.hasNext() ) {
            restore( iterator.next() );
        }
    }

    @Override
    public void restoreAll() {
        for ( Entity entity : activeEntities ) {
            restore( entity.indexedId() );
        }
    }

    @Override
    public final Entity getEntity( int entityId ) {
        return activeEntities.get( entityId );
    }

    @Override
    public final Aspect getEntityAspect( int entityId ) {
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

    @Override
    public final Iterable<Entity> entities( final Aspect aspect ) {
        return new Iterable<Entity>() {
            @Override
            public final Iterator<Entity> iterator() {
                return new AspectedEntityIterator( aspect );
            }
        };
    }
    

    @Override
    public final Iterable<Entity> entities( final Matcher<Entity> matcher ) {
        return new Iterable<Entity>() {
            @Override
            public final Iterator<Entity> iterator() {
                return new MatcherEntityIterator( matcher );
            }
        };
    }
    
    private final void activate( Entity entity ) {
        if ( entity == null || entity.isActive() ) {
            return;
        }
        int entityId = entity.indexedId();
        
        Aspect aspect = getEntityAspect( entityId );
        if ( aspect == null || aspect.isEmpty() ) {
            throw new IllegalStateException( 
                String.format( "The Entity with id: %s has no or an empty Aspect. This makes no sense and an empty Entity cannot activated", entityId ) 
            );
        }
        
        inactiveEntities.remove( entity );
        activeEntities.set( entity.indexedId(), entity ) ;
        
        if ( eventDispatcher != null ) {
            eventDispatcher.notify( 
                new EntityActivationEvent( entity.indexedId(), aspect, Type.ENTITY_ACTIVATED ) 
            );
        }
    }
    
    // ---- Components ------------------------------------------------
    
    @Override
    public void initEmptyComponents( Class<? extends EntityComponent> componentType, int number ) {
        int componentTypeIndex = Indexer.getIndexForType( componentType, EntityComponent.class );
        Stack<EntityComponent> components = (Stack<EntityComponent>) unusedComponents.get( componentTypeIndex );
        if ( components == null ) {
            components = new Stack<EntityComponent>();
            unusedComponents.set( componentTypeIndex, components );
        }
        
        for ( int i = 0; i < number + 1; i++ ) {
            components.push( createComponent( componentType ) );
        }
    }

    @Override
    public final <T extends EntityComponent> T getComponent( int entityId, Class<T> componentType ) {
        return usedComponents.get( entityId ).get( componentType );
    }

    @Override
    public final <T extends EntityComponent> T getComponent( int entityId, int componentId ) {
        return usedComponents.get( entityId ).get( componentId );
    }

    @Override
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

    @Override
    public final void fromAttributes( Attributes attributes, BuildType buildType ) {
        if ( buildType == BuildType.CLEAR_OLD ) {
            restoreAll();
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
                restore( entityId );
            }
            entityBuilder
                .setAttributes( attrsOfView )
                .buildAndNext()
                .clear();
        } 
    }

    @Override
    public final void toAttributes( Attributes attributes ) {
        for ( Entity entity : activeEntities ) {
            ComponentKey key = new ComponentKey( Entity.class, entity.getId() );
            EntityAttributeMap attributeMap = new EntityAttributeMap();
            attributeMap.setComponentKey( key );
            attributes.add( attributeMap );
            IndexedTypeSet components = getComponents( entity.getId() );
            for ( EntityComponent component : components.<EntityComponent>getIterable() ) {
                component.toAttributes( attributeMap );
            }
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
                Stack<EntityComponent> stack = unusedComponents.get( i );
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
    
    private EntityComponent getUnused( int componentTypeIndex ) {
        Stack<EntityComponent> unusedStack = unusedComponents.get( componentTypeIndex );
        if ( unusedStack.isEmpty() ) {
            return null;
        }
        
        return unusedStack.pop();
    }
    
    // ---- Utilities --------------------------------------------------------
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "EntitySystem [inactiveEntites=" ).append( inactiveEntities.size() ).append( " activeEntities=" );
        for ( Entity entity : activeEntities ) {
            builder.append( "entity{" ).append( entity.indexedId() );
            IndexedTypeSet components = getComponents( entity.indexedId() );
            if ( components != null ) {
                builder.append( ":" );
                Iterator<EntityComponent> iterator = components.<EntityComponent>getIterable().iterator();
                while ( iterator.hasNext() ) {
                    EntityComponent component = iterator.next();
                    builder.append( component.indexedType().getSimpleName() );
                    if ( iterator.hasNext() ) {
                        builder.append( "," );
                    }
                }
            }
            builder.append( "}" );
        }
        builder.append( "]" );
        return builder.toString();
    }

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
        
        private final Aspect aspect;
        
        public AspectedEntityIterator( Aspect aspect ) {
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
    
    private final class MatcherEntityIterator extends EntityIterator {
        
        private final Matcher<Entity> matcher;

        private MatcherEntityIterator( Matcher<Entity> matcher ) {
            super( activeEntities.iterator() );
            this.matcher = matcher;
            findNext();
        }

        @Override
        protected void findNext() {
            current = null;
            boolean foundNext = false;
            while ( !foundNext && delegate.hasNext() ) {
                Entity next = delegate.next();
                if ( matcher.match( next ) ) {
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
            
            IndexedTypeSet components;
            components = getComponents( entity.indexedId() );
            if ( components == null ) {
                components = new IndexedTypeSet( EntityComponent.class );
                usedComponents.set( entity.indexedId(), components );
            }
            
            // if we have prefab components we set them first
            if ( prefabComponents != null ) {
                for ( EntityComponent component : prefabComponents.<EntityComponent>getIterable() ) {
                    components.set( component );
                }
            }
            
            Set<Class<? extends Component>> componentTypes = ( (EntityAttributeMap) attributes ).getEntityComponentTypes();
            for ( Class<? extends Component> componentType : componentTypes ) {
                if ( !EntityComponent.class.isAssignableFrom( componentType ) ) {
                    continue;
                }
                
                @SuppressWarnings( "unchecked" )
                EntityComponent component = newComponent( (Class<? extends EntityComponent>) componentType, attributes );
                components.set( component );
            }
            
            return entity;
        }
    }

}
