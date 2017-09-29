package com.inari.firefly.control.maintenance;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.firefly.FFInitException;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentMap;
import com.inari.firefly.system.component.SystemComponentMap.BuilderListenerAdapter;

public final class MaintenanceSystem extends ComponentSystem<MaintenanceSystem> {
    
    public static final FFSystemTypeKey<MaintenanceSystem> SYSTEM_KEY = FFSystemTypeKey.create( MaintenanceSystem.class );
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        Init.TYPE_KEY
    );
    
    private SystemComponentMap<Init> inits;

    protected MaintenanceSystem() {
        super( SYSTEM_KEY );
    }

    public void init( final FFContext context ) throws FFInitException {
        super.init( context );
        inits = new SystemComponentMap<Init>( 
            this, Init.TYPE_KEY,
            new BuilderListenerAdapter<Init>() {
                public void notifyActivation( int id ) { init( id ); }
                public void notifyDeactivation( int id ) { dispose( id ); }
            }
        );
    }
    
    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }
    
    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            inits.getBuilderAdapter()
        );
    }
    
    public final FFContext init( int id ) {
        if ( inits.map.contains( id ) ) {
            inits.get( id ).init( context );
        }
        
        return context;
    }
    
    public final FFContext dispose( int id ) {
        if ( inits.map.contains( id ) ) {
            inits.get( id ).dispose( context );
        }
        
        return context;
    }

    public final void dispose( FFContext context ) {
        for ( Init init : inits.map ) {
            if ( init.isInitialised() ) {
                init.dispose( context );
            }
        }
        
        clearSystem();
    }

    public final void clearSystem() {
        inits.clear();
    }

}
