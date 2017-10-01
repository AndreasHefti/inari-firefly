package com.inari.firefly.control.maintenance;

import java.util.Collection;

import com.inari.firefly.system.component.IComponentName;

public abstract class NamedComponentsInit extends Init {

    protected NamedComponentsInit( int index ) {
        super( index );
    }
    
    protected abstract Collection<IComponentName> getComponentNames();


    protected void cleanup() {
        for ( IComponentName cName : getComponentNames() ) {
            context.deleteComponent( cName );
        }
    }

}
