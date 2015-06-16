package com.inari.firefly.state;

import java.util.Arrays;
import java.util.Set;

import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.AttributeMap;
import com.inari.firefly.component.NamedIndexedComponent;


public final class StateChange extends NamedIndexedComponent {
    
    public static final AttributeKey<Integer> WORKFLOW_ID = new AttributeKey<Integer>( "workflowId", Integer.class, StateChange.class );
    public static final AttributeKey<Integer> FORM_STATE_ID = new AttributeKey<Integer>( "fromStateId", Integer.class, StateChange.class );
    public static final AttributeKey<Integer> TO_STATE_ID = new AttributeKey<Integer>( "toStateId", Integer.class, StateChange.class );
    public static final AttributeKey<Integer> CONDITION_ID = new AttributeKey<Integer>( "conditionId", Integer.class, StateChange.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        WORKFLOW_ID,
        FORM_STATE_ID,
        TO_STATE_ID,
        CONDITION_ID
    };
    
    private int fromStateId;
    private int toStateId;
    private int workflowId;
    private int conditionId = -1;
    
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

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( Arrays.asList( ATTRIBUTE_KEYS ) );
        return attributeKeys;
    }

    @Override
    public final void fromAttributeMap( AttributeMap attributes ) {
        super.fromAttributeMap( attributes );
        
        fromStateId = attributes.getValue( FORM_STATE_ID, fromStateId );
        toStateId = attributes.getValue( TO_STATE_ID, toStateId );
        workflowId = attributes.getValue( WORKFLOW_ID, workflowId );
        conditionId = attributes.getValue( CONDITION_ID, conditionId );
    }

    @Override
    public final void toAttributeMap( AttributeMap attributes ) {
        super.toAttributeMap( attributes );
        
        attributes.put( FORM_STATE_ID, fromStateId );
        attributes.put( TO_STATE_ID, toStateId );
        attributes.put( WORKFLOW_ID, workflowId );
        attributes.put( CONDITION_ID, conditionId );
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "StateChange [name=" );
        builder.append( name );
        builder.append( ", fromStateId=" );
        builder.append( fromStateId );
        builder.append( ", toStateId=" );
        builder.append( toStateId );
        builder.append( ", workflowId=" );
        builder.append( workflowId );
        builder.append( ", conditionId=" );
        builder.append( conditionId );
        builder.append( ", indexedId()=" );
        builder.append( indexedId() );
        builder.append( "]" );
        return builder.toString();
    }

}
