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
package com.inari.firefly.state.event;

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
    
    public final int stateChangeId;
    public final String stateChangeName;
    
    public WorkflowEvent( int workflowId, int stateChangeId, Type type ) {
        super( TYPE_KEY );
        this.workflowId = workflowId;
        this.workflowName = null;
        this.stateChangeId = stateChangeId;
        this.stateChangeName = null;
        this.type = type;
    }
    
    public WorkflowEvent( String workflowName, String stateChangeName, Type type ) {
        super( TYPE_KEY );
        this.workflowId = -1;
        this.workflowName = workflowName;
        this.stateChangeId = -1;
        this.stateChangeName = stateChangeName;
        this.type = type;
    }

    @Override
    public final void notify( WorkflowEventListener listener ) {
        listener.onEvent( this );
    }

}
