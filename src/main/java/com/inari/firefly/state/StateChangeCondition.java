package com.inari.firefly.state;

import com.inari.firefly.component.NamedIndexedComponent;


public abstract class StateChangeCondition extends NamedIndexedComponent {
    
    protected StateChangeCondition( int stateChangeConditionId ) {
        super( stateChangeConditionId );
    }

    @Override
    public final Class<StateChangeCondition> getComponentType() {
        return StateChangeCondition.class;
    }

    @Override
    public final Class<StateChangeCondition> getIndexedObjectType() {
        return StateChangeCondition.class;
    }

    public abstract boolean check( Workflow workflow, long update );
    
}
