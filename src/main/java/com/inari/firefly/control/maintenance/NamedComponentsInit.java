package com.inari.firefly.control.maintenance;

import com.inari.firefly.system.component.IComponentName;

public abstract class NamedComponentsInit extends Init {

    protected NamedComponentsInit( int index ) {
        super( index );
    }
    
    protected abstract IComponentName[] getComponentNames();


    protected void cleanup() {
        IComponentName[] componentNames = getComponentNames();
        if ( componentNames == null ) {
            return;
        }
        
        for ( int i = 0; i < componentNames.length; i++ ) {
            if ( componentNames[ i ] != null ) {
                context.deleteComponent( componentNames[ i ] );
            }
        }
    }

}
