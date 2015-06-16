package com.inari.firefly.entity;

import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.functional.Matcher;
import com.inari.commons.lang.indexed.IndexProvider;
import com.inari.commons.lang.indexed.IndexedTypeMap;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFContext;
import com.inari.firefly.component.AttributeMap;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.entity.event.EntityActivationEvent.Type;

public class EntitySystem implements IEntitySystem {
    
    private static final int DEFAULT_CAPACITY = 100;

    private final IEventDispatcher eventDispatcher;
    
    private final DynArray<Entity> activeEntities;
    private final DynArray<IndexedTypeSet> usedComponents;
    
    private final Stack<Entity> inactiveEntities;
    private final IndexedTypeMap<Stack<EntityComponent>> unusedComponents;

    
    public EntitySystem( FFContext context ) {
        this( context, DEFAULT_CAPACITY );
    }
    
    public EntitySystem( FFContext context,  int initialCapacity ) {
        if ( initialCapacity < 0 ) {
            initialCapacity = DEFAULT_CAPACITY;
        }
        
        eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
        
        activeEntities = new DynArray<Entity>( initialCapacity );
        usedComponents = new DynArray<IndexedTypeSet>( initialCapacity );
        for ( int i = 0; i < usedComponents.capacity(); i++ ) {
            usedComponents.set( i, new IndexedTypeSet( EntityComponent.class ) );
        }

        inactiveEntities = new Stack<Entity>();
        unusedComponents = new IndexedTypeMap<Stack<EntityComponent>>( 
            EntityComponent.class, 
            Stack.class 
        );
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
        
        return (ComponentBuilder<C>) new EntityBuilder( this );
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
        Stack<EntityComponent> components = (Stack<EntityComponent>) unusedComponents.getValue( componentType );
        if ( components == null ) {
            components = new Stack<EntityComponent>();
            unusedComponents.put( componentType, components );
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

//    @Override
//    public final <C extends EntityComponent> Iterator<C> getComponentIterator( Class<C> componentType ) {
//        return getComponentIterator( componentType, new EntityIdIterator( entityIterator() ) );
//    }
//    
//    @Override
//    public final <C extends EntityComponent> Iterator<C> getComponentIterator( Class<C> componentType, IntIterator entityIterator ) {
//        return new EntityComponentIterator<C>( componentType, entityIterator );
//    }
//
//    @Override
//    public final <C extends EntityComponent> Iterator<C> getComponentIterator( Class<C> componentType, Matcher<Entity> matcher ) {
//        return getComponentIterator( componentType, new EntityIdIterator( entityIterator( matcher ) ) );
//    }
    
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
                Stack<EntityComponent> stack = unusedComponents.getValue( i );
                stack.push( component );
            }
        }
    }
    
    private <C extends EntityComponent> EntityComponent newComponent( Class<C> componentType ) {
        int componentTypeIndex = IndexProvider.getIndexForType( componentType, EntityComponent.class );
        
        EntityComponent result = getUnused( componentTypeIndex );
        if ( result != null ) {
            return result;
        }
        
        return createComponent( componentType );
    }
    
    private <C extends EntityComponent> EntityComponent newComponent( Class<C> componentType, AttributeMap attributes ) {
        EntityComponent newComponent = newComponent( componentType );
        newComponent.fromAttributeMap( attributes );
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
        Stack<EntityComponent> unusedStack = unusedComponents.getValue( componentTypeIndex );
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
    
//    private final class EntityIdIterator implements IntIterator {
//        
//        private final Iterator<Entity> delegate;
//
//        public EntityIdIterator( Iterator<Entity> delegate ) {
//            super();
//            this.delegate = delegate;
//        }
//
//        @Override
//        public boolean hasNext() {
//            return delegate.hasNext();
//        }
//
//        @Override
//        public int next() {
//            return delegate.next().indexedId();
//        }
//    }
//    
//    private final class EntityComponentIterator<C extends EntityComponent> implements Iterator<C> {
//        
//        private final IntIterator entityIterator;
//        private final Class<C> componentType;
//        private C next = null;
//        
//        private EntityComponentIterator( Class<C> componentType, IntIterator entityIterator ) {
//            this.entityIterator = entityIterator;
//            this.componentType = componentType;
//            findNext();
//        }
//
//        @Override
//        public boolean hasNext() {
//            return next != null;
//        }
//
//        @Override
//        public C next() {
//            C result = next;
//            findNext();
//            return result;
//        }
//        
//        private void findNext() {
//            next = null;
//            while( entityIterator.hasNext() && next == null ) {
//                next = getComponent( entityIterator.next(), componentType );
//            }
//        }
//
//        @Override
//        public void remove() {
//            throw new UnsupportedOperationException();
//        }
//    }
    
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
    
    
    public final class EntityBuilder extends BaseComponentBuilder<Entity> {
        
        private IndexedTypeSet prefabComponents;

        private EntityBuilder( EntitySystem system ) {
            super( system );
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
            
            Set<Class<? extends Component>> componentTypes = attributes.getComponentTypes();
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
