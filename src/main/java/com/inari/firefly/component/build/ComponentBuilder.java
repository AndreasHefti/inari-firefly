package com.inari.firefly.component.build;

import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public interface ComponentBuilder<C> {
    
    ComponentBuilder<C> clear();
    
    ComponentBuilder<C> setAttributes( AttributeMap attributes );
    
    AttributeMap getAttributes();
    
    ComponentBuilder<C> setAttribute( AttributeKey<?> key, Object value );
    
    C build();
    
    C build( int componentId );
    
    ComponentBuilder<C> buildAndNext();
    
    <CC> ComponentBuilder<CC> buildAndNext( Class<CC> componentType );
    
    ComponentBuilder<C> buildAndNext( int componentId );
    
    <CC> ComponentBuilder<CC> buildAndNext( int componentId, Class<CC> componentType );

}
