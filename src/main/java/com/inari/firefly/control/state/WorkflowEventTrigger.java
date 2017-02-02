package com.inari.firefly.control.state;

import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.utils.Condition;
import com.inari.firefly.system.utils.Trigger;
import com.inari.firefly.system.utils.Triggerer;

public abstract class WorkflowEventTrigger extends Trigger implements WorkflowEventListener {
    
    public enum Type {
        STATE_CHANGE,
        ENTER_STATE,
        EXIT_STATE
    }

    private final String workflowName;
    private final Type triggerType;
    private final String triggerName;
    
    public WorkflowEventTrigger( String workflowName, Type triggerType, String triggerName ) {
        super( null );
        this.workflowName = workflowName;
        this.triggerType = triggerType;
        this.triggerName = triggerName;
    }

    public WorkflowEventTrigger( String workflowName, Type triggerType, String triggerName, Condition condition ) {
        super( condition );
        this.workflowName = workflowName;
        this.triggerType = triggerType;
        this.triggerName = triggerName;
    }
    
    @Override
    public final void register( FFContext context, int componentId, Triggerer triggerer ) {
        super.register( context, componentId, triggerer );
        context.registerListener( WorkflowEvent.TYPE_KEY, this );
    }

    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( WorkflowEvent.TYPE_KEY, this );
    }

    @Override
    public final void onEvent( WorkflowEvent event ) {
        if ( !workflowName.equals( event.workflowName ) ) {
            return;
        }
        
        if ( condition != null && !condition.check( context ) ) {
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
        
        triggerer.trigger( context, componentId );
    }
}
