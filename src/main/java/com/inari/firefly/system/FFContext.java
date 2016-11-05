package com.inari.firefly.system;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;

import com.inari.commons.event.AspectedEvent;
import com.inari.commons.event.AspectedEventListener;
import com.inari.commons.event.Event;
import com.inari.commons.event.Event.EventTypeKey;
import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.event.PredicatedEvent;
import com.inari.commons.event.PredicatedEventListener;
import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.aspect.Aspects;
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
import com.inari.firefly.system.utils.Disposable;

/** This is the main access point of the firefly-engine API. A FFContext is a singleton instance and created by the application
 *  initializer. FFContext ususally is available in Systems, Components and Controllers. All this types get injected on 
 *  creation time and usually have a protected member of it.
 *  <p>
 *  
 * <pre>
 *  Within The FFContext you can:
 *  - Get lower system API interfaces like {@link IEventDispatcher, {@link FFGraphics}, {@link FFAudio}, {@link FFTimer}, {@link FFInput}
 *  - Load / Dispose a {@link FFSystem} within its {@link SystemKey}
 *  - Get / Delete a {@link SystemComponent} within the specified {@link ComponentId} or {@link SystemComponentKey} and the components instance id or name
 *  - Create / Get / Delete a {@link SystemComponent} or a {@link Entity}, {@link EntityComponent} or a {@link ContextComponent}
 *  - Get a {@link ComponentBuilder} of specifiec type to build a {@link Component} within
 *  - Get an {@link EntityComponent} of specified type by id or name
 *  - Get the {@link Aspect} of an specified {@link Entity} by Id or name
 *  - Activate / Deactivate / Delete a specified {@link Entity} that exists by id or name
 *  - Register / Dispose an event-listener for a specifed {@link Events}
 *  - Notify an {@link Event} of specified types to the internal event dispatcher
 *  - Serialize / Load one or many {@link Component} from {@link Attributes}
 *  - ...
 *  </pre>
 * 
 *  TODO: provide some example code that shows how to work with the FFContext
 **/
public final class FFContext {
    
    /** Defines the name of the default {@link Font} that is pre created and installed with the native firefly-engine */
    public static final String DEFAULT_FONT = "FIREFLY_DEFAULT_FONT";

    /** This defines configuration and initialization properties for the firefly-engine.
     *  For the moment mostly cache sizes and stuff.
     */
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

    /** Creates a FFContext instance with the needed lower level API interface implementations. */
    FFContext( 
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

    /** Use this to get the underling {@link IEventDispatcher} implementation. 
     * @return {@link IEventDispatcher} implementation
     */
    public final IEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }
    
    /** Use this to get the underling {@link FFGraphics} implementation 
     * @return underling {@link FFGraphics}
     */
    public final FFGraphics getGraphics() {
        return graphics;
    }
    
    /** Use this to get the underling {@link FFAudio} implementation 
     * @return underling {@link FFAudio} implementation
     */
    public final FFAudio getAudio() {
        return audio;
    }

    /** Use this to get the underling {@link FFTimer} implementation
     * @return underling {@link FFTimer} implementation
     */
    public final FFTimer getTimer() {
        return timer;
    }
    
    public final long getTime() {
        return timer.getTime();
    }
    
    public final long getTimeElapsed() {
        return timer.getTimeElapsed();
    }
    
    /** Use this to get the underling {@link FFInput} implementation 
     * @return underling {@link FFInput} implementation
     */
    public final FFInput getInput() {
        return input;
    }
    
    /** Use this to get the {@link SystemInfoDisplay} that gives the ability to add/remove {@link SystemInfo} displays
     *  and activate/deactivate the system info display. 
     * @return {@link SystemInfoDisplay} implementation
     */
    public final SystemInfoDisplay getSystemInfoDisplay() {
        return systemInfoDisplay;
    }

    /** Use this to get a {@link FFSystem} for a specified {@link FFSystemTypeKey}.<p>
     *  The key normally is provided within a static reference within the implementing {@link FFSystem} class.
     *  <p>
     *  <code>context.getSystem( EntitySystem.SYSTEM_KEY )</code>
     *  <p>
     *  If the {@link FFSystem} is not already loaded, the context tries to load the specified {@link FFSystem} and throws a {@link FFInitException}
     *  if an error occurs on loading process.
     *  @param key {@link FFSystemTypeKey} specifies the {@link FFSytem}
     *  @return {@link FFSystem} of type of key type
     *  @throws {@link FFInitExcpetion} if the FFSystem was not loaded and an error occurs on load
     */
    public final <T extends FFSystem> T getSystem( FFSystemTypeKey<T> key ) {
        if ( !systems.contains( key.index() ) ) {
            loadSystem( key, true );
        }
        return key.systemType.cast( systems.get( key.index() ) );
    }
    /** Use this to load a specified {@link FFSystem} into the context.
     *  @param key {@link FFSystemTypeKey} specifies the {@link FFSystem}
     *  @return {@link FFSystem} of type of key type
     *  @throws {@link FFInitExcpetion} if the {@link FFSystem} was not loaded and an error occurs on load
     */
    public final <T extends FFSystem> void loadSystem( FFSystemTypeKey<T> key ) {
        loadSystem( key, false );
    }
    
    /** Use this to load a specified {@link FFSystem} into the context.<p>
     *  Use force true to force the loading of the specified {@link FFSystem}. This means, even if the {@link FFSystem}
     *  is already loaded, the already loaded instance is disposed and a new instance is created.
     *  @param key {@link FFSystemTypeKey} specifies the {@link FFSystem}
     *  @param force forces to load of specified {@link FFSystem}
     *  @return {@link FFSystem} of type of key type
     *  @throws {@link FFInitExcpetion} if the {@link FFSystem} was not loaded and an error occurs on load
     */
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
    
