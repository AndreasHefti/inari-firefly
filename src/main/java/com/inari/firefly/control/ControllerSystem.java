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
package com.inari.firefly.control;

import java.util.Iterator;

import com.inari.commons.StringUtils;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public final class ControllerSystem
    extends
        ComponentSystem<ControllerSystem>
    implements
        UpdateEventListener {
    
    public static final FFSystemTypeKey<ControllerSystem> SYSTEM_KEY = FFSystemTypeKey.create( ControllerSystem.class );
    
    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
       Controller.TYPE_KEY
    };

    private final DynArray<Controller> controller;

    ControllerSystem() {
        super( SYSTEM_KEY );
        controller = new DynArray<Controller>();
    }
    
    @Override
    public void init( FFContext context ) {
        super.init( context );
        
        context.registerListener( UpdateEvent.TYPE_KEY, this );
    }

    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( UpdateEvent.TYPE_KEY, this );
        
        clear();
    }
    
    public final Controller getController( int controllerId ) {
        if ( !controller.contains( controllerId ) ) {
            return null;
        }
        return controller.get( controllerId );
    }
    
    public final <T extends Controller> T getControllerAs( int controllerId, Class<T> controllerType ) {
        Controller c = getController( controllerId );
        if ( c == null ) {
            return null;
        }
        return controllerType.cast( c );
    }
    
    public final <T extends Controller> T getControllerAs( String controllerName, Class<T> controllerType ) {
        Controller c = getController( getControllerId( controllerName ) );
        if ( c == null ) {
            return null;
        }
        return controllerType.cast( c );
    }
    
    public final void deleteController( int controllerId ) {
        if ( controllerId < 0 ) {
            return;
        }
        
        if ( !controller.contains( controllerId ) ) {
            return;
        }
        
        Controller removed = controller.remove( controllerId );
        if ( removed != null ) {
            disposeSystemComponent( removed );
        }
    }
    
    public final void deleteController( String name ) {
        deleteController( getControllerId( name ) );
    }

    public final int getControllerId( String name ) {
        if ( StringUtils.isBlank( name ) ) {
            return -1;
        }
        
        for ( Controller c : controller ) {
            if ( name.equals( c.getName() ) ) {
                return c.index();
            }
        }
        
        return -1;
    }
    
    public final void addControlledComponentId( int controllerId, int componentId ) {
        if ( !controller.contains( controllerId ) ) {
            return;
        }
        controller.get( controllerId ).addComponentId( componentId );
    }
    
    public final void removeControlledComponentId( int controllerId, int componentId ) {
        if ( !controller.contains( controllerId ) ) {
            return;
        }
        controller.get( controllerId ).removeComponentId( componentId );
    }

    public final void clear() {
        for ( Controller c : controller ) {
            disposeSystemComponent( c );
        }
        controller.clear();
    }

    @Override
    public final void update( UpdateEvent event ) {
        for ( int i = 0; i < controller.capacity(); i++ ) {
            Controller c = controller.get( i );
            if ( c != null ) {
                c.processUpdate();
            }
        }
    }
    
    public final ControllerBuilder getControllerBuilder( Class<? extends Controller> componentType ) {
        return new ControllerBuilder( componentType );
    }

    @Override
    public final SystemComponentKey<?>[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
            new ControllerBuilderAdapter()
        };
    }

    private final class ControllerBuilder extends SystemComponentBuilder {
        
        private ControllerBuilder( Class<? extends Controller> componentType ) {
            super( context, componentType );
        }
        
        @Override
        public final SystemComponentKey<Controller> systemComponentKey() {
            return Controller.TYPE_KEY;
        }

        @Override
        public final int doBuild( int componentId, Class<?> controllerType, boolean activate ) {
            Controller result = createSystemComponent( componentId, controllerType, context );
            controller.set( result.index(), result );
            
            if ( activate ) {
                result.setActive( true );
            }
            
            return result.index();
        }
    }

    private final class ControllerBuilderAdapter extends SystemBuilderAdapter<Controller> {
        private ControllerBuilderAdapter() {
            super( ControllerSystem.this, Controller.TYPE_KEY );
        }
        @Override
        public final Controller get( int id ) {
            return controller.get( id );
        }
        @Override
        public final Iterator<Controller> getAll() {
            return controller.iterator();
        }
        @Override
        public final void delete( int id ) {
            deleteController( id );
        }
        @Override
        public final int getId( String name ) {
            return getControllerId( name );
        }
        @Override
        public final void activate( int id ) {
            get( id ).setActive( true );
        }
        @Override
        public final void deactivate( int id ) {
            get( id ).setActive( false );
        }
        @Override
        public final SystemComponentBuilder createComponentBuilder( Class<? extends Controller> componentType ) {
            if ( componentType == null ) {
                throw new IllegalArgumentException( "componentType is needed for SystemComponentBuilder for component: " + componentTypeKey().name() );
            }
            return new ControllerBuilder( componentType );
        }
    }

}
