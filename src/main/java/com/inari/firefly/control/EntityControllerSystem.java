package com.inari.firefly.control;

import java.util.HashSet;
import java.util.Set;

import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.Disposable;
import com.inari.firefly.FFContext;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.entity.IEntitySystem;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.entity.event.EntityActivationEvent.Type;
import com.inari.firefly.entity.event.EntityActivationListener;
import com.inari.firefly.system.event.UpdateEvent;
import com.inari.firefly.system.event.UpdateEventListener;

public final class EntityControllerSystem implements ComponentBuilderFactory, EntityActivationListener, UpdateEventListener, Disposable {
    
    private final IEntitySystem entitySystem;
    
    private Set<ComponentControllerType> allActiveControllerTypes = new HashSet<ComponentControllerType>();
    private DynArray<EntityController> controller = new DynArray<EntityController>();
    private DynArray<IntBag> entitiesPerController = new DynArray<IntBag>();

    EntityControllerSystem( FFContext context ) {
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
    
    private void addEntityController( EntityController entityController ) {
        if ( controller.contains( entityController.indexedId() ) ) {
            return;
        }
        controller.set( entityController.indexedId(), entityController );
        allActiveControllerTypes.add( entityController.getComponentControllerType() );
    }

}
