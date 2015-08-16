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

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.ComponentBuilderHelper;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;

public final class ControllerSystem 
    implements
        FFContextInitiable,
        ComponentSystem,
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
        clear();
    }
    
    public final void deleteController( int id ) {
        Controller removed = controller.remove( id );
        if ( removed != null ) {
            removed.dispose();
        }
    }

    public final void clear() {
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

    @Override
    public final void fromAttributes( Attributes attributes, BuildType buildType ) {
        if ( buildType == BuildType.CLEAR_OLD ) {
            clear();
        }
        
        new ComponentBuilderHelper<Controller>() {
            @Override
            public Controller get( int id ) {
                return controller.get( id );
            }
            @Override
            public void delete( int id ) {
                deleteController( id );
            }
        }.buildComponents( Controller.class, buildType, getControllerBuilder(), attributes );
    }

    @Override
    public final void toAttributes( Attributes attributes ) {
        ComponentBuilderHelper.toAttributes( attributes, Controller.class, controller );
    }
    

    private final class ControllerBuilder extends BaseComponentBuilder<Controller> {

        protected ControllerBuilder( ComponentBuilderFactory componentFactory ) {
            super( componentFactory );
        }

        @Override
        public Controller build( int componentId ) {
            Controller result = getInstance( context, componentId );
            result.fromAttributes( attributes );
            controller.set( result.index(), result );
            return result;
        }
    }

}
