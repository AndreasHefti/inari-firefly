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
package com.inari.firefly.control.task;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.Activation;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;
import com.inari.firefly.system.component.SystemComponentMap;

public final class TaskSystem extends ComponentSystem<TaskSystem> {
    
    public static final FFSystemTypeKey<TaskSystem> SYSTEM_KEY = FFSystemTypeKey.create( TaskSystem.class );
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        Task.TYPE_KEY
    );

    final SystemComponentMap<Task> tasks;
    
    TaskSystem() {
        super( SYSTEM_KEY );
        tasks = SystemComponentMap.create( 
            this, Task.TYPE_KEY,
            new Activation() {
                public final void activate( int id ) { runTask( id ); }
                public final void deactivate( int id ) {}
            },
            20, 10 
        );
    }

    @Override
    public final void init( FFContext context ) {
        super.init( context );
        
        context.registerListener( TaskSystemEvent.TYPE_KEY, this );
    }

    public final SystemComponentMap<Task> taskMap() {
        return tasks;
    }

    public final void runTask( int taskId ) {
        if ( !tasks.map.contains( taskId ) ) {
            return;
        }
        
        Task task = tasks.get( taskId );
        task.runTask();
        
        if ( task.removeAfterRun() ) {
            tasks.remove( taskId );
        }
    }
    
    public final void dispose( FFContext context ) {
        context.disposeListener( TaskSystemEvent.TYPE_KEY, this );
        clearSystem();
    }
    
    public final void clearSystem() {
        tasks.clear();
    }

    public final SystemComponentBuilder getTaskBuilder( Class<? extends Task> componentType ) {
        if ( componentType == null ) {
            throw new IllegalArgumentException( "componentType is needed for SystemComponentBuilder for component: " + Task.TYPE_KEY.name() );
        }
        
        return tasks.getBuilder( componentType );
    }

    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            tasks.getBuilderAdapter()
        );
    }

}
