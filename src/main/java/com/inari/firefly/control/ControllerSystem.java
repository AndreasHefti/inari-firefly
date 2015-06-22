package com.inari.firefly.control;

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFContext;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.event.UpdateEvent;
import com.inari.firefly.system.event.UpdateEventListener;

public final class ControllerSystem 
    implements 
        FFSystem,
        ComponentBuilderFactory,
        UpdateEventListener {
    
    private FFContext context;
    private final DynArray<Controller> controller;

    ControllerSystem() {
        controller = new DynArray<Controller>();
    }
    
    @Override
    public void init( FFContext context ) {
        this.context = context;
    }

    @Override
    public final void dispose( FFContext context ) {
        for ( Controller c : controller ) {
            c.dispose( context );
        }
        controller.clear();
    }
    
    @Override
    public final void update( UpdateEvent event ) {
        long updateTime = event.getUpdate();
        for ( int i = 0; i < controller.capacity(); i++ ) {
            Controller c = controller.get( i );
            if ( c != null ) {
                c.update( updateTime );
            }
        }
    }

//    @Override
//    public void onEntityActivationEvent( EntityActivationEvent event ) {
//        if ( event.type == Type.ENTITY_ACTIVATED ) {
//            IndexedTypeSet components = entitySystem.getComponents( event.entityId );
//            for ( EntityControllerAdapter entityControllerAdapter : allActiveEntityController ) {
//                int controllerId = entityControllerAdapter.getControllerId( components );
//                if ( controllerId >= 0 ) {
//                    Controller c = controller.get( controllerId );
//                    c.addComponentId( event.entityId );
//                }
//            }
//        }
//    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public final <C> ComponentBuilder<C> getComponentBuilder( Class<C> type ) {
        if ( Controller.class.isAssignableFrom( type ) ) {
            return (ComponentBuilder<C>) getControllerBuilder();
        }
        
        throw new IllegalArgumentException( "Unsupported Component type for ControllerSystem Builder. Type: " + type );
    }
    
    public final ControllerBuilder getControllerBuilder() {
        return new ControllerBuilder( this );
    }

    private final class ControllerBuilder extends BaseComponentBuilder<Controller> {

        protected ControllerBuilder( ComponentBuilderFactory componentFactory ) {
            super( componentFactory );
        }

        @Override
        public Controller build( int componentId ) {
            Controller result = getInstance( context, componentId );
            result.fromAttributeMap( attributes );
            controller.set( result.indexedId(), result );
            return result;
        }
    }
}
