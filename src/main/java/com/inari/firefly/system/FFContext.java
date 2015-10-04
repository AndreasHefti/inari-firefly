package com.inari.firefly.system;

import java.util.Map;
import java.util.Set;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.TypedKey;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.ComponentSystem.BuildType;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.entity.EntityProvider;

public interface FFContext {
    
    /** The key for the event dispatcher service that provides the system wide event handling.
     *  Use the event dispatcher service to register listeners or fire certain events.
     *  For more information please have a look at the documentation of {@link IEventDispatcher}.
     */
    public static final TypedKey<IEventDispatcher> EVENT_DISPATCHER = TypedKey.create( "FF_EVENT_DISPATCHER", IEventDispatcher.class );
    
    /** The key for the interface to lower system that must be implements for a specified context like lwjgl, libGDX, Processing...
     *  The class type of an specified implementation of the LOWER_SYSTEM_FACADE must be defined by initializing of the FFContext
     */
    public static final TypedKey<LowerSystemFacade> LOWER_SYSTEM_FACADE = TypedKey.create( "FF_LOWER_SYSTEM_FACADE", LowerSystemFacade.class );
    
    public static final TypedKey<FFTimer> TIMER = TypedKey.create( "FF_TIMER", FFTimer.class );
    
    public static final TypedKey<Input> INPUT = TypedKey.create( "FF_INPUT", Input.class );

    public static final TypedKey<EntityProvider> ENTITY_PROVIDER = TypedKey.create( "FF_ENTITY_PROVIDER", EntityProvider.class );

    public static interface Properties {
        public static final TypedKey<Integer> ENTITY_MAP_CAPACITY = TypedKey.create( "FF_ENTITY_MAP_CAPACITY", Integer.class );
        public static final TypedKey<Integer> ENTITY_COMPONENT_SET_CAPACITY = TypedKey.create( "FF_ENTITY_COMPONENT_SET_CAPACITY", Integer.class );
        public static final TypedKey<Integer> ENTITY_BEANS_CACHE_SIZE = TypedKey.create( "FF_ENTITY_BEANS_CACHE_SIZE", Integer.class );
    }


    <T> T getComponent( TypedKey<T> key );
    
    <T> void putComponent( TypedKey<T> key, T component );

    <T> T getProperty( TypedKey<T> key );
    
    void fromAttributes( Attributes attributes, BuildType buildType );
    
    void toAttributes( Attributes attributes );
    
    Map<TypedKey<? extends ComponentSystem>, Set<Class<?>>> getComponentTypes();

    int getScreenWidth();
    
    int getScreenHeight();

}
