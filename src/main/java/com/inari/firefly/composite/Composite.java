package com.inari.firefly.composite;

import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.Disposable;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.SystemComponent;

public abstract class Composite extends SystemComponent implements Disposable {
    
    public static final SystemComponentKey<Composite> TYPE_KEY = SystemComponentKey.create( Composite.class );

    protected Composite( int id ) {
        super( id );
    }

    @Override
    public final IIndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }
    
    public abstract void load( FFContext context );

}
