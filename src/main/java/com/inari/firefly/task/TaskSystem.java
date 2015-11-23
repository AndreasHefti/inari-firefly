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

import java.util.Iterator;

import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.Disposable;
import com.inari.firefly.component.Component;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;
import com.inari.firefly.task.event.TaskEvent;
import com.inari.firefly.task.event.TaskEventListener;

public final class TaskSystem extends ComponentSystem implements TaskEventListener {
    
    private static final SystemComponentKey[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        Task.TYPE_KEY,
    };
    
    public static final TypedKey<TaskSystem> CONTEXT_KEY = TypedKey.create( "FF_TASK_SYSTEM", TaskSystem.class );
    
    private final DynArray<Task> tasks;
    
    TaskSystem() {
        tasks = new DynArray<Task>();
    }

    @Override
    public final void init( FFContext context ) {
        super.init( context );
        
        context.registerListener( TaskEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( TaskEvent.class, this );
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

    
    public final TaskBuilder getTaskBuilder() {
        return new TaskBuilder();
    }

    @Override
    public final SystemComponentKey[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
            new TaskBuilderAdapter( this )
        };
    }

    
    public final class TaskBuilder extends SystemComponentBuilder {

        @Override
        public final SystemComponentKey systemComponentKey() {
            return Task.TYPE_KEY;
        }
        
        @Override
        public final int doBuild( int componentId, Class<?> taskType ) {
            attributes.put( Component.INSTANCE_TYPE_NAME, taskType.getName() );
            
            Task result = getInstance( context, componentId );
            result.fromAttributes( attributes );
            
            tasks.set( result.getId(), result );
            postInit( result, context );
            return result.getId();
        }
    }
    
    private final class TaskBuilderAdapter extends SystemBuilderAdapter<Task> {
        public TaskBuilderAdapter( ComponentSystem system ) {
            super( system, new TaskBuilder() );
        }
        @Override
        public final SystemComponentKey componentTypeKey() {
            return Task.TYPE_KEY;
        }
        @Override
        public final Task get( int id, Class<? extends Task> subtype ) {
            return tasks.get( id );
        }
        @Override
        public final Iterator<Task> getAll() {
            return tasks.iterator();
        }
        @Override
        public final void delete( int id, Class<? extends Task> subtype ) {
            deleteTask( id );
        }
    }
    
}
