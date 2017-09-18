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

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;
import com.inari.firefly.system.component.Activation;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;
import com.inari.firefly.system.component.SystemComponentMap;
import com.inari.firefly.system.external.FFTimer;

public final class ControllerSystem
    extends
        ComponentSystem<ControllerSystem>
    implements
        UpdateEventListener {
    
    public static final FFSystemTypeKey<ControllerSystem> SYSTEM_KEY = FFSystemTypeKey.create( ControllerSystem.class );
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
       Controller.TYPE_KEY
    );

    private final SystemComponentMap<Controller> controller;

    ControllerSystem() {
        super( SYSTEM_KEY );
        controller = SystemComponentMap.create( 
            this, Controller.TYPE_KEY,
            new Activation() {
                public final void activate( int id ) { ControllerSystem.this.activate( id ); }
                public final void deactivate( int id ) { ControllerSystem.this.deactivate( id ); }
            },
            20, 10 
        );
    }
    
    @Override
    public void init( FFContext context ) {
        super.init( context );
        
        context.registerListener( UpdateEvent.TYPE_KEY, this );
    }
    
    public final void activate( int controllerId ) {
        if ( !controller.map.contains( controllerId ) ) {
            return;
        }
        
        controller.map.get( controllerId ).setActive( true );
    }
    
    public final void deactivate( int controllerId ) {
        if ( !controller.map.contains( controllerId ) ) {
            return;
        }
        
        controller.map.get( controllerId ).setActive( false );
    }

    public final void addControlledComponentId( int controllerId, int componentId ) {
        if ( !controller.map.contains( controllerId ) ) {
            return;
        }
        controller.map.get( controllerId ).addComponentId( componentId );
    }
    
    public final void removeControlledComponentId( int controllerId, int componentId ) {
        if ( !controller.map.contains( controllerId ) ) {
            return;
        }
        controller.map.get( controllerId ).removeComponentId( componentId );
    }

    public final void update( final FFTimer timer ) {
        for ( int i = 0; i < controller.map.capacity(); i++ ) {
            final Controller c = controller.map.get( i );
            if ( c != null ) {
                c.processUpdate();
            }
        }
    }
    
    public final void dispose( FFContext context ) {
        context.disposeListener( UpdateEvent.TYPE_KEY, this );
        
        clearSystem();
    }
    
    public final void clearSystem() {
        controller.clear();
    }
    
    public final SystemComponentBuilder getControllerBuilder( Class<? extends Controller> componentType ) {
        return controller.getBuilder( componentType );
    }

    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            controller.getBuilderAdapter()
        );
    }
   
}
