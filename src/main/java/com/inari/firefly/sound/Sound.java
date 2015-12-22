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
package com.inari.firefly.sound;

import java.util.Arrays;
import java.util.Set;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.component.SystemComponent;

public final class Sound extends SystemComponent {
    
    public static final SystemComponentKey<Sound> TYPE_KEY = SystemComponentKey.create( Sound.class );
    
    public static final AttributeKey<Integer> ASSET_ID = new AttributeKey<Integer>( "assetId", Integer.class, Sound.class );
    public static final AttributeKey<Boolean> LOOPING = new AttributeKey<Boolean>( "looping", Boolean.class, Sound.class );
    public static final AttributeKey<Float> VOLUME = new AttributeKey<Float>( "volume", Float.class, Sound.class );
    public static final AttributeKey<Float> PITCH = new AttributeKey<Float>( "pitch", Float.class, Sound.class );
    public static final AttributeKey<Float> PAN = new AttributeKey<Float>( "pan", Float.class, Sound.class );
    public static final AttributeKey<Integer> CHANNEL = new AttributeKey<Integer>( "channel", Integer.class, Sound.class );
    public static final AttributeKey<Integer> CONTROLLER_ID = new AttributeKey<Integer>( "controllerId", Integer.class, Sound.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        ASSET_ID,
        LOOPING,
        VOLUME,
        PITCH,
        PAN,
        CHANNEL,
        CONTROLLER_ID,
    };
    
    private int assetId;
    private boolean looping;
    private float volume;
    private float pitch;
    private float pan;
    private int controllerId;
    boolean streaming;
    long instanceId;
    int channel;
    
    Sound( int id ) {
        super( id );
        assetId = -1;
        looping = false;
        volume = 1;
        pitch = 1;
        pan = 0;
        controllerId = -1;
        streaming = false;
        instanceId = -1;
        channel = 0;
    }

    public final int getAssetId() {
        return assetId;
    }

    final void setAssetId( int assetId ) {
        this.assetId = assetId;
    }

    public final boolean isStreaming() {
        return streaming;
    }
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

    @Override
    public final Class<Sound> indexedObjectType() {
        return Sound.class;
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
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( Arrays.asList( ATTRIBUTE_KEYS ) );
        return attributeKeys;
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        assetId = attributes.getValue( ASSET_ID, assetId );
        looping = attributes.getValue( LOOPING, looping );
        volume = attributes.getValue( VOLUME, volume );
        pitch = attributes.getValue( PITCH, pitch );
        pan = attributes.getValue( PAN, pan );
        controllerId = attributes.getValue( CONTROLLER_ID, controllerId );
        channel = attributes.getValue( CHANNEL, channel );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( ASSET_ID, assetId );
        attributes.put( LOOPING, looping );
        attributes.put( VOLUME, volume );
        attributes.put( PITCH, pitch );
        attributes.put( PAN, pan );
        attributes.put( CONTROLLER_ID, controllerId );
        attributes.put( CHANNEL, channel );
    }

}
