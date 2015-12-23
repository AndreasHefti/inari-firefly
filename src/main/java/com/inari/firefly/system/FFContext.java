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
import com.inari.firefly.FFInitException;
import com.inari.firefly.component.ContextComponent;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.entity.EntityComponent.EntityComponentTypeKey;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.system.FFSystem.FFSystemTypeKey;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.ComponentSystem.BuildType;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.external.FFAudio;
import com.inari.firefly.system.external.FFGraphics;
import com.inari.firefly.system.external.FFTimer;
import com.inari.firefly.system.external.FFInput;

public final class FFContext {

    public static interface Properties {
        public static final TypedKey<Integer> ENTITY_MAP_CAPACITY = TypedKey.create( "FF_ENTITY_MAP_CAPACITY", Integer.class );
        public static final TypedKey<Integer> ENTITY_COMPONENT_SET_CAPACITY = TypedKey.create( "FF_ENTITY_COMPONENT_SET_CAPACITY", Integer.class );
        public static final TypedKey<Integer> ENTITY_BEANS_CACHE_SIZE = TypedKey.create( "FF_ENTITY_BEANS_CACHE_SIZE", Integer.class );
    }
    
    private final Map<TypedKey<?>, Object> properties =  new LinkedHashMap<TypedKey<?>, Object>();
    
    private final Map<TypedKey<? extends ContextComponent>, ContextComponent> contextComponents = new LinkedHashMap<TypedKey<? extends ContextComponent>, ContextComponent>();
    private final DynArray<FFSystem> systems = new DynArray<FFSystem>();
    
    private final DynArray<SystemBuilderAdapter<?>> systemBuilderAdapter = new DynArray<SystemBuilderAdapter<?>>();
    
    private final IEventDispatcher eventDispatcher;
    private final FFGraphics graphics;
    private final FFAudio audio;
    private final FFTimer timer;
    private final FFInput input;
    
    private EntitySystem entitySystem;
    boolean exit = false;

    public FFContext( 
        IEventDispatcher eventDispatcher, 
        FFGraphics graphics,
        FFAudio audio,
        FFTimer timer,
        FFInput input 
    ) {
        this.eventDispatcher = eventDispatcher;
        this.graphics = graphics;
        graphics.init( this );
        this.audio = audio;
        audio.init( this );
        this.timer = timer;
        this.input = input;
    }

    public final IEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public final FFGraphics getGraphics() {
        return graphics;
    }
    
    public final FFAudio getAudio() {
        return audio;
    }

    public final FFTimer getTimer() {
        return timer;
    }

    public final FFInput getInput() {
        return input;
    }

    public final <T extends FFSystem> T getSystem( FFSystemTypeKey<T> key ) {
        if ( !systems.contains( key.index() ) ) {
            loadSystem( key, true );
        }
        return key.systemType.cast( systems.get( key.index() ) );
    }
    
    public final <T extends FFSystem> void loadSystem( FFSystemTypeKey<T> key ) {
        loadSystem( key, false );
    }
    
    public final <T extends FFSystem> void loadSystem( FFSystemTypeKey<T> key, boolean force ) {
        if ( systems.contains( key.index() ) ) {
            if ( force ) {
                FFSystem oldSystem = systems.remove( key.index() );
                oldSystem.dispose( this );
            } else {
                throw new FFInitException( "The System for key: " + key + " already exists" );
            }
        }
        
        try {
            Constructor<T> constructor = key.systemType.getDeclaredConstructor();
            boolean accessible = constructor.isAccessible();
            constructor.setAccessible( true );
            T componentSystem = constructor.newInstance();
            systems.set( key.index(), componentSystem );
            componentSystem.init( this );
            constructor.setAccessible( accessible );
            
            if ( componentSystem instanceof ComponentSystem ) {
                initComponentSystem( (ComponentSystem<?>) componentSystem );
            }
            
            if ( key.systemType == EntitySystem.class ) {
                entitySystem = (EntitySystem) componentSystem;
            }
        } catch ( Exception e ) {
            throw new FFInitException( "Failed to load system: " + key, e );
        }
    }
    
    public final <T extends FFSystem> void disposeSystem( FFSystemTypeKey<T> key ) {
        @SuppressWarnings( "unchecked" )
        T system = (T) systems.remove( key.index() );
        if ( system != null ) {
            system.dispose( this );
        }
    }
    
    //---- SystemComponent adaption ----


    public final <C extends SystemComponent> C getSystemComponent( SystemComponentKey<C> key, int componentId ) {
        SystemBuilderAdapter<?> builderHelper = systemBuilderAdapter.get( key.index() );
        return key.<C>type().cast( builderHelper.getComponent( componentId ) );
    }
    
