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
import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.Disposable;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.task.event.TaskEvent;
import com.inari.firefly.task.event.TaskEventListener;

public final class TaskSystem implements FFContextInitiable, ComponentSystem, ComponentBuilderFactory, TaskEventListener {
    
    public static final TypedKey<TaskSystem> CONTEXT_KEY = TypedKey.create( "FF_TASK_SYSTEM", TaskSystem.class );
    
    private FFContext context;
    private IEventDispatcher eventDispatcher;
    
    private final DynArray<Task> tasks;
    
    TaskSystem() {
        tasks = new DynArray<Task>();
    }

    @Override
    public final void init( FFContext context ) {
        this.context = context;
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        eventDispatcher.register( TaskEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        eventDispatcher.unregister( TaskEvent.class, this );
        clear();
    }

    public final void clear() {
        for ( Task task : tasks ) {
            deleteTask( task );
        }
        
        tasks.clear();
    }
    
    public final int getTaskId( String taskName ) {
        for ( int i = 0; i < tasks.capacity(); i++ ) {
            if ( !tasks.contains( i ) ) {
                continue;
            }
            Task task = tasks.get( i );
            if ( task.getName().equals( taskName ) ) {
                return i;
            }
        }
        
        return -1;
    }
    
    public final void deleteTask( int taskId ) {
        Task remove = tasks.remove( taskId );
        if ( remove != null ) {
            deleteTask( remove );
        }
    }

    private void deleteTask( Task task ) {
        if ( task instanceof Disposable ) {
            ( (Disposable) task ).dispose( context );
        }
        task.dispose();
    }

    @Override
    public final void onTaskEvent( TaskEvent taskEvent ) {
        switch ( taskEvent.eventType ) {
            case RUN_TASK: {
                Task task;
                if ( taskEvent.taskName != null ) {
                    task = tasks.get( getTaskId( taskEvent.taskName ) );
                } else {
                    task = tasks.get( taskEvent.taskId );
                }
                if ( task != null ) {
                    task.run( context );
                    if ( task.removeAfterRun() && tasks.contains( taskEvent.taskId ) ) {
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
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public final <C> ComponentBuilder<C> getComponentBuilder( Class<C> type ) {
        if ( !Task.class.isAssignableFrom( type ) ) {
            throw new IllegalArgumentException( "The IComponentType is not a subtype of Task." + type );
        }
        
        return new TaskBuilder( this, type );
    }
    
    public final <T extends Task> TaskBuilder<T> getTaskBuilder( Class<T> taskType ) {
        return new TaskBuilder<T>( this, taskType );
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
    
    public final class TaskBuilder<T extends Task> extends BaseComponentBuilder<T> {
        
        private final Class<T> taskType;

        protected TaskBuilder( ComponentBuilderFactory componentFactory, Class<T> taskType ) {
            super( componentFactory );
            this.taskType = taskType;
        }
        
        @Override
        protected T createInstance( Constructor<T> constructor, Object... paramValues ) throws Exception {
            return constructor.newInstance( paramValues );
        }

        @Override
        public T build( int componentId ) {
            attributes.put( Component.INSTANCE_TYPE_NAME, taskType.getName() );
            
            T result = getInstance( context, componentId );
            result.fromAttributes( attributes );
            
            tasks.set( result.getId(), result );
            postInit( result, context );
            
            return result;
        }
        
    }
    
}
