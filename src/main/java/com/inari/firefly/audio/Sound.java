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
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.control.Controller;
import com.inari.firefly.system.component.SystemComponent;

public final class Sound extends SystemComponent {
    
    public static final SystemComponentKey<Sound> TYPE_KEY = SystemComponentKey.create( Sound.class );
    
    public static final AttributeKey<String> SOUND_ASSET_NAME = AttributeKey.createString( "soundAssetName", Sound.class );
    public static final AttributeKey<Integer> SOUND_ASSET_ID = AttributeKey.createInt( "soundAssetId", Sound.class );
    public static final AttributeKey<Boolean> LOOPING = AttributeKey.createBoolean( "looping", Sound.class );
    public static final AttributeKey<Float> VOLUME = AttributeKey.createFloat( "volume", Sound.class );
    public static final AttributeKey<Float> PITCH = AttributeKey.createFloat( "pitch", Sound.class );
    public static final AttributeKey<Float> PAN = AttributeKey.createFloat( "pan", Sound.class );
    public static final AttributeKey<Integer> CHANNEL = AttributeKey.createInt( "channel", Sound.class );
    public static final AttributeKey<String> CONTROLLER_NAME = AttributeKey.createString( "controllerName", Sound.class );
    public static final AttributeKey<Integer> CONTROLLER_ID = AttributeKey.createInt( "controllerId", Sound.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet( 
        SOUND_ASSET_ID,
        LOOPING,
        VOLUME,
        PITCH,
        PAN,
        CHANNEL,
        CONTROLLER_ID
    );
    
    private int soundAssetId;
    private boolean looping;
    private float volume;
    private float pitch;
    private float pan;
    private int controllerId;
    private int channel;
    
    int soundId;
    boolean streaming;
    long instanceId;
    
    Sound( int id ) {
        super( id );
        soundAssetId = -1;
        looping = false;
        volume = 1;
        pitch = 1;
        pan = 0;
        controllerId = -1;
        streaming = false;
        instanceId = -1;
        channel = 0;
        soundId = -1;
    }
    
    public final int getSoundId() {
        return soundId;
    }

    public final int getSoundAssetId() {
        return soundAssetId;
    }

    final void setSoundAssetId( int soundAssetId ) {
        this.soundAssetId = soundAssetId;
    }

    public final boolean isStreaming() {
        return streaming;
    }
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

    public final boolean isLooping() {
        return looping;
    }

    final void setLooping( boolean looping ) {
        this.looping = looping;
    }

    public final float getVolume() {
        return volume;
    }

    public final void setVolume( float volume ) {
        this.volume = volume;
    }

    public final float getPitch() {
        return pitch;
    }

    public final void setPitch( float pitch ) {
        this.pitch = pitch;
    }

    public final float getPan() {
        return pan;
    }

    public final void setPan( float pan ) {
        this.pan = pan;
    }

    public final int getControllerId() {
        return controllerId;
    }

    public final void setControllerId( int controllerId ) {
        this.controllerId = controllerId;
    }

    public final int getChannel() {
        return channel;
    }

    public final void setChannel( int channel ) {
        this.channel = channel;
    }

    public long getInstanceId() {
        return instanceId;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return JavaUtils.unmodifiableSet( super.attributeKeys(), ATTRIBUTE_KEYS );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        soundAssetId = attributes.getIdForName( SOUND_ASSET_NAME, SOUND_ASSET_ID, Asset.TYPE_KEY, soundAssetId );
        looping = attributes.getValue( LOOPING, looping );
        volume = attributes.getValue( VOLUME, volume );
        pitch = attributes.getValue( PITCH, pitch );
        pan = attributes.getValue( PAN, pan );
        controllerId = attributes.getIdForName( CONTROLLER_NAME, CONTROLLER_ID, Controller.TYPE_KEY, controllerId );
        channel = attributes.getValue( CHANNEL, channel );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( SOUND_ASSET_ID, soundAssetId );
        attributes.put( LOOPING, looping );
        attributes.put( VOLUME, volume );
        attributes.put( PITCH, pitch );
        attributes.put( PAN, pan );
        attributes.put( CONTROLLER_ID, controllerId );
        attributes.put( CHANNEL, channel );
    }

}
