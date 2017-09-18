package com.inari.firefly.system.component;

import java.util.HashMap;
import java.util.Map;

import com.inari.firefly.FFInitException;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public final class SystemComponentNameMap<C extends SystemComponent> extends SystemComponentMap<C> {
    
    private final Map<String, C> nameMapping;

    @SuppressWarnings( "unchecked" )
    public SystemComponentNameMap( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey
    ) {
        this( system, componentKey, UNSUPPORTED_ACTIVATION, DUMMY_BUILDER_ADAPTER, 20, 10 );
    }
    
    @SuppressWarnings( "unchecked" )
    public SystemComponentNameMap( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        int cap, int grow 
    ) {
        this( system, componentKey, UNSUPPORTED_ACTIVATION, DUMMY_BUILDER_ADAPTER, cap, grow );
    }
    
    public SystemComponentNameMap( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        Activation activationAdapter, 
        BuilderAdapter<C> builderAdapter
    ) {
        this( system, componentKey, activationAdapter, builderAdapter, 20, 10 );
    }
    
    public SystemComponentNameMap( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        Activation activationAdapter, 
        BuilderAdapter<C> builderAdapter, 
        int cap, int grow  
    ) {
        super( system, componentKey, activationAdapter, builderAdapter, cap, grow );
        nameMapping = new HashMap<>( cap );
    }

    @Override
    public final void set( int index, C component ) {
        String name = component.getName();
        
        if ( name == null || nameMapping.containsKey( name ) ) {
            throw new FFInitException( "Compponent with name: " + name + " already mapped" );
        }
        
        super.set( index, component );
        nameMapping.put( name, component );
    }

    @Override
    public final int getId( String name ) {
        if ( name == null ) {
            return -1;
        }
        
        if ( !nameMapping.containsKey( name ) ) {
            return -1;
        }
        
        return nameMapping.get( name ).index();
    }

    @Override
    public final C remove( int id ) {
        C removed = super.remove( id );
        if ( removed != null ) {
            nameMapping.remove( removed.getName() );
        }
        
        return removed;
    }

    @Override
    public final void clear() {
        super.clear();
        nameMapping.clear();
    }

}
