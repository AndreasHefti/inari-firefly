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

import java.util.HashSet;
import java.util.Set;

import com.inari.commons.StringUtils;
import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.asset.AssetTypeKey;
import com.inari.firefly.component.ComponentBuilderHelper;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.sound.event.SoundEventListener;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.ILowerSystemFacade;

public final class SoundSystem 
    implements 
        FFContextInitiable, 
        ComponentSystem,
        ComponentBuilderFactory,
        SoundEventListener {
    
    public static final TypedKey<SoundSystem> CONTEXT_KEY = TypedKey.create( "FF_SOUND_SYSTEM", SoundSystem.class );
    
    private FFContext context;
    private AssetSystem assetSystem;
    private ILowerSystemFacade lowerSystemFacade;
    
    private final DynArray<Sound> sounds;

    SoundSystem() {
        sounds = new DynArray<Sound>();
    }
    
    @Override
    public final void init( FFContext context ) {
        this.context = context;
        assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        lowerSystemFacade = context.getComponent( FFContext.LOWER_SYSTEM_FACADE );
        
        context.getComponent( FFContext.EVENT_DISPATCHER ).register( SoundEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        clearSounds();
        
        context.getComponent( FFContext.EVENT_DISPATCHER ).unregister( SoundEvent.class, this );
    }

    private void clearSounds() {
        for ( Sound sound : sounds ) {
            sound.dispose();
        }
        sounds.clear();
    }

    public final void deleteSound( int soundId ) {
        Sound sound = sounds.remove( soundId );
        if ( sound == null ) {
            return;
        }
        
        sound.dispose();
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

    @Override
    public final void onSoundEvent( SoundEvent event ) {
        Sound sound;
        if ( event.name!= null ) {
            sound = getSound( event.name );
        } else {
            sound = getSound( event.soundId );
        }
        
        if ( sound == null ) {
            return;
        }
        
        switch ( event.eventType ) {
            case PLAY_SOUND : {
                if ( sound.streaming ) {
                    lowerSystemFacade.playMusic( sound.getAssetId(), sound.isLooping(), sound.getVolume(), sound.getPan() );
                } else {
                    sound.instanceId = lowerSystemFacade.playSound( sound.getAssetId(), sound.isLooping(), sound.getVolume(), sound.getPitch(), sound.getPan() );
                } 
                break;
            }
            case STOP_PLAYING : {
                if ( sound.streaming ) {
                    lowerSystemFacade.stopMusic( sound.getAssetId() );
                } else {
                    lowerSystemFacade.stopSound( sound.getAssetId(), sound.instanceId );
                } 
                break;
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public final <C> ComponentBuilder<C> getComponentBuilder( Class<C> type ) {
        if ( Sound.class.isAssignableFrom( type ) ) {
            return (ComponentBuilder<C>) new SoundBuilder( this );
        }
        
        throw new IllegalArgumentException( "Unsupported Component type for SoundSystem Builder. Type: " + type );
    }
    
    public final SoundBuilder getSoundBuilder() {
        return new SoundBuilder( this );
    }

    private static final Set<Class<?>> SUPPORTED_COMPONENT_TYPES = new HashSet<Class<?>>();
    @Override
    public final Set<Class<?>> supportedComponentTypes() {
        if ( SUPPORTED_COMPONENT_TYPES.isEmpty() ) {
            SUPPORTED_COMPONENT_TYPES.add( Sound.class );
        }
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final void fromAttributes( Attributes attributes ) {
        fromAttributes( attributes, BuildType.CLEAR_OLD );
    }

    @Override
    public final void fromAttributes( Attributes attributes, BuildType buildType ) {
        if ( buildType == BuildType.CLEAR_OLD ) {
            clearSounds();
        }

        new ComponentBuilderHelper<Sound>() {
            @Override
            public Sound get( int id ) {
                return sounds.get( id );
            }
            @Override
            public void delete( int id ) {
                deleteSound( id );
            }
        }.buildComponents( Sound.class, buildType, getSoundBuilder(), attributes );
    }

    @Override
    public final void toAttributes( Attributes attributes ) {
        ComponentBuilderHelper.toAttributes( attributes, Sound.class, sounds );
    }
    
    public final class SoundBuilder extends BaseComponentBuilder<Sound> {

        protected SoundBuilder( ComponentBuilderFactory componentFactory ) {
            super( componentFactory );
        }

        @Override
        public Sound build( int componentId ) {
            Sound result = getInstance( context, componentId );
            result.fromAttributes( attributes );
            
            SoundAsset asset = assetSystem.getAsset( new AssetTypeKey( result.getAssetId(), SoundAsset.class ), SoundAsset.class );
            if ( asset == null ) {
                throw new ComponentCreationException( "The SoundAsset with id: " + result.getAssetId() + " does not exist" );
            }
            result.streaming = asset.isStreaming();
            
            sounds.set( result.index(), result );
            postInit( result, context );
            
            return result;
        }
    }

}
