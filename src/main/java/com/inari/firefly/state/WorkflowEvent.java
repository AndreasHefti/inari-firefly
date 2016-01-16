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
package com.inari.firefly.state;

import com.inari.commons.event.Event;

public final class WorkflowEvent extends Event<WorkflowEventListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( WorkflowEvent.class );
    
    public enum Type {
        DO_STATE_CHANGE,
        WORKFLOW_STARTED,
        STATE_CHANGED,
        WORKFLOW_FINISHED
    }
    
    public final Type type;
    
    public final int workflowId;
    public final String workflowName;
    public final String stateChangeName;
    
    public final String sourceStateName;
    public final String targetStateName;

    public WorkflowEvent( 
        Type type, 
        int workflowId,
        String workflowName, 
        String stateChangeName, 
        String sourceStateName, 
        String targetStateName 
    ) {
        super( TYPE_KEY );
        this.type = type;
        this.workflowId = workflowId;
        this.workflowName = workflowName;
        this.stateChangeName = stateChangeName;
        this.sourceStateName = sourceStateName;
        this.targetStateName = targetStateName;
    }

    @Override
    public final void notify( WorkflowEventListener listener ) {
        listener.onEvent( this );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "WorkflowEvent [type=" );
        builder.append( type );
        builder.append( ", workflowId=" );
        builder.append( workflowId );
        builder.append( ", workflowName=" );
        builder.append( workflowName );
        builder.append( ", stateChangeName=" );
        builder.append( stateChangeName );
        builder.append( ", sourceStateName=" );
        builder.append( sourceStateName );
        builder.append( ", targetStateName=" );
        builder.append( targetStateName );
        builder.append( "]" );
        return builder.toString();
    }

    public static final WorkflowEvent createDoStateChangeEvent( String workflowName, String stateChangeName ) {
        return new WorkflowEvent( Type.DO_STATE_CHANGE, -1, workflowName, stateChangeName, null, null );
    }
    
    public static final WorkflowEvent createDoStateChangeEventTo( String workflowName, String targetStateName ) {
        return new WorkflowEvent( Type.DO_STATE_CHANGE, -1, workflowName, null, null, targetStateName );
    }

    public static final WorkflowEvent createWorkflowStartedEvent( int workflowId, String workflowName, String initTaskName ) {
        return new WorkflowEvent( Type.WORKFLOW_STARTED, workflowId, workflowName, null, null , initTaskName );
    }

    public static final WorkflowEvent createStateChangedEvent( int workflowId, String workflowName, StateChange stateChange ) {
        return new WorkflowEvent( 
            Type.STATE_CHANGED, workflowId, workflowName, 
            stateChange.getName(), stateChange.getFromStateName() , stateChange.getToStateName() 
        );
    }

    public static final WorkflowEvent createWorkflowFinishedEvent( int workflowId, String workflowName, StateChange stateChange ) {
        return new WorkflowEvent( 
            Type.WORKFLOW_FINISHED, workflowId, workflowName, 
            stateChange.getName(), stateChange.getFromStateName() , stateChange.getToStateName() 
        );
    }

    

}
