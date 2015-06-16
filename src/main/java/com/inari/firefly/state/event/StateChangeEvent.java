package com.inari.firefly.state.event;

import com.inari.commons.event.Event;
import com.inari.firefly.state.StateChange;

public final class StateChangeEvent extends Event<StateChangeListener> {
    
    public final StateChange stateChange;
    
    public StateChangeEvent( StateChange stateChange ) {
        this.stateChange = stateChange;
    }

    @Override
    public final void notify( StateChangeListener listener ) {
        listener.onStateChange( this );
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "StateChangeEvent [stateChange=" );
        builder.append( stateChange );
        builder.append( "]" );
        return builder.toString();
    }

}
