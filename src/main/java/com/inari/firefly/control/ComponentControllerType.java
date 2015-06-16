package com.inari.firefly.control;

import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.entity.EntityComponent;

public interface ComponentControllerType {
    
    AttributeKey<Integer> getAttribute();
    
    Class<? extends EntityComponent> getType();

    int getControllerId( IndexedTypeSet entityComponents );

}
