package com.inari.firefly.system.component;

import com.inari.firefly.FFInitException;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.ComponentAttributeMap;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public abstract class SystemComponentBuilder extends BaseComponentBuilder<SystemComponent> {
    
    @SuppressWarnings( "unchecked" )
    protected SystemComponentBuilder( FFContext context, Class<?> componentType ) {
        super( new ComponentAttributeMap( context ), (Class<SystemComponent>) componentType );
    }
    
    protected SystemComponentBuilder( FFContext context ) {
        super( new ComponentAttributeMap( context ), null );
    }
    
    protected SystemComponentBuilder( AttributeMap attributes ) {
        super( attributes, null );
    }

    public abstract SystemComponentKey<?> systemComponentKey();
    
    @Override
    public final int build() {
        int componentId = getId();
        int id = doBuild( componentId, ( componentType != null )? componentType : systemComponentKey().baseComponentType(), false );
        attributes.clear();
        return id;
    }
    
    @Override
    public final void build( int componentId ) {
        doBuild( componentId, ( componentType != null )? componentType : systemComponentKey().baseComponentType(), false );
    }
    
    @Override
    public final int activate() {
        int componentId = getId();
        int id = doBuild( componentId, ( componentType != null )? componentType : systemComponentKey().baseComponentType(), true );
        attributes.clear();
        return id;
    }
    
    @Override
    public final void activate( int componentId ) {
        doBuild( componentId, ( componentType != null )? componentType : systemComponentKey().baseComponentType(), true );
    }
    
    protected <SC extends SystemComponent> SC createSystemComponent( int componentId, Class<?> componentType, FFContext context ) {
        if ( !systemComponentKey().indexedType.isAssignableFrom( componentType ) ) {
            throw new FFInitException( "Component Builder Type missmatch. builderType: " + componentType.getName() + " is not a valid substitute of type: " + componentType.getName() );
        }
 
        @SuppressWarnings( "unchecked" )
        SC systemComponent = getInstance( componentId, (Class<SC>) componentType );
        systemComponent.injectContext( context );
        systemComponent.fromAttributes( attributes );
        systemComponent.init();
        
        return systemComponent;
    }

}
