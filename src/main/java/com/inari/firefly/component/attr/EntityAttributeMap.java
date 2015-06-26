package com.inari.firefly.component.attr;

import java.util.HashSet;
import java.util.Set;

import com.inari.firefly.component.Component;
import com.inari.firefly.entity.Entity;

public class EntityAttributeMap extends ComponentAttributeMap {
    
    @Override
    public final void setComponentKey( ComponentKey typeKey ) {
        if ( typeKey.getType() != Entity.class ) {
            throw new IllegalArgumentException( "The ComponentKey has not the expected type: " + Entity.class.getName() );
        }
        super.setComponentKey( typeKey );
    }

    public Set<Class<? extends Component>> getEntityComponentTypes() {
        Set<Class<? extends Component>> componentTypes = new HashSet<Class<? extends Component>>();
        for ( AttributeKey<?> attrKey : attributes.keySet() ) {
            componentTypes.add( attrKey.componentType );
        }
        return componentTypes;
    }

}