    /** Use this to dispose a specified {@link FFSystem} that was loaded into the context.
     * @param key {@link FFSystemTypeKey} specifies the {@link FFSystem}
     */
    public final <T extends FFSystem> void disposeSystem( FFSystemTypeKey<T> key ) {
        @SuppressWarnings( "unchecked" )
        T system = (T) systems.remove( key.index() );
        if ( system != null ) {
            system.dispose( this );
        }
    }
    
    //---- SystemComponent adaption ----
    
    /** Use this to get a {@link Component} instance with the specified {@link ComponentId}.
     *  The {@link ComponentId} defines the type and the instance id of the component.
     *  The {@link Component} can be one of the following component base types:
     *  <pre>
     *  - {@link SystemComponent}
     *  - {@link EntityComponent}
     *  - {@link ContextComponent}
     *  </pre>
     *  
     * @param the {@link ComponentId}, defining the type and instance id of the {@link Component}
     * @return id the {@link Component} instance for specified {@link ComponentId} or null of no such {@link Component} exists within the context
     */
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

    /** Use this to get a {@link SystemComponent} for specified {@link SystemComponentKey} and component instance id.<p>
     *  The {@link SystemComponentKey} usually is provided with a static field within the Component's base class. For example:
     *  <p>
     *  <code>View baseView = context.getSystemComponent( View.TYPE_KEY, 0 )</code>
     *  
     * @param key {@link SystemComponentKey} that defines the type of the {@link SystemComponent}
     * @param componentId the instance id/index of the specified {@link SystemComponent}
     * @return the {@link SystemComponent} or null if no such component exists
     */
    public final <C extends SystemComponent> C getSystemComponent( SystemComponentKey<C> key, int componentId ) {
        SystemBuilderAdapter<?> builderHelper = systemBuilderAdapter.get( key.index() );
        return key.<C>type().cast( builderHelper.get( componentId ) );
    }

    public final <C extends SystemComponent, CS extends C> CS getSystemComponent( SystemComponentKey<C> key, int componentId, Class<CS> subType ) {
        @SuppressWarnings( "unchecked" )
        SystemBuilderAdapter<C> builderHelper = (SystemBuilderAdapter<C>) systemBuilderAdapter.get( key.index() );
        C component = builderHelper.get( componentId );
        if ( component == null ) {
            return null;
        }
        return subType.cast( component );
    }

    public final <C extends SystemComponent> C getSystemComponent( SystemComponentKey<C> key, String componentName ) {
        SystemBuilderAdapter<?> builderHelper = systemBuilderAdapter.get( key.index() );
        return key.<C>type().cast( builderHelper.get( builderHelper.getId( componentName ) ) );
    }
    
    public final <C extends SystemComponent> int getSystemComponentId( SystemComponentKey<C> key, String componentName ) {
        SystemBuilderAdapter<?> builderHelper = systemBuilderAdapter.get( key.index() );
        return builderHelper.getId( componentName );
    }
    
    @SuppressWarnings( "unchecked" )
    public final <C extends SystemComponent, CS extends C> CS getSystemComponent( SystemComponentKey<C> key, String componentName, Class<CS> subType ) {
        SystemBuilderAdapter<C> builderHelper = (SystemBuilderAdapter<C>) systemBuilderAdapter.get( key.index() );
        C component = builderHelper.get( builderHelper.getId( componentName ) );
        if ( component == null ) {
            return null;
        }
        return subType.cast( component );
    }
    
    public final <C extends SystemComponent, CS extends C> void activateSystemComponent( SystemComponentKey<C> key, int id ) {
        @SuppressWarnings( "unchecked" )
        SystemBuilderAdapter<C> builderHelper = (SystemBuilderAdapter<C>) systemBuilderAdapter.get( key.index() );
        builderHelper.activate( id );
    }
    
    public final <C extends SystemComponent, CS extends C> void deactivateSystemComponent( SystemComponentKey<C> key, int id ) {
        @SuppressWarnings( "unchecked" )
        SystemBuilderAdapter<C> builderHelper = (SystemBuilderAdapter<C>) systemBuilderAdapter.get( key.index() );
        builderHelper.deactivate( id );
    }
    
    public final <C extends SystemComponent> void deleteSystemComponent( SystemComponentKey<C> key, int componentIndex ) {
        SystemBuilderAdapter<?> builderHelper = systemBuilderAdapter.get( key.index() );
        builderHelper.delete( componentIndex );
    }
    
    public final <C extends SystemComponent> void deleteSystemComponent( SystemComponentKey<C> key, String componentName ) {
        SystemBuilderAdapter<?> builderHelper = systemBuilderAdapter.get( key.index() );
        builderHelper.delete( builderHelper.getId( componentName ) );
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
    
    public final Aspects getEntityComponentAspects( int entityId ) {
        return entitySystem.getEntityComponentAspects( entityId );
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
    
    public final int getAssetInstanceId( String assetName ) {
        Asset asset = getSystemComponent( TextureAsset.TYPE_KEY, assetName );
        if ( asset == null ) {
            return -1;
        }
        
        return asset.getInstanceId();
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
    
    public final <L> void registerListener( EventTypeKey eventType, L listener ) {
        eventDispatcher.register( eventType, listener );
    }
    
    public final <L> void disposeListener( EventTypeKey eventType, L listener ) {
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