    @SuppressWarnings( "unchecked" )
    public final <C extends SystemComponent, CS extends C> CS getSystemComponent( SystemComponentKey<C> key, int componentId, Class<CS> subType ) {
        SystemBuilderAdapter<C> builderHelper = (SystemBuilderAdapter<C>) systemBuilderAdapter.get( key.index() );
        C component = builderHelper.getComponent( componentId );
        if ( component == null ) {
            return null;
        }
        return subType.cast( component );
    }
    
    public <C extends SystemComponent> C getSystemComponent( SystemComponentKey<C> key, String componentName ) {
        SystemBuilderAdapter<?> builderHelper = systemBuilderAdapter.get( key.index() );
        return key.<C>type().cast( builderHelper.getComponent( componentName ) );
    }
    
    public <C extends SystemComponent> int getSystemComponentId( SystemComponentKey<C> key, String componentName ) {
        SystemBuilderAdapter<?> builderHelper = systemBuilderAdapter.get( key.index() );
        return key.<C>type().cast( builderHelper.getComponent( componentName ) ).getId();
    }
    
    @SuppressWarnings( "unchecked" )
    public final <C extends SystemComponent, CS extends C> CS getSystemComponent( SystemComponentKey<C> key, String componentName, Class<CS> subType ) {
        SystemBuilderAdapter<C> builderHelper = (SystemBuilderAdapter<C>) systemBuilderAdapter.get( key.index() );
        C component = builderHelper.getComponent( componentName );
        if ( component == null ) {
            return null;
        }
        return subType.cast( component );
    }
    
    public final <C extends SystemComponent> void deleteSystemComponent( SystemComponentKey<C> key, int componentId ) {
        SystemBuilderAdapter<?> builderHelper = systemBuilderAdapter.get( key.index() );
        builderHelper.deleteComponent( componentId );
    }
    
    
    
    public final <T extends ContextComponent> T getContextComponent( TypedKey<T> componentKey ) {
        return componentKey.cast(contextComponents.get( componentKey ) );
    }
    
    public final <T extends ContextComponent> void setContextComponent( T component ) {
        contextComponents.put( component.contextKey(), component );
    }
    
    public final void disposeContextComponent( TypedKey<? extends ContextComponent> componentKey ) {
        ContextComponent component = contextComponents.remove( componentKey );
        if ( component != null && component instanceof Disposable ) {
            ( (Disposable) component ).dispose( this );
        }
    }
    
    public final ComponentBuilder getComponentBuilder( SystemComponentKey<?> key ) {
        int id = key.index();
        if ( !systemBuilderAdapter.contains( id ) ) {
            return null;
        }
        
        return systemBuilderAdapter.get( id ).getComponentBuilder();
    }
    
    public final <T extends EntityComponent> T getEntityComponent( int entityId, EntityComponentTypeKey<T> typeKey ) {
        return entitySystem.getComponent( entityId, typeKey );
    }
    
    public final void deactivateEntity( int entityId ) {
        entitySystem.deactivateEntity( entityId );
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
    
    public final void toAttributes( Attributes attributes, SystemComponentKey<?>... componentKeys ) {
        for ( SystemComponentKey<?> componentKey : componentKeys ) {
            SystemBuilderAdapter<?> builderAdapter = systemBuilderAdapter.get( componentKey.index() );
            builderAdapter.toAttributes( attributes );
        }
    }

    public final int getScreenWidth() {
        return graphics.getScreenWidth();
    }
    
    public final int getScreenHeight() {
        return graphics.getScreenHeight();
    }
    
    public final void exit() {
        this.exit = true;
    }

    final void dispose() {
        for ( ContextComponent component : contextComponents.values() ) {
            if ( component instanceof Disposable ) {
                ( (Disposable) component ).dispose( this );
            }
        }
        contextComponents.clear();
        
        for ( FFSystem system : systems ) {
            system.dispose( this );
        }
        systems.clear();
    }
    
    private final void initComponentSystem( ComponentSystem<?> system ) {
        SystemBuilderAdapter<?>[] supportedBuilderAdapter = system.getSupportedBuilderAdapter();
        if ( supportedBuilderAdapter != null ) {
            for ( int i = 0; i < supportedBuilderAdapter.length; i++ ) {
                systemBuilderAdapter.set( supportedBuilderAdapter[ i ].componentTypeKey().index(), supportedBuilderAdapter[ i ] );
            }
        }
    }

}
