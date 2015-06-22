package com.inari.firefly.sound;

import java.util.Arrays;
import java.util.Set;

import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.AttributeMap;
import com.inari.firefly.component.NamedIndexedComponent;

public final class Sound extends NamedIndexedComponent {
    
    public static final AttributeKey<Boolean> LOOPING = new AttributeKey<Boolean>( "looping", Boolean.class, Sound.class );
    public static final AttributeKey<Float> VOLUME = new AttributeKey<Float>( "volume", Float.class, Sound.class );
    public static final AttributeKey<Float> PITCH = new AttributeKey<Float>( "pitch", Float.class, Sound.class );
    public static final AttributeKey<Float> PAN = new AttributeKey<Float>( "pan", Float.class, Sound.class );
    public static final AttributeKey<Integer> CONTROLLER_ID = new AttributeKey<Integer>( "controllerId", Integer.class, Sound.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        LOOPING,
        VOLUME,
        PITCH,
        PAN,
        CONTROLLER_ID
    };
    
    private boolean looping;
    float volume;
    float pitch;
    float pan;
    private int controllerId;
    
    Sound( int id ) {
        super( id );
    }

    @Override
    public final Class<Sound> getIndexedObjectType() {
        return Sound.class;
    }

    @Override
    public final Class<Sound> getComponentType() {
        return Sound.class;
    }

    public final boolean isLooping() {
        return looping;
    }

    public final void setLooping( boolean looping ) {
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

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( Arrays.asList( ATTRIBUTE_KEYS ) );
        return attributeKeys;
    }

    @Override
    public final void fromAttributeMap( AttributeMap attributes ) {
        super.fromAttributeMap( attributes );
        
        looping = attributes.getValue( LOOPING, looping );
        volume = attributes.getValue( VOLUME, volume );
        pitch = attributes.getValue( PITCH, pitch );
        pan = attributes.getValue( PAN, pan );
        controllerId = attributes.getValue( CONTROLLER_ID, controllerId );
    }

    @Override
    public final void toAttributeMap( AttributeMap attributes ) {
        super.toAttributeMap( attributes );
        
        attributes.put( LOOPING, looping );
        attributes.put( VOLUME, volume );
        attributes.put( PITCH, pitch );
        attributes.put( PAN, pan );
        attributes.put( CONTROLLER_ID, controllerId );
    }

    

}
