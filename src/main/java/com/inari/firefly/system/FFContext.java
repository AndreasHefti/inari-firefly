package com.inari.firefly.system;

import java.util.Map;
import java.util.Set;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.TypedKey;
import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.ComponentSystem.BuildType;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.entity.EntityPrefabSystem;
import com.inari.firefly.entity.EntityProvider;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.movement.MovementSystem;
import com.inari.firefly.renderer.sprite.SpriteViewRenderer;
import com.inari.firefly.renderer.sprite.SpriteViewSystem;
import com.inari.firefly.renderer.sprite.tile.TileGridRenderer;
import com.inari.firefly.renderer.sprite.tile.TileGridSystem;
import com.inari.firefly.sound.SoundSystem;
import com.inari.firefly.state.StateSystem;
import com.inari.firefly.system.view.ViewSystem;

public interface FFContext {
    
    /** The Kkey for the event dispatcher service that provides the system wide event handling.
     *  Use the event dispatcher service to register listeners or fire certain events.
     *  For more information please have a look at the documentation of {@link IEventDispatcher}.
     */
    public static final TypedKey<IEventDispatcher> EVENT_DISPATCHER = TypedKey.create( "FF_EVENT_DISPATCHER", IEventDispatcher.class );
    
    /** The key for the interface to lower system that must be implements for a specified context like lwjgl, libGDX, Processing...
     *  The class type of an specified implementation of the LOWER_SYSTEM_FACADE must be defined by initializing of the FFContext
     */
    public static final TypedKey<ILowerSystemFacade> LOWER_SYSTEM_FACADE = TypedKey.create( "FF_LOWER_SYSTEM_FACADE", ILowerSystemFacade.class );

    public static final TypedKey<EntityProvider> ENTITY_PROVIDER = TypedKey.create( "FF_ENTITY_PROVIDER", EntityProvider.class );

    public static interface Properties {
        public static final TypedKey<Integer> ENTITY_MAP_CAPACITY = TypedKey.create( "FF_ENTITY_MAP_CAPACITY", Integer.class );
        public static final TypedKey<Integer> ENTITY_COMPONENT_SET_CAPACITY = TypedKey.create( "FF_ENTITY_COMPONENT_SET_CAPACITY", Integer.class );
        public static final TypedKey<Integer> ENTITY_BEANS_CACHE_SIZE = TypedKey.create( "FF_ENTITY_BEANS_CACHE_SIZE", Integer.class );
    }

    /** The System defines global system keys for components that are essential to the system and must be initialized.
     *  All this components can be getComponent from the IFFContext system context if the context was once properly initialized.
     */
    public static interface Systems {

        public static final TypedKey<ViewSystem> VIEW_SYSTEM = TypedKey.create( "FF_VIEW_SYSTEM", ViewSystem.class );

        /** The K^key for the AssetSystem that provides the whole asset handling, creation, loading, disposing, delete... in the system.
         *  Use this to create new assets, to load created assets, dispose loaded assets or delete them and free the memory.
         *  For more information please have a look at the documentation of {@link AssetSystem}.
         */
        public static final TypedKey<AssetSystem> ASSET_SYSTEM = TypedKey.create( "FF_ASSET_SYSTEM", AssetSystem.class );
        
        public static final TypedKey<EntitySystem> ENTITY_SYSTEM = TypedKey.create( "FF_ENTITY_SYSTEM", EntitySystem.class );
        
        public static final TypedKey<EntityPrefabSystem> ENTITY_PREFAB_SYSTEM = TypedKey.create( "ENTITY_PREFAB_SYSTEM", EntityPrefabSystem.class );
        
        public static final TypedKey<ControllerSystem> ENTITY_CONTROLLER_SYSTEM = TypedKey.create( "FF_NTITY_CONTROLLER_SYSTEM", ControllerSystem.class );
        
        public static final TypedKey<TileGridSystem> TILE_GRID_SYSTEM = TypedKey.create( "FF_TILE_GRID_SYSTEM", TileGridSystem.class ); 
        
        public static final TypedKey<SpriteViewSystem> SPRITE_VIEW_SYSTEM = TypedKey.create( "FF_SPRITE_VIEW_SYSTEM", SpriteViewSystem.class ); 

        public static final TypedKey<StateSystem> STATE_SYSTEM = TypedKey.create( "FF_STATE_SYSTEM", StateSystem.class );
        
        public static final TypedKey<AnimationSystem> ANIMATION_SYSTEM = TypedKey.create( "FF_ANIMATION_SYSTEM", AnimationSystem.class );
        
        public static final TypedKey<MovementSystem> MOVEMENT_SYSTEM = TypedKey.create( "FF_MOVEMENT_SYSTEM", MovementSystem.class );
        
        public static final TypedKey<SoundSystem> SOUND_SYSTEM = TypedKey.create( "FF_SOUND_SYSTEM", SoundSystem.class );
        
    }
    
    public static interface Renderer {
        
        public static final TypedKey<SpriteViewRenderer> SPRITE_VIEW_RENDERER = TypedKey.create( "FF_SPRITE_VIEW_RENDERER", SpriteViewRenderer.class );
        
        public static final TypedKey<TileGridRenderer> TILE_GRID_RENDERER = TypedKey.create( "FF_TILE_GRID_RENDERER", TileGridRenderer.class );
        
    }


    <T> T getComponent( TypedKey<T> key );

    <T> T getProperty( TypedKey<T> key );
    
    void fromAttributes( Attributes attributes, BuildType buildType );
    
    void toAttributes( Attributes attributes );
    
    Map<TypedKey<? extends ComponentSystem>, Set<Class<?>>> getComponentTypes();

}
