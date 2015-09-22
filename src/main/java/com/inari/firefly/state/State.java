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
package com.inari.firefly.state;

import java.util.Arrays;
import java.util.Set;

import com.inari.firefly.component.NamedIndexedComponent;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public final class State extends NamedIndexedComponent {
    
    public static final AttributeKey<Integer> WORKFLOW_ID = new AttributeKey<Integer>( "workflowId", Integer.class, State.class );
    public static final AttributeKey<Integer> INIT_TASK_ID = new AttributeKey<Integer>( "initTaskId", Integer.class, State.class );
    public static final AttributeKey<Integer> DISPOSE_TASK_ID = new AttributeKey<Integer>( "disposeTaskId", Integer.class, State.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        WORKFLOW_ID,
        INIT_TASK_ID,
        DISPOSE_TASK_ID
    };
    
    private int workflowId;
    private int initTaskId;
    private int disposeTaskId;

    State( int stateId ) {
        super( stateId );
        initTaskId = -1;
        disposeTaskId = -1;
    }

    public final int getWorkflowId() {
        return workflowId;
    }

    public final void setWorkflowId( int workflowId ) {
        this.workflowId = workflowId;
    }

    public final int getInitTaskId() {
        return initTaskId;
    }

    public final void setInitTaskId( int initTaskId ) {
        this.initTaskId = initTaskId;
    }

    public final int getDisposeTaskId() {
        return disposeTaskId;
    }

    public final void setDisposeTaskId( int disposeTaskId ) {
        this.disposeTaskId = disposeTaskId;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( Arrays.asList( ATTRIBUTE_KEYS ) );
        return attributeKeys;
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        workflowId = attributes.getValue( WORKFLOW_ID, workflowId );
        initTaskId = attributes.getValue( INIT_TASK_ID, initTaskId );
        disposeTaskId = attributes.getValue( DISPOSE_TASK_ID, disposeTaskId );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );

        attributes.put( WORKFLOW_ID, workflowId );
        attributes.put( INIT_TASK_ID, initTaskId );
        attributes.put( DISPOSE_TASK_ID, disposeTaskId );
    }
    
}
