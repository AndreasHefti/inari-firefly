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

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.control.Controller;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentMap;
import com.inari.firefly.system.component.SystemComponentMap.BuilderListener;

public final class AudioSystem
    extends
        ComponentSystem<AudioSystem> {
    
    public static final FFSystemTypeKey<AudioSystem> SYSTEM_KEY = FFSystemTypeKey.create( AudioSystem.class );
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        Sound.TYPE_KEY
    );
    
    final SystemComponentMap<Sound> sounds;

    AudioSystem() {
        super( SYSTEM_KEY );
        sounds = new SystemComponentMap<>( 
            this, Sound.TYPE_KEY, 
            new BuilderListener<Sound>() {
                public final void notifyBuild( Sound component ) { build( component ); }
                public final void notifyActivation( int id ) { playSound( id ); }
                public final void notifyDeactivation( int id ) { stopPlaying( id ); }
                public final void notifyDeletion( Sound component ) {}
            }
        );
    }
    
    @Override
    public final void init( FFContext context ) {
        super.init( context );
        
        context.registerListener( AudioSystemEvent.TYPE_KEY, this );
    }
    
    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            sounds.getBuilderAdapter()
        );
    }

    void stopPlaying( int soundId ) {
        if ( !sounds.map.contains( soundId ) ) {
            return;
        }
        
        Sound sound = sounds.get( soundId );
        if ( sound.streaming ) {
            context
                .getAudio()
                .stopMusic( sound.getSoundId() );
        } else {
            context
                .getAudio()
                .stopSound( sound.getSoundId(), sound.instanceId );
        }
        
        int controllerId = sound.getControllerId();
        if ( controllerId >= 0 ) {
            context
                .getSystemComponent( Controller.TYPE_KEY, controllerId )
                .removeComponentId( sound.soundId );
        }
    }

    void playSound( int soundId ) {
        if ( !sounds.map.contains( soundId ) ) {
            return;
        }
        
        Sound sound = sounds.get( soundId );
        if ( sound.streaming ) {
            context.getAudio()
                .playMusic( 
                    sound.getSoundId(), 
                    sound.isLooping(), 
                    sound.getVolume(), 
                    sound.getPan() 
                );
        } else {
            sound.instanceId = context.getAudio()
                .playSound( 
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
            context
                .getSystemComponent( Controller.TYPE_KEY, controllerId )
                .addComponentId( sound.soundId );
        }
    }
    
    public final void dispose( FFContext context ) {
        clearSystem();
        
        context.disposeListener( AudioSystemEvent.TYPE_KEY, this );
    }

    public final void clearSystem() {
        sounds.clear();
    }
    
    private void build( Sound sound ) {
        final SoundAsset asset = context.getSystemComponent( Asset.TYPE_KEY, sound.getSoundAssetId(), SoundAsset.class );
        
        if ( asset == null ) {
            throw new ComponentCreationException( "The SoundAsset with id: " + sound.getSoundId() + " does not exist" );
        }
        sound.soundId = asset.getSoundId();
        sound.streaming = asset.isStreaming();
    }

}
