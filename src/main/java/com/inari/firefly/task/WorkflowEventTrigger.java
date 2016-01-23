package com.inari.firefly.task;

import java.util.Arrays;
import java.util.Set;

import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.state.WorkflowEvent;
import com.inari.firefly.state.WorkflowEventListener;
import com.inari.firefly.system.FFContext;

public final class WorkflowEventTrigger extends TaskTrigger implements WorkflowEventListener {
    
    public enum Type {
        STATE_CHANGE,
        ENTER_STATE,
        EXIT_STATE
    }
    
    public static final AttributeKey<String> WORKFLOW_NAME = new AttributeKey<String>( "workflowName", String.class, WorkflowEventTrigger.class );
    public static final AttributeKey<Type> TRIGGER_TYPE = new AttributeKey<Type>( "type", Type.class, WorkflowEventTrigger.class );
    public static final AttributeKey<String> TRIGGER_NAME = new AttributeKey<String>( "stateChangeName", String.class, WorkflowEventTrigger.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        WORKFLOW_NAME,
        TRIGGER_TYPE,
        TRIGGER_NAME
    };

    private String workflowName;
    private Type triggerType;
    private String triggerName;
    
    private final FFContext context;

    protected WorkflowEventTrigger( int id, FFContext context ) {
        super( id );
        this.context = context;
        context.registerListener( WorkflowEvent.class, this );
    }
    
    @Override
    public final IIndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

    @Override
    public final void onEvent( WorkflowEvent event ) {
        if ( !workflowName.equals( event.workflowName ) ) {
            return;
        }
        
        switch ( triggerType ) {
            case STATE_CHANGE: {
                if ( event.type != WorkflowEvent.Type.STATE_CHANGED || !triggerName.equals( event.stateChangeName ) ) {
                    return;
                }
                break;
            }
            case ENTER_STATE: {
                if ( !( event.type == WorkflowEvent.Type.STATE_CHANGED || event.type == WorkflowEvent.Type.WORKFLOW_STARTED ) || !triggerName.equals( event.targetStateName ) ) {
                    return;
                }
                break;
            }
            case EXIT_STATE: {
                if ( !( event.type == WorkflowEvent.Type.STATE_CHANGED || event.type == WorkflowEvent.Type.WORKFLOW_FINISHED ) || !triggerName.equals( event.sourceStateName ) ) {
                    return;
                }
                break;
            }
        }
        
        context.getSystemComponent( Task.TYPE_KEY, taskId ).run( context );
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
        
        workflowName = attributes.getValue( WORKFLOW_NAME, workflowName );
        triggerType = attributes.getValue( TRIGGER_TYPE, triggerType );
        triggerName = attributes.getValue( TRIGGER_NAME, triggerName );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( WORKFLOW_NAME, workflowName );
        attributes.put( TRIGGER_TYPE, triggerType );
        attributes.put( TRIGGER_NAME, triggerName );
    }

    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( WorkflowEvent.class, this );
    }


}
