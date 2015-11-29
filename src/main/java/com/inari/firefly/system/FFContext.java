package com.inari.firefly.system;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;

import com.inari.commons.event.AspectedEvent;
import com.inari.commons.event.AspectedEventListener;
import com.inari.commons.event.Event;
import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.event.PredicatedEvent;
import com.inari.commons.event.PredicatedEventListener;
import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.Disposable;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.DataComponent;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.entity.EntityComponent.EntityComponentTypeKey;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.ComponentSystem.BuildType;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public final class FFContext {

    public static interface Properties {
        public static final TypedKey<Integer> ENTITY_MAP_CAPACITY = TypedKey.create( "FF_ENTITY_MAP_CAPACITY", Integer.class );
        public static final TypedKey<Integer> ENTITY_COMPONENT_SET_CAPACITY = TypedKey.create( "FF_ENTITY_COMPONENT_SET_CAPACITY", Integer.class );
        public static final TypedKey<Integer> ENTITY_BEANS_CACHE_SIZE = TypedKey.create( "FF_ENTITY_BEANS_CACHE_SIZE", Integer.class );
    }
    
    private final Map<TypedKey<?>, Object> properties =  new LinkedHashMap<TypedKey<?>, Object>();
    
    private final Map<TypedKey<? extends DataComponent>, DataComponent> dataComponents = new LinkedHashMap<TypedKey<? extends DataComponent>, DataComponent>();
    private final Map<TypedKey<? extends FFSystem>, FFSystem> componentSystems = new LinkedHashMap<TypedKey<? extends FFSystem>, FFSystem>();
    
    private final DynArray<SystemBuilderAdapter<?>> systemBuilderAdapter = new DynArray<SystemBuilderAdapter<?>>();
    
    private final IEventDispatcher eventDispatcher;
    private final FFSystemInterface systemInterface;
    private final FFTimer timer;
    private final Input input;
    
    private EntitySystem entitySystem;
    boolean exit = false;

    public FFContext( 
        IEventDispatcher eventDispatcher, 
        FFSystemInterface systemInterface, 
        FFTimer timer,
        Input input 
    ) {
        this.eventDispatcher = eventDispatcher;
        this.systemInterface = systemInterface;
        systemInterface.init( this );
        this.timer = timer;
        this.input = input;
    }

    public final IEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public final FFSystemInterface getSystemInterface() {
        return systemInterface;
    }

    public final FFTimer getTimer() {
        return timer;
    }

    public final Input getInput() {
        return input;
    }

    public final <T extends FFSystem> T getSystem( TypedKey<T> key ) {
        if ( !componentSystems.containsKey( key ) ) {
            loadSystem( key, true );
        }
        return key.cast( componentSystems.get( key ) );
    }
    
    public final <T extends FFSystem> void loadSystem( TypedKey<T> key ) {
        loadSystem( key, false );
    }
    
    public final <T extends FFSystem> void loadSystem( TypedKey<T> key, boolean force ) {
        if ( componentSystems.containsKey( key ) ) {
            if ( force ) {
                FFSystem oldSystem = componentSystems.remove( key );
                oldSystem.dispose( this );
            } else {
                throw new FFInitException( "The System for key: " + key + " already exists" );
            }
        }
        
        try {
            Class<T> typeClass = key.type();
            Constructor<T> constructor = typeClass.getDeclaredConstructor();
            boolean accessible = constructor.isAccessible();
            constructor.setAccessible( true );
            T componentSystem = constructor.newInstance();
            componentSystems.put( key, componentSystem );
            componentSystem.init( this );
            constructor.setAccessible( accessible );
            
            if ( componentSystem instanceof ComponentSystem ) {
                initComponentSystem( (ComponentSystem) componentSystem );
            }
            
            if ( key == EntitySystem.CONTEXT_KEY ) {
                entitySystem = (EntitySystem) componentSystem;
            }
        } catch ( Exception e ) {
            throw new FFInitException( "Failed to load system: " + key, e );
        }
    }
    
    public final <T extends FFSystem> void disposeSystem( TypedKey<T> key ) {
        @SuppressWarnings( "unchecked" )
        T system = (T) componentSystems.remove( key );
        if ( system != null ) {
            system.dispose( this );
        }
    }
    
    private final void initComponentSystem( ComponentSystem system ) {
        SystemBuilderAdapter<?>[] supportedBuilderAdapter = system.getSupportedBuilderAdapter();
        if ( supportedBuilderAdapter != null ) {
            for ( int i = 0; i < supportedBuilderAdapter.length; i++ ) {
                systemBuilderAdapter.set( supportedBuilderAdapter[ i ].componentTypeKey().index(), supportedBuilderAdapter[ i ] );
            }
        }
    }
    
    @SuppressWarnings( "unchecked" )
    public final <T extends Component> T getSystemComponent( SystemComponentKey key, int componentId ) {
        SystemBuilderAdapter<?> builderHelper = systemBuilderAdapter.get( key.index() );
        return (T) builderHelper.get( componentId, null );
    }
    
    @SuppressWarnings( "unchecked" )
    public final <T extends Component, S extends T> T getSystemComponent( SystemComponentKey key, Class<S> subType, int componentId ) {
        SystemBuilderAdapter<T> builderHelper = (SystemBuilderAdapter<T>) systemBuilderAdapter.get( key.index() );
        return builderHelper.get( componentId, (Class<S>) key.indexedType );
    }
    
    public final <T extends Component> void deleteSystemComponent( SystemComponentKey key, int componentId ) {
        SystemBuilderAdapter<?> builderHelper = systemBuilderAdapter.get( key.index() );
        builderHelper.delete( componentId, null );
    }
    
    public final <T extends DataComponent> T getComponent( TypedKey<T> componentKey ) {
        return componentKey.cast( dataComponents.get( componentKey ) );
    }
    
    public final <T extends DataComponent> void setComponent( T component ) {
        dataComponents.put( component.componentKey(), component );
    }
    
    public final void disposeComponent( TypedKey<? extends DataComponent> componentKey ) {
        DataComponent component = dataComponents.remove( componentKey );
        if ( component != null && component instanceof Disposable ) {
            ( (Disposable) component ).dispose( this );
        }
    }
    
    public final ComponentBuilder getComponentBuilder( SystemComponentKey key ) {
        int id = key.index();
        if ( !systemBuilderAdapter.contains( id ) ) {
            return null;
        }
        
        return systemBuilderAdapter.get( id ).getComponentBuilder();
    }
    
    public final <T extends EntityComponent> T getEntityComponent( int entityId, EntityComponentTypeKey typeKey ) {
        return entitySystem.getComponent( entityId, typeKey );
    }
    
    public final void deactivateEntity( int entityId ) {
        entitySystem.deactivate( entityId );
    }
    
    public final void deleteEntity( int entityId ) {
        entitySystem.delete( entityId );
    }

    public final <T> T getProperty( TypedKey<T> key ) {
        Object property = properties.get( key );
        if ( property == null ) {
            return null;
        }
        
        return key.cast( property );
    }
    
    public final <T> void addProperty( TypedKey<T> key, T value ) {
        properties.put( key, value );
    }
    
    public final <L> void registerListener( Class<? extends Event<L>> eventType, L listener ) {
        eventDispatcher.register( eventType, listener );
    }
    
    public final <L> void disposeListener( Class<? extends Event<L>> eventType, L listener ) {
        eventDispatcher.unregister( eventType, listener );
    }
    
    public final <L> void notify( final Event<L> event ) {
        eventDispatcher.notify( event );
    }
    
    public final <L extends AspectedEventListener> void notify( final AspectedEvent<L> event ) {
        eventDispatcher.notify( event );
    }
    
    public final <L extends PredicatedEventListener> void notify( final PredicatedEvent<L> event ) {
        eventDispatcher.notify( event );
    }
    
    public final void fromAttributes( Attributes attributes ) {
        fromAttributes( attributes, BuildType.CLEAR_OLD );
    }
    
    public final void fromAttributes( Attributes attributes, BuildType buildType ) {
        for ( SystemBuilderAdapter<?> builderAdapter : systemBuilderAdapter ) {
            builderAdapter.fromAttributes( attributes, buildType );
        }
    }
    
    public final void toAttributes( Attributes attributes ) {
        for ( SystemBuilderAdapter<?> builderAdapter : systemBuilderAdapter ) {
            builderAdapter.toAttributes( attributes );
        }
    }
    
    public final void toAttributes( Attributes attributes, SystemComponentKey... componentKeys ) {
        for ( SystemComponentKey componentKey : componentKeys ) {
            SystemBuilderAdapter<?> builderAdapter = systemBuilderAdapter.get( componentKey.index() );
            builderAdapter.toAttributes( attributes );
        }
    }

    public final int getScreenWidth() {
        return systemInterface.getScreenWidth();
    }
    
    public final int getScreenHeight() {
        return systemInterface.getScreenHeight();
    }
    
    public final void exit() {
        this.exit = true;
    }

    final void dispose() {
        for ( DataComponent component : dataComponents.values() ) {
            if ( component instanceof Disposable ) {
                ( (Disposable) component ).dispose( this );
            }
        }
        dataComponents.clear();
        
        for ( FFSystem system : componentSystems.values() ) {
            system.dispose( this );
        }
        componentSystems.clear();
    }

}
