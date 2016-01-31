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

import com.inari.commons.event.Event;

public final class TaskSystemEvent extends Event<TaskSystem> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( TaskSystemEvent.class );
    
    public enum Type {
        RUN_TASK,
        REMOVE_TASK
    }
    
    public final Type eventType;
    public final int taskId;
    public final String taskName;

    public TaskSystemEvent( Type eventType, int taskId ) {
        super( TYPE_KEY );
        this.eventType = eventType;
        this.taskId = taskId;
        taskName = null; 
    }
    
    public TaskSystemEvent( Type eventType, String taskName ) {
        super( TYPE_KEY );
        this.eventType = eventType;
        this.taskName = taskName;
        taskId = -1; 
    }

    @Override
    public final void notify( TaskSystem listener ) {
        switch ( eventType ) {
            case RUN_TASK: {
               if ( taskId >= 0 ) {
                   listener.runTask( taskId );
               } else {
                   listener.runTask( listener.getTaskId( taskName ) );
               }
               break;
            }
            case REMOVE_TASK: {
                if ( taskId >= 0 ) {
                    listener.deleteTask( taskId );
                } else {
                    listener.deleteTask( listener.getTaskId( taskName ) );
                }
                break;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "TaskEvent [eventType=" );
        builder.append( eventType );
        builder.append( ", taskId=" );
        builder.append( taskId );
        builder.append( "]" );
        return builder.toString();
    }

}
