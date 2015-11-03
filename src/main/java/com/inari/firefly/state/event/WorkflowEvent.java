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
    
    public enum Type {
        STATE_CHANGE
    }
    
    public final Type type;
    
    public final int workflowId;
    public final String workflowName;
    
    public final int stateChangeId;
    public final String stateChangeName;
    
    public WorkflowEvent( int workflowId, int stateChangeId, Type type ) {
        this.workflowId = workflowId;
        this.workflowName = null;
        this.stateChangeId = stateChangeId;
        this.stateChangeName = null;
        this.type = type;
    }
    
    public WorkflowEvent( String workflowName, String stateChangeName, Type type ) {
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
