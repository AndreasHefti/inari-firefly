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
package com.inari.firefly.state.event;

import com.inari.commons.event.Event;
import com.inari.firefly.state.StateChange;

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
    
    public final int stateChangeId;
    public final String stateChangeName;
    
    public final int sourceStateId;
    public final int targetStateId;

    public WorkflowEvent( 
        Type type, 
        int workflowId,
        String workflowName, 
        int stateChangeId, 
        String stateChangeName, 
        int sourceStateId, 
        int targetStateId 
    ) {
        super( TYPE_KEY );
        this.type = type;
        this.workflowId = workflowId;
        this.workflowName = workflowName;
        this.stateChangeId = stateChangeId;
        this.stateChangeName = stateChangeName;
        this.sourceStateId = sourceStateId;
        this.targetStateId = targetStateId;
    }

    @Override
    public final void notify( WorkflowEventListener listener ) {
        listener.onEvent( this );
    }
    
    public static final WorkflowEvent createDoStateChangeEvent( String stateChangeName ) {
        return new WorkflowEvent( Type.DO_STATE_CHANGE, -1, null, -1, stateChangeName, -1 , -1 );
    }

    public static final WorkflowEvent createWorkflowStartedEvent( int workflowId, int startStateId ) {
        return new WorkflowEvent( Type.WORKFLOW_STARTED, workflowId, null, -1, null, startStateId , startStateId );
    }

    public static final WorkflowEvent createStateChangedEvent( StateChange stateChange ) {
        return new WorkflowEvent( 
            Type.STATE_CHANGED, stateChange.getWorkflowId(), null, stateChange.getId(), 
            stateChange.getName(), stateChange.getFromStateId() , stateChange.getToStateId() 
        );
    }

    public static WorkflowEvent createWorkflowFinishedEvent( StateChange stateChange ) {
        return new WorkflowEvent( 
            Type.WORKFLOW_FINISHED, stateChange.getWorkflowId(), null, stateChange.getId(), 
            stateChange.getName(), stateChange.getFromStateId() , stateChange.getToStateId() 
        );
    }

}
