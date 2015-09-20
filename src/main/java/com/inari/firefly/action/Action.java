package com.inari.firefly.action;

import com.inari.firefly.component.NamedIndexedComponent;
import com.inari.firefly.system.FFContextInitiable;

public abstract class Action extends NamedIndexedComponent implements FFContextInitiable {
    
    protected Action( int id ) {
        super( id );
    }
    
    @Override
    public final Class<Action> getComponentType() {
        return Action.class;
    }

    @Override
    public final Class<Action> indexedObjectType() {
        return Action.class;
    }
    
    public abstract void performAction( int entityId );
}
