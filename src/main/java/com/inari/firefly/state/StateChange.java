package com.inari.firefly.state;

import java.util.Arrays;
import java.util.Set;

import com.inari.firefly.component.NamedIndexedComponent;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;


public final class StateChange extends NamedIndexedComponent {
    
    public static final AttributeKey<Integer> WORKFLOW_ID = new AttributeKey<Integer>( "workflowId", Integer.class, StateChange.class );
    public static final AttributeKey<Integer> FORM_STATE_ID = new AttributeKey<Integer>( "fromStateId", Integer.class, StateChange.class );
    public static final AttributeKey<Integer> TO_STATE_ID = new AttributeKey<Integer>( "toStateId", Integer.class, StateChange.class );
    public static final AttributeKey<Integer> CONDITION_ID = new AttributeKey<Integer>( "conditionId", Integer.class, StateChange.class );
    public static final AttributeKey<Integer> TASK_ID = new AttributeKey<Integer>( "taskId", Integer.class, StateChange.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        WORKFLOW_ID,
        FORM_STATE_ID,
        TO_STATE_ID,
        CONDITION_ID,
        TASK_ID
    };
    
    private int fromStateId;
    private int toStateId;
    private int workflowId;
    private int conditionId = -1;
    private int taskId = -1;
    
    protected StateChange( int stateChangeId ) {
        super( stateChangeId );
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
    
    public final int getConditionId() {
        return conditionId;
    }
    
    public final void setConditionId( int conditionId ) {
        this.conditionId = conditionId;
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
        conditionId = attributes.getValue( CONDITION_ID, conditionId );
        taskId = attributes.getValue( TASK_ID, taskId );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( FORM_STATE_ID, fromStateId );
        attributes.put( TO_STATE_ID, toStateId );
        attributes.put( WORKFLOW_ID, workflowId );
        attributes.put( CONDITION_ID, conditionId );
        attributes.put( TASK_ID, taskId );
    }

}
