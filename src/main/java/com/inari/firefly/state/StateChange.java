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

import java.util.Arrays;
import java.util.Set;

import com.inari.commons.StringUtils;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.component.SystemComponent;


public final class StateChange extends SystemComponent {
    
    public static final SystemComponentKey TYPE_KEY = SystemComponentKey.create( StateChange.class );
    
    public static final AttributeKey<Integer> WORKFLOW_ID = new AttributeKey<Integer>( "workflowId", Integer.class, StateChange.class );
    public static final AttributeKey<Integer> FORM_STATE_ID = new AttributeKey<Integer>( "fromStateId", Integer.class, StateChange.class );
    public static final AttributeKey<Integer> TO_STATE_ID = new AttributeKey<Integer>( "toStateId", Integer.class, StateChange.class );
    public static final AttributeKey<String> CONDITION_TYPE_NAME = new AttributeKey<String>( "conditionTypeName", String.class, StateChange.class );
    public static final AttributeKey<Integer> TASK_ID = new AttributeKey<Integer>( "taskId", Integer.class, StateChange.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        WORKFLOW_ID,
        FORM_STATE_ID,
        TO_STATE_ID,
        CONDITION_TYPE_NAME,
        TASK_ID
    };
    
    private int fromStateId;
    private int toStateId;
    private int workflowId;
    private StateChangeCondition condition;
    private int taskId;
    
    protected StateChange( int stateChangeId ) {
        super( stateChangeId );
        fromStateId = -1;
        toStateId = -1;
        workflowId = -1;
        condition = null;
        taskId = -1;
    }
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }
    
    public final int getFromStateId() {
        return fromStateId;
    }
    
    public final void setFromStateId( int fromStateId ) {
        this.fromStateId = fromStateId;
    }
    
    public final int getToStateId() {
        return toStateId;
    }
    
    public final void setToStateId( int toStateId ) {
        this.toStateId = toStateId;
    }
    
    public final int getWorkflowId() {
        return workflowId;
    }
    
    public final void setWorkflowId( int workflowId ) {
        this.workflowId = workflowId;
    }

    public final StateChangeCondition getCondition() {
        return condition;
    }

    public final void setCondition( StateChangeCondition condition ) {
        this.condition = condition;
    }

    public final int getTaskId() {
        return taskId;
    }

    public final void setTaskId( int taskId ) {
        this.taskId = taskId;
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
        
        fromStateId = attributes.getValue( FORM_STATE_ID, fromStateId );
        toStateId = attributes.getValue( TO_STATE_ID, toStateId );
        workflowId = attributes.getValue( WORKFLOW_ID, workflowId );
        
        String conditionTypeName = attributes.getValue( CONDITION_TYPE_NAME );
        if ( !StringUtils.isBlank( conditionTypeName ) ) {
            try {
                condition = (StateChangeCondition) Class.forName( conditionTypeName ).newInstance();
            } catch ( Exception  e ) {
                throw new FFInitException( "Failed to get Condition form type: " + conditionTypeName, e );
            }
        }
        
        taskId = attributes.getValue( TASK_ID, taskId );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( FORM_STATE_ID, fromStateId );
        attributes.put( TO_STATE_ID, toStateId );
        attributes.put( WORKFLOW_ID, workflowId );
        if ( condition != null ) {
            attributes.put( CONDITION_TYPE_NAME, condition.getClass().getName() );
        }
        attributes.put( TASK_ID, taskId );
    }

}
