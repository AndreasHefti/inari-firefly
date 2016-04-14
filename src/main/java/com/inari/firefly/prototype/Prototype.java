package com.inari.firefly.prototype;

import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.ComponentId;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.Disposable;
import com.inari.firefly.system.Loadable;
import com.inari.firefly.system.component.SystemComponent;

public abstract class Prototype extends  SystemComponent implements Loadable, Disposable {
    
    protected Prototype( int id ) {
        super( id );
    }

    public static final SystemComponentKey<Prototype> TYPE_KEY = SystemComponentKey.create( Prototype.class );

    @Override
    public final IIndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

    public abstract DynArray<ComponentId> createOne( AttributeMap attributes );

}
