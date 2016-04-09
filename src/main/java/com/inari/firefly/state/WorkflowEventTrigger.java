package com.inari.firefly.state;

import com.inari.firefly.system.Condition;
import com.inari.firefly.system.EventTrigger;
import com.inari.firefly.system.FFContext;

public abstract class WorkflowEventTrigger extends EventTrigger implements WorkflowEventListener {
    
    public enum Type {
        STATE_CHANGE,
        ENTER_STATE,
        EXIT_STATE
    }

    private final String workflowName;
    private final Type triggerType;
    private final String triggerName;
    
    

    public WorkflowEventTrigger( String workflowName, Type triggerType, String triggerName, Condition condition ) {
        super( condition );
        this.workflowName = workflowName;
        this.triggerType = triggerType;
        this.triggerName = triggerName;
    }
    
    @Override
    public final void register( FFContext context, int componentId ) {
        super.register( context, componentId );
        context.registerListener( WorkflowEvent.class, this );
    }

    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( WorkflowEvent.class, this );
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
        
        trigger();
    }
    
    protected abstract void trigger();
    
    @Override
    public final void fromConfigString( String stringValue ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public final String toConfigString() {
        // TODO Auto-generated method stub
        return null;
    }

}
