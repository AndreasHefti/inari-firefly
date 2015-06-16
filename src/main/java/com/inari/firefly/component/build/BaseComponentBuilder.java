package com.inari.firefly.component.build;

import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.AttributeMap;
import com.inari.firefly.component.AttributeMapImpl;

public abstract class BaseComponentBuilder<C> implements ComponentBuilder<C>{
    
    protected final AttributeMap attributes = new AttributeMapImpl();
    private final ComponentBuilderFactory componentFactory;
    
    protected BaseComponentBuilder( ComponentBuilderFactory componentFactory ) {
        this.componentFactory = componentFactory;
    }
    
    @Override
    public final void clear() {
        attributes.clear();
    }

    @Override
    public final BaseComponentBuilder<C> setAttributes( AttributeMap attributes ) {
        attributes.putAll( attributes );
        return this;
    }
    
    @Override
    public final ComponentBuilder<C> setAttribute( AttributeKey<?> key, Object value ) {
        attributes.putUntyped( key, value );
        return this;
    }

    @Override
    public C build() {
        return build( -1 );
    }

    @Override
    public final ComponentBuilder<C> buildAndNext() {
        build( -1 );
        return this;
    }

    @Override
    public final <CC> ComponentBuilder<CC> buildAndNext( Class<CC> componentType ) {
        build( -1 );
        return componentFactory.getComponentBuilder( componentType );
    }

    @Override
    public final ComponentBuilder<C> buildAndNext( int componentId ) {
        build( componentId );
        return this;
    }

    @Override
    public final <CC> ComponentBuilder<CC> buildAndNext( int componentId, Class<CC> componentType ) {
        build( componentId );
        return componentFactory.getComponentBuilder( componentType );
    }

}
