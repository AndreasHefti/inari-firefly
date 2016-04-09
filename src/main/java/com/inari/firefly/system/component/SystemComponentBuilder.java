package com.inari.firefly.system.component;

import com.inari.firefly.FFInitException;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.ComponentAttributeMap;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public abstract class SystemComponentBuilder extends BaseComponentBuilder<SystemComponent> {
    
    protected SystemComponentBuilder( FFContext context ) {
        super( new ComponentAttributeMap( context ) );
    }
    
    protected SystemComponentBuilder( AttributeMap attributes ) {
        super( attributes );
    }

    public abstract SystemComponentKey<?> systemComponentKey();
    
    @Override
    public final int build() {
        int componentId = getId();
        int id = doBuild( componentId, systemComponentKey().indexedType, false );
        attributes.clear();
        return id;
    }
    
    @Override
    public final void build( int componentId ) {
        doBuild( componentId, systemComponentKey().indexedType, false );
    }
    
    @Override
    public final int activate() {
        int componentId = getId();
        int id = doBuild( componentId, systemComponentKey().indexedType, true );
        attributes.clear();
        return id;
    }
    
    @Override
    public final void activate( int componentId ) {
        doBuild( componentId, systemComponentKey().indexedType, true );
    }
    
    protected <SC extends SystemComponent> SC createSystemComponent( int componentId, Class<?> componentType, FFContext context ) {
        if ( !systemComponentKey().indexedType.isAssignableFrom( componentType ) ) {
            throw new FFInitException( "Component Builder Type missmatch. builderType: " + componentType.getName() + " is not a valid substitute of type: " + componentType.getName() );
        }
        attributes.put( Component.INSTANCE_TYPE_NAME, componentType.getName() );
            
        SC systemComponent = getInstance( componentId );
        systemComponent.injectContext( context );
        systemComponent.fromAttributes( attributes );
        systemComponent.init();
        
        return systemComponent;
    }

}
