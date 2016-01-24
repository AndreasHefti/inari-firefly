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
package com.inari.firefly.task;

import java.util.Iterator;

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.Component;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public final class TaskSystem extends ComponentSystem<TaskSystem> {
    
    public static final FFSystemTypeKey<TaskSystem> SYSTEM_KEY = FFSystemTypeKey.create( TaskSystem.class );
    
    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        Task.TYPE_KEY,
    };

    private final DynArray<Task> tasks;
    
    TaskSystem() {
        super( SYSTEM_KEY );
        tasks = new DynArray<Task>();
    }

    @Override
    public final void init( FFContext context ) {
        super.init( context );
        
        context.registerListener( TaskSystemEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( TaskSystemEvent.class, this );
        clear();
    }
    
    public final Task getTask( int taskId ) {
        if ( !tasks.contains( taskId ) ) {
            return null;
        }
        
        return tasks.get( taskId );
    }
    
    public <T extends Task> T getTaskAs( int taskId, Class<T> subType ) {
        Task task = getTask( taskId );
        if ( task == null ) {
            return null;
        }
        
        return subType.cast( task );
    }

    public final void clear() {
        for ( Task task : tasks ) {
            disposeSystemComponent( task );
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
        Task task = tasks.remove( taskId );

        if ( task != null ) {
            DynArray<TaskTrigger> triggers = task.getTriggers();
            if ( triggers != null ) {
                for ( TaskTrigger trigger : triggers ) {
                    trigger.dispose( context );
                }
            }
            
            disposeSystemComponent( task );
        }
    }


    final void onTaskEvent( TaskSystemEvent taskEvent ) {
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
    public final SystemComponentKey<?>[] supportedComponentTypes() {
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
        public final SystemComponentKey<Task> systemComponentKey() {
            return Task.TYPE_KEY;
        }
        
        @Override
        public final int doBuild( int componentId, Class<?> taskType, boolean activate ) {
            attributes.put( Component.INSTANCE_TYPE_NAME, taskType.getName() );
            
            Task task = getInstance( context, componentId );
            task.fromAttributes( attributes );
            
            tasks.set( task.getId(), task );
            postInit( task, context );
            
            DynArray<TaskTrigger> triggers = task.getTriggers();
            if ( triggers != null ) {
                for ( TaskTrigger trigger : triggers ) {
                    trigger.connect( context, task );
                }
            }
            
            return task.getId();
        }
    }
    
    private final class TaskBuilderAdapter extends SystemBuilderAdapter<Task> {
        public TaskBuilderAdapter( TaskSystem system ) {
            super( system, new TaskBuilder() );
        }
        @Override
        public final SystemComponentKey<Task> componentTypeKey() {
            return Task.TYPE_KEY;
        }
        @Override
        public final Task getComponent( int id ) {
            return tasks.get( id );
        }
        @Override
        public final Iterator<Task> getAll() {
            return tasks.iterator();
        }
        @Override
        public final void deleteComponent( int id ) {
            deleteTask( id );
        }
        @Override
        public final void deleteComponent( String name ) {
            deleteTask( getTaskId( name ) );
        }
        @Override
        public final Task getComponent( String name ) {
            return getTask( getTaskId( name ) );
        }
    }

}
