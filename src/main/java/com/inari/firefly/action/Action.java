package com.inari.firefly.action;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.component.SystemComponent;

public abstract class Action extends SystemComponent implements FFContextInitiable {
    
    public static final SystemComponentKey TYPE_KEY = SystemComponentKey.create( Action.class );

    protected Action( int id ) {
        super( id );
    }

    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

    @Override
    public final Class<Action> componentType() {
        return Action.class;
    }

    @Override
    public final Class<Action> indexedObjectType() {
        return Action.class;
    }
    
    public abstract void performAction( int entityId );
}
