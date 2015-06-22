package com.inari.firefly.component.build;

import java.lang.reflect.Constructor;

import com.inari.firefly.FFContext;
import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.AttributeMap;
import com.inari.firefly.component.AttributeMapImpl;
import com.inari.firefly.component.Component;

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
    
    protected C getInstance( int componentId ) {
        return getInstance( null, componentId );
    }
    
    protected C getInstance( FFContext context, Integer componentId ) {
        try {
            String className = attributes.getValue( Component.INSTANCE_TYPE_NAME );
            if ( className == null ) {
                throw new ComponentCreationException( "Missing mandatory attribute " + Component.INSTANCE_TYPE_NAME + " for StateChangeCondition" );
            }
            @SuppressWarnings( "unchecked" )
            Class<C> typeClass = (Class<C>) Class.forName( className );
            if ( componentId == null ) {
                try {
                    Constructor<C> constructor = typeClass.getDeclaredConstructor();
                    return createInstance( constructor );
                } catch ( Throwable t ) {
                    if ( context == null ) {
                        throw new ComponentCreationException( "No Component: " + className + " with default constructor found", t );
                    }
                    Constructor<C> constructor = typeClass.getDeclaredConstructor( FFContext.class );
                    return createInstance( constructor, context );
                }
            } else {
                try {
                    Constructor<C> constructor = typeClass.getDeclaredConstructor( int.class );
                    return createInstance( constructor, componentId );
                } catch ( Throwable t ) {
                    if ( context == null ) {
                        throw new ComponentCreationException( "No Component: " + className + " with default constructor found", t );
                    }
                    Constructor<C> constructor = typeClass.getDeclaredConstructor( int.class, FFContext.class );
                    return createInstance( constructor, componentId, context );
                }
            }
        } catch ( Exception e ) {
            throw new ComponentCreationException( "Unexpected Exception while trying to instantiate Component", e );
        }
    }
    
    protected C createInstance( Constructor<C> constructor, Object... paramValues ) throws Exception {
        return constructor.newInstance( paramValues );
    }

}
