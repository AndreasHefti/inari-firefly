/*******************************************************************************
 * Copyright (c) 2015, Andreas Hefti, inarisoft@yahoo.de 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/ 
package com.inari.firefly.sound;

import com.inari.commons.StringUtils;
import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.asset.event.AssetEvent;
import com.inari.firefly.asset.event.AssetEventListener;
import com.inari.firefly.system.FFContextInitiable;

public final class SoundSystem implements FFContextInitiable, AssetEventListener {
    
    private IEventDispatcher eventDispatcher;
    private final DynArray<Sound> sounds;

    SoundSystem() {
        sounds = new DynArray<Sound>();
    }
    
    @Override
    public final void init( FFContext context ) {
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        eventDispatcher.register( AssetEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        eventDispatcher.unregister( AssetEvent.class, this );
        for ( Sound sound : sounds ) {
            sound.dispose();
        }
        sounds.clear();
    }

    @Override
    public final void onAssetEvent( AssetEvent event ) {
        if ( event.assetType != SoundAsset.class ) {
            return;
        }
        switch ( event.eventType ) {
            case ASSET_LOADED: {
                Sound sound = new Sound( 
                    event.asset.index(), 
                    ( (SoundAsset) event.asset ).isStreaming() 
                );
                sound.setName( event.asset.getName() );
                sounds.add( sound );
                break;
            }
            case ASSET_DISPOSED: {
                sounds.remove( event.asset.index() );
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
