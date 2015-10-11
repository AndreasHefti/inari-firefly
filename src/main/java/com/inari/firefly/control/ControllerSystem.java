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
package com.inari.firefly.control;

import java.util.HashSet;
import java.util.Set;

import com.inari.commons.StringUtils;
import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.ComponentBuilderHelper;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.state.event.StateChangeEvent;
import com.inari.firefly.state.event.StateChangeListener;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;

public final class ControllerSystem 
    implements
        FFContextInitiable,
        ComponentSystem,
        ComponentBuilderFactory,
        UpdateEventListener {
    
    public static final TypedKey<ControllerSystem> CONTEXT_KEY = TypedKey.create( "FF_COMPONENT_CONTROLLER_SYSTEM", ControllerSystem.class );
    
    private FFContext context;
    private IEventDispatcher eventDispatcher;
    
    private final DynArray<Controller> controller;

    ControllerSystem() {
        controller = new DynArray<Controller>();
    }
    
    @Override
    public void init( FFContext context ) {
        this.context = context;
        
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        eventDispatcher.register( UpdateEvent.class, this );
    }

    @Override
    public final void dispose( FFContext context ) {
        eventDispatcher.unregister( UpdateEvent.class, this );
        
        clear();
    }
    
    public final void deleteController( int id ) {
        if ( id < 0 ) {
            return;
        }
        
        Controller removed = controller.remove( id );
        if ( removed != null ) {
            disposeController( removed );
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
                return c.getId();
            }
        }
        
        return -1;
    }

    public final void clear() {
        for ( Controller c : controller ) {
            disposeController( c );
        }
        controller.clear();
    }

    @Override
    public final void update( UpdateEvent event ) {
        for ( int i = 0; i < controller.capacity(); i++ ) {
            Controller c = controller.get( i );
            if ( c != null ) {
                c.update( event.timer );
            }
        }
    }
    
    @Override
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public final <C> ComponentBuilder<C> getComponentBuilder( Class<C> controllerType ) {
        if ( Controller.class.isAssignableFrom( controllerType ) ) {
            return new ControllerBuilder( this, controllerType );
        }
        
        throw new IllegalArgumentException( "Unsupported Component type for ControllerSystem Builder. Type: " + controllerType );
    }
    
    public final <C extends Controller> ControllerBuilder<C> getControllerBuilder( Class<C> controllerType ) {
        return new ControllerBuilder<C>( this, controllerType );
    }
    
    private static final Set<Class<?>> SUPPORTED_COMPONENT_TYPES = new HashSet<Class<?>>();
    @Override
    public final Set<Class<?>> supportedComponentTypes() {
        if ( SUPPORTED_COMPONENT_TYPES.isEmpty() ) {
            SUPPORTED_COMPONENT_TYPES.add( Controller.class );
        }
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final void fromAttributes( Attributes attributes ) {
        fromAttributes( attributes, BuildType.CLEAR_OLD );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public final void fromAttributes( Attributes attributes, BuildType buildType ) {
        if ( buildType == BuildType.CLEAR_OLD ) {
            clear();
        }
        for ( Class<? extends Controller> controllerSubType : attributes.getAllSubTypes( Controller.class ) ) {
            new ComponentBuilderHelper<Controller>() {
                @Override
                public Controller get( int id ) {
                    return controller.get( id );
                }
                @Override
                public void delete( int id ) {
                    deleteController( id );
                }
            }.buildComponents( Controller.class, buildType, (ControllerBuilder<Controller>) getControllerBuilder( controllerSubType ), attributes );
        }
    }

    @Override
    public final void toAttributes( Attributes attributes ) {
        ComponentBuilderHelper.toAttributes( attributes, Controller.class, controller );
    }
    
    private final void disposeController( Controller c ) {
        if ( c instanceof StateChangeListener ) {
            eventDispatcher.unregister( StateChangeEvent.class, (StateChangeListener) c );
        }
        c.dispose( context );
        c.dispose();
    }
    

    public final class ControllerBuilder<C extends Controller> extends BaseComponentBuilder<C> {
        
        private final Class<C> controllerType;

        protected ControllerBuilder( ComponentBuilderFactory componentFactory, Class<C> controllerType ) {
            super( componentFactory );
            this.controllerType = controllerType;
        }

        @Override
        public C build( int componentId ) {
            attributes.put( Component.INSTANCE_TYPE_NAME, controllerType.getName() );
            C result = getInstance( context, componentId );
            result.fromAttributes( attributes );
            controller.set( result.index(), result );
            
            postInit( result, context );
            
            return result;
        }
    }

}
