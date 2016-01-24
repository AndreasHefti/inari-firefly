package com.inari.firefly.task;

import com.inari.firefly.state.WorkflowEvent;
import com.inari.firefly.state.WorkflowEventListener;
import com.inari.firefly.system.FFContext;

public final class WorkflowEventTrigger implements TaskTrigger, WorkflowEventListener {
    
    public enum Type {
        STATE_CHANGE,
        ENTER_STATE,
        EXIT_STATE
    }

    private final String workflowName;
    private final Type triggerType;
    private final String triggerName;
    
    private FFContext context;
    private Task task;

    public WorkflowEventTrigger( String workflowName, Type triggerType, String triggerName ) {
        this.workflowName = workflowName;
        this.triggerType = triggerType;
        this.triggerName = triggerName;
    }
    
    @Override
    public void connect( FFContext context, Task task ) {
        if ( this.context == null ) {
            this.context = context;
        }
        if ( this.task == null ) {
            this.task = task;
        }
        
        context.registerListener( WorkflowEvent.class, this );
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
        
        task.run( context );
    }

    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( WorkflowEvent.class, this );
        
    }

    @Override
    public void fromConfigString( String stringValue ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String toConfigString() {
        // TODO Auto-generated method stub
        return null;
    }

}
