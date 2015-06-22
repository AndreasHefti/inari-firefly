package com.inari.firefly.sound;

import com.inari.commons.StringUtils;
import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFContext;
import com.inari.firefly.asset.event.AssetEvent;
import com.inari.firefly.asset.event.AssetEventListener;
import com.inari.firefly.system.FFSystem;

public final class SoundSystem implements FFSystem, AssetEventListener {
    
    private IEventDispatcher eventDispatcher;
    private final DynArray<Sound> sounds;

    SoundSystem() {
        sounds = new DynArray<Sound>();
    }
    
    @Override
    public final void init( FFContext context ) {
        eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
        eventDispatcher.register( AssetEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        eventDispatcher.unregister( AssetEvent.class, this );
        sounds.clear();
    }

    @Override
    public final void onAssetEvent( AssetEvent event ) {
        if ( event.assetType != SoundAsset.class ) {
            return;
        }
        switch ( event.type ) {
            case ASSET_LOADED: {
                Sound sound = new Sound( event.asset.indexedId() );
                sound.setName( event.asset.getName() );
                sounds.add( sound );
                break;
            }
            case ASSET_DISPOSED: {
                sounds.remove( event.asset.indexedId() );
                break;
            }
            default: {}
        }
        
    }
    
    public final Sound getSound( int soundId ) {
        return sounds.get( soundId );
    }
    
    public final Sound getSound( String name ) {
        if ( StringUtils.isBlank( name ) ) {
            return null;
        }
        
        for ( Sound sound : sounds ) {
            if ( name.equals( sound.getName() ) ) {
                return sound;
            }
        }
        
        return null;
    }

}
