/*******************************************************************************
 * Copyright (c) 2015 - 2016, Andreas Hefti, inarisoft@yahoo.de 
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
package com.inari.firefly.audio;

import java.util.Iterator;

import com.inari.commons.StringUtils;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;
import com.inari.firefly.system.external.FFAudio;

public final class AudioSystem
    extends
        ComponentSystem<AudioSystem> {
    
    public static final FFSystemTypeKey<AudioSystem> SYSTEM_KEY = FFSystemTypeKey.create( AudioSystem.class );
    
    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        Sound.TYPE_KEY
    };

    private AssetSystem assetSystem;
    private ControllerSystem controllerSystem;
    private FFAudio audio;
    
    private final DynArray<Sound> sounds;

    AudioSystem() {
        super( SYSTEM_KEY );
        sounds = new DynArray<Sound>();
    }
    
    @Override
    public final void init( FFContext context ) {
        super.init( context );
        
        assetSystem = context.getSystem( AssetSystem. SYSTEM_KEY );
        controllerSystem = context.getSystem( ControllerSystem. SYSTEM_KEY );
        audio = context.getAudio();
        
        context.registerListener( AudioSystemEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        clear();
        
        context.disposeListener( AudioSystemEvent.class, this );
    }

    public final void clear() {
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
    
    public void deleteSound( String soundName ) {
        Sound sound = getSound( soundName );
        if ( sound != null ) {
            deleteSound( sound.getId() );
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
    
    public int getSoundId( String soundName ) {
        Sound sound = getSound( soundName );
        if ( sound == null ) {
            return -1;
        }
        return sound.getId();
    }

    final void onSoundEvent( AudioSystemEvent event ) {
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
                playSound( sound ); 
                break;
            }
            case STOP_PLAYING : {
                stopPlaying( sound ); 
                break;
            }
        }
    }
    
    private final void stopPlaying( Sound sound ) {
        if ( sound.streaming ) {
            audio.stopMusic( sound.getSoundId() );
        } else {
            audio.stopSound( sound.getSoundId(), sound.instanceId );
        }
        
        int controllerId = sound.getControllerId();
        if ( controllerId >= 0 ) {
            controllerSystem.removeControlledComponentId( controllerId, sound.soundId );
        }
    }

    private final void playSound( Sound sound ) {
        if ( sound.streaming ) {
            audio.playMusic( 
                sound.getSoundId(), 
                sound.isLooping(), 
                sound.getVolume(), 
                sound.getPan() 
            );
        } else {
            sound.instanceId = audio.playSound( 
                sound.getSoundId(), 
                sound.getChannel(), 
                sound.isLooping(), 
                sound.getVolume(), 
                sound.getPitch(), 
                sound.getPan() 
            );
        }
        
        int controllerId = sound.getControllerId();
        if ( controllerId >= 0 ) {
            controllerSystem.addControlledComponentId( controllerId, sound.soundId );
        }
    }

    public final SoundBuilder getSoundBuilder() {
        return new SoundBuilder();
    }
    
    @Override
    public final SystemComponentKey<?>[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
            new SoundBuilderAdapter( this )
        };
    }

    public final class SoundBuilder extends SystemComponentBuilder {

        @Override
        public final SystemComponentKey<Sound> systemComponentKey() {
            return Sound.TYPE_KEY;
        }

        @Override
        public int doBuild( int componentId, Class<?> subType, boolean activate ) {
            if ( componentId >= 0 && sounds.contains( componentId ) ) {
                throw new FFInitException( "Sound with id: " + componentId + " already exists: " + sounds.get( componentId ).getName() );
            }
            
            Sound result = new Sound( componentId );
            result.fromAttributes( attributes );
            
            SoundAsset asset = assetSystem.getAssetAs( result.getSoundAssetId(), SoundAsset.class );
            if ( asset == null ) {
                throw new ComponentCreationException( "The SoundAsset with id: " + result.getSoundId() + " does not exist" );
            }
            result.soundId = asset.getSoundId();
            result.streaming = asset.isStreaming();
            
            sounds.set( result.index(), result );
            postInit( result, context );
            
            return result.getId();
        }
    }
    
    private final class SoundBuilderAdapter extends SystemBuilderAdapter<Sound> {
        public SoundBuilderAdapter( AudioSystem system ) {
            super( system, new SoundBuilder() );
        }
        @Override
        public final SystemComponentKey<Sound> componentTypeKey() {
            return Sound.TYPE_KEY;
        }
        @Override
        public final Sound getComponent( int id ) {
            return sounds.get( id );
        }
        @Override
        public final Iterator<Sound> getAll() {
            return sounds.iterator();
        }
        @Override
        public final void deleteComponent( int id ) {
            deleteSound( id );
        }
        @Override
        public final void deleteComponent( String name ) {
            deleteSound( getSound( name ).getId() );
        }
        @Override
        public final Sound getComponent( String name ) {
            return getSound( name );
        }
    }

}
