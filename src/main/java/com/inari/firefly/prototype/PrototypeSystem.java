package com.inari.firefly.prototype;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.ComponentId;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentMap;

public class PrototypeSystem extends ComponentSystem<PrototypeSystem> {
    
    public static final FFSystemTypeKey<PrototypeSystem> SYSTEM_KEY = FFSystemTypeKey.create( PrototypeSystem.class );
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        Prototype.TYPE_KEY
    );

    private final SystemComponentMap<Prototype> prototypes;
    
    PrototypeSystem() {
        super( SYSTEM_KEY );
        prototypes = new SystemComponentMap<>( this, Prototype.TYPE_KEY, 20, 10 );
    }

    @Override
    public final void init( FFContext context ) {
        super.init( context );
    }
    
    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            prototypes.getBuilderAdapter()
        );
    }

    public final DynArray<ComponentId> createOne( int prototypeId, AttributeMap attributes ) {
        if ( prototypes.map.contains( prototypeId ) ) {
            return null;
        }
        
        return prototypes.map
            .get( prototypeId )
            .createOne( attributes );
    }

    public final void dispose( FFContext context ) {
        clearSystem();
    }
    
    public final void clearSystem() {
        prototypes.clear();
    }

}
