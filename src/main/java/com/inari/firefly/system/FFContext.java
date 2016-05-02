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
import com.inari.commons.lang.aspect.AspectBitSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.ComponentId;
import com.inari.firefly.component.ContextComponent;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.entity.EntityComponent.EntityComponentTypeKey;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.entity.EntitySystem.EntityBuilder;
import com.inari.firefly.graphics.TextureAsset;
import com.inari.firefly.system.FFSystem.FFSystemTypeKey;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.ComponentSystem.BuildType;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.external.FFAudio;
import com.inari.firefly.system.external.FFGraphics;
import com.inari.firefly.system.external.FFInput;
import com.inari.firefly.system.external.FFTimer;
import com.inari.firefly.system.info.SystemInfoDisplay;

/** This is the main access point of the firefly-engine API. A FFContext is a singleton instance and created by the application
 *  initializer. You can access the FFContext in differently within different situations. Systems, Components and Controllers 
 *  get injected within a FFContext on creation time and usually have a proetected member of it.
 * 
 *  Within The FFContext you can:
 *  - Load/dispose a Systems within its SystemKey
 *  - Get/Delete a Sytem Components within the specified SystemComponentKey and the Component Id or name
 *  - Get/Set/Delete Context Components
 *  - Get a Component Builder of specifiec type to build a Component within
 *  - Get a Entity Component of specified type by Id or name
 *  - Get the Aspect of an specified Entity by Id or name
 *  - Activate / Deactivate / Delete a specified Entity that exists by Id or name
 *  - Register / Dispose EventListers for specifed Events
 *  - Notify Events of specified types to the EventDispatcher
 *  - Serialize / Load Components form Attributes
 *  - ...
 **/
public final class FFContext {
    
    public static final String DEFAULT_FONT = "FIREFLY_DEFAULT_FONT";

    public static interface Properties {
        public static final TypedKey<Integer> ENTITY_MAP_CAPACITY = TypedKey.create( "FF_ENTITY_MAP_CAPACITY", Integer.class );
        public static final TypedKey<Integer> ENTITY_COMPONENT_SET_CAPACITY = TypedKey.create( "FF_ENTITY_COMPONENT_SET_CAPACITY", Integer.class );
        public static final TypedKey<Integer> ENTITY_BEANS_CACHE_SIZE = TypedKey.create( "FF_ENTITY_BEANS_CACHE_SIZE", Integer.class );
    }
    
    private final Map<TypedKey<?>, Object> properties =  new LinkedHashMap<TypedKey<?>, Object>();
    
    private final DynArray<ContextComponent> contextComponents = new DynArray<ContextComponent>();
    private final DynArray<FFSystem> systems = new DynArray<FFSystem>();
    
    private final DynArray<SystemBuilderAdapter<?>> systemBuilderAdapter = new DynArray<SystemBuilderAdapter<?>>();
    
    private final IEventDispatcher eventDispatcher;
    private final FFGraphics graphics;
    private final FFAudio audio;
    private final FFTimer timer;
    private final FFInput input;
    private final SystemInfoDisplay systemInfoDisplay;
    
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
        systemInfoDisplay = new SystemInfoDisplayImpl( this );
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
    
