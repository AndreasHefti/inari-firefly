package com.inari.firefly.system.component;

import com.inari.firefly.action.Action;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public abstract class SystemComponentBuilder extends BaseComponentBuilder {
    
    protected SystemComponentBuilder() {
        super();
    }
    
    protected SystemComponentBuilder( AttributeMap attributes ) {
        super( attributes );
    }

    public abstract SystemComponentKey systemComponentKey();
    
    @Override
    public final int build() {
        int componentId = getId();
        return doBuild( componentId, systemComponentKey().indexedType );
    }
    
    @Override
    public final void build( int componentId ) {
        doBuild( componentId, systemComponentKey().indexedType );
    }
    
    protected void checkType( Class<?> componentType ) {
        if ( !systemComponentKey().indexedType.isAssignableFrom( componentType ) ) {
            throw new FFInitException( "Component Builder Type missmatch. builderType: " + Action.class.getName() + " is not a valid substitute of type: " + componentType.getName() );
        }
    }

}
