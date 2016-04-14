package com.inari.firefly.control.task;

import com.inari.firefly.control.state.WorkflowEventTrigger;
import com.inari.firefly.system.Condition;

public final class WorkflowTaskTrigger extends WorkflowEventTrigger {

    public WorkflowTaskTrigger( String workflowName, Type triggerType, String triggerName ) {
        super( workflowName, triggerType, triggerName, null );
    }

    public WorkflowTaskTrigger( String workflowName, Type triggerType, String triggerName, Condition condition ) {
        super( workflowName, triggerType, triggerName, condition );
    }

    @Override
    protected final void trigger() {
        if ( componentId < 0 ) {
            return;
        }
        
        context.getSystem( TaskSystem.SYSTEM_KEY ).runTask( componentId );
    }

}
