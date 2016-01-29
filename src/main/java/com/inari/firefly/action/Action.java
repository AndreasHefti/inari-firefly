package com.inari.firefly.action;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.component.SystemComponent;

public abstract class Action extends SystemComponent implements FFContextInitiable {
    
    public static final SystemComponentKey<Action> TYPE_KEY = SystemComponentKey.create( Action.class );

    protected Action( int id ) {
        super( id );
    }

    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }
    
    public abstract void performAction( int entityId );
}
