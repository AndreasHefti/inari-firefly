package com.inari.firefly.sound;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFContext;
import com.inari.firefly.asset.event.AssetEvent;
import com.inari.firefly.asset.event.AssetEventListener;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.system.FFSystem;

public class SoundSystem implements FFSystem, ComponentBuilderFactory, AssetEventListener {
    
    private IEventDispatcher eventDispatcher;
    
    private final DynArray<Sound> sounds;

    SoundSystem() {
        sounds = new DynArray<Sound>();
    }
    
    @Override
    public void init( FFContext context ) {
        eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
        
        eventDispatcher.register( AssetEvent.class, this );
    }
    
    @Override
    public void dispose( FFContext context ) {
        eventDispatcher.unregister( AssetEvent.class, this );
        sounds.clear();
    }

    @Override
    public void onAssetEvent( AssetEvent event ) {
        if ( event.assetType != SoundAsset.class ) {
            return;
        }
        switch ( event.type ) {
            case ASSET_LOADED: {
                
                break;
            }
            case ASSET_DISPOSED: {
                sounds.remove( event.asset.indexedId() );
                break;
            }
            default: {}
        }
        
    }

    @Override
    public <C> ComponentBuilder<C> getComponentBuilder( Class<C> type ) {
        // TODO Auto-generated method stub
        return null;
    }

    

}
