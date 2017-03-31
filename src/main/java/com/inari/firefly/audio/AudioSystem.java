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
import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.StringUtils;
import com.inari.commons.lang.list.DynArray;
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
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        Sound.TYPE_KEY
    );

    private AssetSystem assetSystem;
    private ControllerSystem controllerSystem;
    private FFAudio audio;
    
    private final DynArray<Sound> sounds;

    AudioSystem() {
        super( SYSTEM_KEY );
        sounds = DynArray.create( Sound.class, 20, 10 );
    }
    
    @Override
    public final void init( FFContext context ) {
        super.init( context );
        
        assetSystem = context.getSystem( AssetSystem. SYSTEM_KEY );
        controllerSystem = context.getSystem( ControllerSystem. SYSTEM_KEY );
        audio = context.getAudio();
        
        context.registerListener( AudioSystemEvent.TYPE_KEY, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        clear();
        
        context.disposeListener( AudioSystemEvent.TYPE_KEY, this );
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
            deleteSound( sound.index() );
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
        return sound.index();
    }
    
    public final void stopPlaying( String soundName ) {
        stopPlaying( getSoundId( soundName ) );
    }

    public final void stopPlaying( int soundId ) {
        if ( !sounds.contains( soundId ) ) {
            return;
        }
        
        Sound sound = sounds.get( soundId );
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
    
    public final void playSound( String soundName ) {
        playSound( getSoundId( soundName ) );
    }

    public final void playSound( int soundId ) {
        if ( !sounds.contains( soundId ) ) {
            return;
        }
        
        Sound sound = sounds.get( soundId );
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

    public final SystemComponentBuilder getSoundBuilder() {
        return new SoundBuilder();
    }
    
    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            new SoundBuilderAdapter()
        );
    }

    private final class SoundBuilder extends SystemComponentBuilder {
        
        private SoundBuilder() {
            super( context );
        }

        @Override
        public final SystemComponentKey<Sound> systemComponentKey() {
            return Sound.TYPE_KEY;
        }

        @Override
        public int doBuild( int componentId, Class<?> subType, boolean activate ) {
            Sound result = createSystemComponent( componentId, subType, context );
            
            SoundAsset asset = assetSystem.getAssetAs( result.getSoundAssetId(), SoundAsset.class );
            if ( asset == null ) {
                throw new ComponentCreationException( "The SoundAsset with id: " + result.getSoundId() + " does not exist" );
            }
            result.soundId = asset.getSoundId();
            result.streaming = asset.isStreaming();
            
            sounds.set( result.index(), result );
            return result.index();
        }
    }
    
    private final class SoundBuilderAdapter extends SystemBuilderAdapter<Sound> {
        private SoundBuilderAdapter() {
            super( AudioSystem.this, Sound.TYPE_KEY );
        }
        @Override
        public final SystemComponentBuilder createComponentBuilder( Class<? extends Sound> soundType ) {
            return new SoundBuilder();
        }
        @Override
        public final Sound get( int id ) {
            return sounds.get( id );
        }
        @Override
        public final Iterator<Sound> getAll() {
            return sounds.iterator();
        }
        @Override
        public final void delete( int id ) {
            deleteSound( id );
        }
        @Override
        public final int getId( String name ) {
            return getSoundId( name );
        }
        @Override
        public final void activate( int id ) {
            throw new UnsupportedOperationException( componentTypeKey() + " is not activable" );
        }
        @Override
        public final void deactivate( int id ) {
            throw new UnsupportedOperationException( componentTypeKey() + " is not activable" );
        }
    }

}
