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
package com.inari.firefly.task;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFContext;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.task.event.TaskEvent;
import com.inari.firefly.task.event.TaskEventListener;

public final class TaskSystem implements FFSystem, ComponentSystem, ComponentBuilderFactory, TaskEventListener {
    
    private FFContext context;
    private final DynArray<Task> tasks;
    
    TaskSystem() {
        tasks = new DynArray<Task>();
    }

    @Override
    public final void init( FFContext context ) {
        this.context = context;
        IEventDispatcher eventDispatcher = context.get( FFContext.EVENT_DISPATCHER );
        eventDispatcher.register( TaskEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        IEventDispatcher eventDispatcher = context.get( FFContext.EVENT_DISPATCHER );
        eventDispatcher.unregister( TaskEvent.class, this );
        tasks.clear();
    }

    @Override
    public final void onTaskEvent( TaskEvent taskEvent ) {
        switch ( taskEvent.type ) {
            case RUN_TASK: {
                Task task = tasks.get( taskEvent.taskId );
                if ( task != null ) {
                    task.run( context );
                    if ( task.removeAfterRun() ) {
                        tasks.remove( taskEvent.taskId );
                    }
                }
                break;
            }
            case REMOVE_TASK: {
                tasks.remove( taskEvent.taskId );
                break;
            }
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final <C> ComponentBuilder<C> getComponentBuilder( Class<C> type ) {
        if ( type == Task.class ) {
            return (ComponentBuilder<C>) getTaskBuilder();
        }
        
        throw new IllegalArgumentException( "Unsupported IComponent type for StateSystem. Type: " + type );
    }
    
    public final TaskBuilder getTaskBuilder() {
        return new TaskBuilder( this );
    }

    private static final Set<Class<?>> SUPPORTED_COMPONENT_TYPES = new HashSet<Class<?>>();
    @Override
    public final Set<Class<?>> supportedComponentTypes() {
        if ( SUPPORTED_COMPONENT_TYPES.isEmpty() ) {
            SUPPORTED_COMPONENT_TYPES.add( Task.class );
        }
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final void fromAttributes( Attributes attributes ) {
        fromAttributes( attributes, BuildType.CLEAR_OLD );
    }

    @Override
    public void fromAttributes( Attributes attributes, BuildType buildType ) {
        
    }

    @Override
    public void toAttributes( Attributes attributes ) {
        // TODO Auto-generated method stub
        
    }
    
    private final class TaskBuilder extends BaseComponentBuilder<Task> {

        protected TaskBuilder( ComponentBuilderFactory componentFactory ) {
            super( componentFactory );
        }
        
        @Override
        protected Task createInstance( Constructor<Task> constructor, Object... paramValues ) throws Exception {
            return constructor.newInstance( paramValues );
        }

        @Override
        public Task build( int componentId ) {
            Task result = getInstance( context, componentId );
            result.fromAttributes( attributes );
            return result;
        }
        
    }

    

    

}
