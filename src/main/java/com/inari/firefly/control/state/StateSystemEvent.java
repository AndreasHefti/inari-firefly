package com.inari.firefly.control.state;

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
    protected final void notify( StateSystem listener ) {
        switch ( type ) {
            case DO_STATE_CHANGE: {
                int wId = workflowId;
                if ( wId < 0 ) {
                    wId = listener.workflowMap().getId( workflowName );
                }
                
                if ( stateChangeName != null ) {
                    listener.doStateChange( wId, stateChangeName );
                } else if ( targetStateName != null ) {
                    listener.changeState( wId, targetStateName );
                }
                break;
            }
            default: {}
        }
    }
    
    public static final StateSystemEvent createDoStateChangeEvent( String workflowName, String stateChangeName ) {
        return new StateSystemEvent( Type.DO_STATE_CHANGE, -1, workflowName, stateChangeName, null, null );
    }
    
    public static final StateSystemEvent createDoStateChangeEventTo( String workflowName, String targetStateName ) {
        return new StateSystemEvent( Type.DO_STATE_CHANGE, -1, workflowName, null, null, targetStateName );
    }

}
