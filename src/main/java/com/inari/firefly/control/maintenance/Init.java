package com.inari.firefly.control.maintenance;

import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.SystemComponent;
import com.inari.firefly.system.utils.Disposable;
import com.inari.firefly.system.utils.Initiable;

public abstract class Init extends SystemComponent implements Initiable, Disposable {
    
    public static final SystemComponentKey<Init> TYPE_KEY = SystemComponentKey.create( Init.class );
    
    private boolean initialised;

    protected Init( int index ) {
        super( index );
    }

    public final IIndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }
    
    public final boolean isInitialised() {
        return initialised;
    }

    public final Disposable init( final FFContext context ) {
        if ( initialised ) {
            return this;
        }
        
        initialise();
        
        initialised = true;
        return this;
    }
    
    public void dispose( final FFContext context ) {
        if ( !initialised ) {
            return;
        }
        
        cleanup();
        initialised = false;
    }
    
    protected abstract Disposable initialise();
    protected abstract void cleanup();
    

}