    public final SystemInfoDisplay getSystemInfoDisplay() {
        return systemInfoDisplay;
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
                return;
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
    
    public final int getAssetInstanceId( String assetName ) {
        Asset asset = getSystemComponent( TextureAsset.TYPE_KEY, assetName );
        if ( asset == null ) {
            return -1;
        }
        
        return asset.getInstanceId();
    }
    
    @SuppressWarnings( "unchecked" )
    public final <C extends Component> C getComponent( ComponentId id ) {
        if ( id.typeKey.baseType() == SystemComponent.class ) {
            return (C) getSystemComponent( SystemComponentKey.class.cast( id.typeKey ), id.indexId );
        } else if ( id.typeKey.baseType() == EntityComponent.class ) {
            return (C) getEntityComponent( id.indexId, EntityComponentTypeKey.class.cast( id.typeKey ) );
        } else if ( id.typeKey.baseType() == ContextComponent.class ) {
            return (C) getContextComponent( id.indexId );
        }
        
        return null;
    }

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
    
    public final <C extends SystemComponent> C getSystemComponent( SystemComponentKey<C> key, String componentName ) {
        SystemBuilderAdapter<?> builderHelper = systemBuilderAdapter.get( key.index() );
        return key.<C>type().cast( builderHelper.getComponent( componentName ) );
    }
    
    public final <C extends SystemComponent> int getSystemComponentId( SystemComponentKey<C> key, String componentName ) {
        SystemBuilderAdapter<?> builderHelper = systemBuilderAdapter.get( key.index() );
        return key.<C>type().cast( builderHelper.getComponent( componentName ) ).index();
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
    
    public final <C extends SystemComponent> void deleteSystemComponent( SystemComponentKey<C> key, int componentIndex ) {
        SystemBuilderAdapter<?> builderHelper = systemBuilderAdapter.get( key.index() );
        builderHelper.deleteComponent( componentIndex );
    }
    
    public final <C extends SystemComponent> void deleteSystemComponent( SystemComponentKey<C> key, String componentName ) {
        SystemBuilderAdapter<?> builderHelper = systemBuilderAdapter.get( key.index() );
        builderHelper.deleteComponent( componentName );
    }

    @SuppressWarnings( "unchecked" )
    public final <C extends ContextComponent> C getContextComponent( int componentIndex ) {
        return (C) contextComponents.get( componentIndex );
    }
    
    @SuppressWarnings( "unchecked" )
    public final <C extends ContextComponent> C getContextComponent( String name ) {
        for ( ContextComponent contextComponent : contextComponents ) {
            if ( name.equals( contextComponent.getName() ) ) {
                return (C) contextComponent;
            }
        }
        
        return null;
    }
    
    public final <C extends ContextComponent> C getContextComponent( int componentIndex, Class<C> type ) {
        return type.cast(  contextComponents.get( componentIndex ) );
    }
    
    public final <T extends ContextComponent> void setContextComponent( T component ) {
        contextComponents.set( component.index(), component );
    }
    
    public final void disposeContextComponent( int componentIndex ) {
        ContextComponent component = contextComponents.remove( componentIndex );
        if ( component != null && component instanceof Disposable ) {
            ( (Disposable) component ).dispose( this );
        }
    }
    
    public final void disposeContextComponent( String componentName ) {
        ContextComponent contextComponent = getContextComponent( componentName );
        if ( contextComponent != null ) {
            disposeContextComponent( contextComponent.index() );
        }
    }
    
    public final ComponentBuilder getComponentBuilder( SystemComponentKey<?> key ) {
        int id = key.index();
        if ( !systemBuilderAdapter.contains( id ) ) {
            throw new FFInitException( "No component builder for key: " + key + " found. Maybe the appropriate System is not loaded?" );
        }
        
        return systemBuilderAdapter.get( id ).getComponentBuilder();
    }
    
    public final EntityBuilder getEntityBuilder() {
        return entitySystem.getEntityBuilder();
    }
    
    public final <T extends EntityComponent> T getEntityComponent( int entityId, EntityComponentTypeKey<T> typeKey ) {
        return entitySystem.getComponent( entityId, typeKey );
    }
    
    public final <T extends EntityComponent> T getEntityComponent( String entityName, EntityComponentTypeKey<T> typeKey ) {
        return entitySystem.getComponent( entityName, typeKey );
    }
    
    public final AspectBitSet getEntityAspect( int entityId ) {
        return entitySystem.getAspect( entityId );
    }
    
    public final void activateEntity( int entityId ) {
        entitySystem.activateEntity( entityId );
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
            builderAdapter.toAttributes( attributes, this );
        }
    }
    
    public final void toAttributes( Attributes attributes, SystemComponentKey<?>... componentKeys ) {
        for ( SystemComponentKey<?> componentKey : componentKeys ) {
            SystemBuilderAdapter<?> builderAdapter = systemBuilderAdapter.get( componentKey.index() );
            builderAdapter.toAttributes( attributes, this );
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

    public final void dispose() {
        for ( ContextComponent component : contextComponents ) {
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
