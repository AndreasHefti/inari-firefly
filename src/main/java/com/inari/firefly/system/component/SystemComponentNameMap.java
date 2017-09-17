package com.inari.firefly.system.component;

import java.util.HashMap;
import java.util.Map;

import com.inari.firefly.FFInitException;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public final class SystemComponentNameMap<C extends SystemComponent> extends SystemComponentMap<C> {
    
    private final Map<String, C> nameMapping;

    protected SystemComponentNameMap( SystemComponentKey<C> componentKey, Activation activationAdapter, BuilderAdapter<C> builderAdapter, int cap ) {
        super( componentKey, activationAdapter, builderAdapter, cap );
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
    
    @SuppressWarnings( "unchecked" )
    public final static <C extends  SystemComponent> SystemComponentNameMap<C> create( SystemComponentKey<C> componentKey ) {
        return new SystemComponentNameMap<C>( componentKey, UNSUPPORTED_ACTIVATION, DUMMY_BUILDER_ADAPTER, 20 );
    }
    
    @SuppressWarnings( "unchecked" )
    public final static <C extends  SystemComponent> SystemComponentNameMap<C> create( SystemComponentKey<C> componentKey, Activation activationAdapter ) {
        return new SystemComponentNameMap<C>( componentKey, activationAdapter, DUMMY_BUILDER_ADAPTER, 20 );
    }
    
    public final static <C extends  SystemComponent> SystemComponentNameMap<C> create( SystemComponentKey<C> componentKey, Activation activationAdapter, BuilderAdapter<C> builderAdapter ) {
        return new SystemComponentNameMap<C>( componentKey, activationAdapter, builderAdapter, 20 );
    }
    
    @SuppressWarnings( "unchecked" )
    public final static <C extends  SystemComponent> SystemComponentNameMap<C> create( SystemComponentKey<C> componentKey, int cap ) {
        return new SystemComponentNameMap<C>( componentKey, UNSUPPORTED_ACTIVATION, DUMMY_BUILDER_ADAPTER, cap );
    }
    
    @SuppressWarnings( "unchecked" )
    public final static <C extends  SystemComponent> SystemComponentNameMap<C> create( SystemComponentKey<C> componentKey, Activation activationAdapter, int cap ) {
        return new SystemComponentNameMap<C>( componentKey, activationAdapter, DUMMY_BUILDER_ADAPTER, cap );
    }
    
    public final static <C extends  SystemComponent> SystemComponentNameMap<C> create( SystemComponentKey<C> componentKey, Activation activationAdapter, BuilderAdapter<C> builderAdapter, int cap ) {
        return new SystemComponentNameMap<C>( componentKey, activationAdapter, builderAdapter, cap );
    }

}
