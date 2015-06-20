package com.inari.firefly.control;

import java.util.HashSet;
import java.util.Set;

import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.FFContext;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.entity.IEntitySystem;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.entity.event.EntityActivationEvent.Type;
import com.inari.firefly.entity.event.EntityActivationListener;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.event.UpdateEvent;
import com.inari.firefly.system.event.UpdateEventListener;

public final class EntityControllerSystem 
    implements 
        FFSystem,
        ComponentBuilderFactory, 
        EntityActivationListener, 
        UpdateEventListener {
    
    private IEntitySystem entitySystem;
    private FFContext context;
    
    private final Set<ComponentControllerType> allActiveControllerTypes;
    private final DynArray<EntityController> controller;
    private final DynArray<IntBag> entitiesPerController;

    EntityControllerSystem() {
        allActiveControllerTypes = new HashSet<ComponentControllerType>();
        controller = new DynArray<EntityController>();
        entitiesPerController = new DynArray<IntBag>();
    }
    
    @Override
    public void init( FFContext context ) {
        this.context = context;
        entitySystem = context.get( FFContext.System.ENTITY_SYSTEM );
    }

    @Override
    public final void dispose( FFContext context ) {
        allActiveControllerTypes.clear();
        controller.clear();
        controller.clear();
    }
    
    @Override
    public final void update( UpdateEvent event ) {
        long updateTime = event.getUpdate();
        for ( EntityController ctrl : controller ) {
            IntBag entities = entitiesPerController.get( ctrl.indexedId() );
            if ( entities.isEmpty() ) {
                continue;
            }
            IntIterator iterator = entities.iterator();
            while ( iterator.hasNext() ) {
                ctrl.update( updateTime, iterator.next() );
            }
        }
    }

    @Override
    public void onEntityActivationEvent( EntityActivationEvent event ) {
        if ( event.type == Type.ENTITY_ACTIVATED ) {
            IndexedTypeSet components = entitySystem.getComponents( event.entityId );
            for ( ComponentControllerType componentControllerType : allActiveControllerTypes ) {
                int controllerId = componentControllerType.getControllerId( components );
                if ( controllerId >= 0 ) {
                    IntBag entities = entitiesPerController.get( controllerId );
                    if ( entities == null ) {
                        entities = new IntBag();
                        entitiesPerController.set( controllerId, entities );
                    }
                    entities.add( event.entityId );
                }
            }
        } else {
            for ( IntBag entities : entitiesPerController ) {
                entities.remove( event.entityId );
            }
        }
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public final <C> ComponentBuilder<C> getComponentBuilder( Class<C> type ) {
        if ( type == EntityController.class ) {
            return (ComponentBuilder<C>) getEntityControllerBuilder();
        }
        
        throw new IllegalArgumentException( "Unsupported IComponent type for StateSystem. Type: " + type );
    }
    
    public final EntityControllerBuilder getEntityControllerBuilder() {
        return new EntityControllerBuilder( this );
    }
    
    private void addEntityController( EntityController entityController ) {
        if ( controller.contains( entityController.indexedId() ) ) {
            return;
        }
        controller.set( entityController.indexedId(), entityController );
        allActiveControllerTypes.add( entityController.getComponentControllerType() );
    }

    private final class EntityControllerBuilder extends BaseComponentBuilder<EntityController> {

        protected EntityControllerBuilder( ComponentBuilderFactory componentFactory ) {
            super( componentFactory );
        }

        @Override
        public EntityController build( int componentId ) {
            EntityController result = getInstance( context, componentId );
            result.fromAttributeMap( attributes );
            addEntityController( result );
            return result;
        }
    }
}
