package com.inari.firefly.control.action;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.component.build.Singleton;
import com.inari.firefly.system.component.SystemComponent;

@Singleton
public abstract class Action extends SystemComponent {
    
    public static final SystemComponentKey<Action> TYPE_KEY = SystemComponentKey.create( Action.class );
    
    protected Action( int id ) {
        super( id );
    }

    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }
    
    public abstract void action( int entityId );
}
