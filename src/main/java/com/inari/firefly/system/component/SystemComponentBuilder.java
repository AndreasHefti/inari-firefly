package com.inari.firefly.system.component;

import java.util.Iterator;

import com.inari.firefly.FFInitException;
import com.inari.firefly.component.ComponentId;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.ComponentAttributeMap;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentConsumer;
import com.inari.firefly.component.build.Singleton;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public abstract class SystemComponentBuilder extends BaseComponentBuilder<SystemComponent> {
    
    @SuppressWarnings( "unchecked" )
    protected SystemComponentBuilder( FFContext context, Class<?> componentType ) {
        super( context, new ComponentAttributeMap( context ), (Class<SystemComponent>) componentType );
    }
    
    protected SystemComponentBuilder( FFContext context ) {
        super( context, new ComponentAttributeMap( context ), null );
    }
    
    protected SystemComponentBuilder( FFContext context, AttributeMap attributes ) {
        super( context, attributes, null );
    }

    public abstract SystemComponentKey<?> systemComponentKey();
    
    public final int build() {
        int componentId = getId();
        int id = doBuild( componentId, ( componentType != null )? componentType : systemComponentKey().baseComponentType(), false );
        attributes.clear();
        return id;
    }
    
    public final void build( int componentId ) {
        doBuild( componentId, ( componentType != null )? componentType : systemComponentKey().baseComponentType(), false );
    }
    
    public final FFContext build( ComponentConsumer consumer ) {
        consumer.add( new ComponentId( systemComponentKey(), build() ) );
        return context;
    }
    
    public final int activate() {
        int componentId = getId();
        int id = doBuild( componentId, ( componentType != null )? componentType : systemComponentKey().baseComponentType(), true );
        attributes.clear();
        return id;
    }
    
    public final void activate( int componentId ) {
        doBuild( componentId, ( componentType != null )? componentType : systemComponentKey().baseComponentType(), true );
    }
    
    public final FFContext activate( ComponentConsumer consumer ) {
        consumer.add( new ComponentId( systemComponentKey(), activate() ) );
        return context;
    }
    
    protected <SC extends SystemComponent> SC createSystemComponent( int componentId, Class<?> componentType ) {
        if ( !systemComponentKey().indexedType.isAssignableFrom( componentType ) ) {
            throw new FFInitException( "Component Builder Type missmatch. builderType: " + componentType.getName() + " is not a valid substitute of type: " + componentType.getName() );
        }
 
        @SuppressWarnings( "unchecked" )
        SC systemComponent = getInstance( componentId, (Class<SC>) componentType );
        systemComponent.injectContext( context );
        systemComponent.fromAttributes( attributes );
        systemComponent.init();
        
        if ( componentType.getAnnotation( Singleton.class ) != null ) {
            checkSingleton( componentType );
        }
        
        return systemComponent;
    }
    
    private void checkSingleton( Class<?> componentType ) {
        Iterator<? extends SystemComponent> systemComponents = context.getSystemComponents( systemComponentKey() );
        while ( systemComponents.hasNext() ) {
            SystemComponent c = systemComponents.next();
            if ( c.getClass() == componentType ) {
                throw new FFInitException( "Singleton SystemComponent check failed on: " + componentType.getName() );
            }
        }
    }

}
