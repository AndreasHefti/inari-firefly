package com.inari.firefly.state;

import com.inari.commons.event.Event;

public final class StateSystemEvent extends Event<StateSystem> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( StateSystemEvent.class );
    
    public enum Type {
        DO_STATE_CHANGE
    }
    
    public final Type type;
    
    public final int workflowId;
    public final String workflowName;
    public final String stateChangeName;
    
    public final String sourceStateName;
    public final String targetStateName;
    
    public StateSystemEvent( 
        Type type, 
        int workflowId,
        String workflowName, 
        String stateChangeName, 
        String sourceStateName, 
        String targetStateName 
    ) {
        super( TYPE_KEY );
        this.type = type;
        this.workflowId = workflowId;
        this.workflowName = workflowName;
        this.stateChangeName = stateChangeName;
        this.sourceStateName = sourceStateName;
        this.targetStateName = targetStateName;
    }

    @Override
    public final void notify( StateSystem listener ) {
        listener.onEvent( this );
    }
    
    public static final StateSystemEvent createDoStateChangeEvent( String workflowName, String stateChangeName ) {
        return new StateSystemEvent( Type.DO_STATE_CHANGE, -1, workflowName, stateChangeName, null, null );
    }
    
    public static final StateSystemEvent createDoStateChangeEventTo( String workflowName, String targetStateName ) {
        return new StateSystemEvent( Type.DO_STATE_CHANGE, -1, workflowName, null, null, targetStateName );
    }

}
