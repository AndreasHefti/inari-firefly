package com.inari.firefly.control.action;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.system.component.SystemComponent;

public abstract class Action extends SystemComponent {
    
    public static final SystemComponentKey<Action> TYPE_KEY = SystemComponentKey.create( Action.class );
    
    protected Action( int id ) {
        super( id );
    }

    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }
    
    public abstract void action( int entityId );
}
