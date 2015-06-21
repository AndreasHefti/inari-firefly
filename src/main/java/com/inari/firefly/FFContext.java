package com.inari.firefly;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.TypedKey;
import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.EntityControllerSystem;
import com.inari.firefly.entity.IEntitySystem;
import com.inari.firefly.movement.MovementSystem;
import com.inari.firefly.sprite.SpriteRendererSystem;
import com.inari.firefly.sprite.tile.TileGridSystem;
import com.inari.firefly.state.StateSystem;
import com.inari.firefly.system.ILowerSystemFacade;
import com.inari.firefly.system.ViewSystem;

public interface FFContext {
    
    
    /** The System defines global system keys for components that are essential to the system and must be initialized.
     *  All this components can be get from the IFFContext system context if the context was once properly initialized.
     */
    public static interface System {

        /** The Key for the event dispatcher service that provides the system wide event handling.
         *  Use the event dispatcher service to register listeners or fire certain events.
         *  For more information please have a look at the documentation of {@link IEventDispatcher}.
         */
        public static final TypedKey<IEventDispatcher> EVENT_DISPATCHER = TypedKey.create( "FF_EVENT_DISPATCHER", IEventDispatcher.class );
        
        public static final TypedKey<ViewSystem> VIEW_SYSTEM = TypedKey.create( "FF_VIEW_SYSTEM", ViewSystem.class );

        /** The Key for the AssetSystem that provides the whole asset handling, creation, loading, disposing, delete... in the system.
         *  Use this to create new assets, to load created assets, dispose loaded assets or delete them and free the memory.
         *  For more information please have a look at the documentation of {@link AssetSystem}.
         */
        public static final TypedKey<AssetSystem> ASSET_SYSTEM = TypedKey.create( "FF_ASSET_SYSTEM", AssetSystem.class );
        
        public static final TypedKey<IEntitySystem> ENTITY_SYSTEM = TypedKey.create( "FF_ENTITY_SYSTEM", IEntitySystem.class );
        
        public static final TypedKey<EntityControllerSystem> ENTITY_CONTROLLER_SYSTEM = TypedKey.create( "FF_NTITY_CONTROLLER_SYSTEM", EntityControllerSystem.class );
        
        public static final TypedKey<TileGridSystem> TILE_GRID_SYSTEM = TypedKey.create( "FF_TILE_GRID_SYSTEM", TileGridSystem.class ); 
        
        public static final TypedKey<SpriteRendererSystem> SPRITE_RENDERER_SYSTEM = TypedKey.create( "FF_SPRITE_RENDERER_SYSTEM", SpriteRendererSystem.class ); 
        
        public static final TypedKey<ILowerSystemFacade> LOWER_SYSTEM_FACADE = TypedKey.create( "FF_LOWER_SYSTEM_FACADE", ILowerSystemFacade.class );
        
        public static final TypedKey<StateSystem> STATE_SYSTEM = TypedKey.create( "FF_STATE_SYSTEM", StateSystem.class );
        
        public static final TypedKey<AnimationSystem> ANIMATION_SYSTEM = TypedKey.create( "FF_ANIMATION_SYSTEM", AnimationSystem.class );
        
        public static final TypedKey<MovementSystem> MOVEMENT_SYSTEM = TypedKey.create( "FF_MovementSystem", MovementSystem.class );
        
        
    }
    
    

    public <T> T get( TypedKey<T> key );
    
    public void dispose();

}
