package com.inari.firefly.sound;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.firefly.FFContext;
import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.animation.FloatAnimation;
import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.AttributeMap;


public final class SoundAnimationController extends SoundController {
    
    public static final AttributeKey<Integer> VOLUME_ANIMATION_ID = new AttributeKey<Integer>( "volumeAnimationId", Integer.class, SoundAnimationController.class );
    public static final AttributeKey<Integer> PITCH_ANIMATION_ID = new AttributeKey<Integer>( "pitchAnimationId", Integer.class, SoundAnimationController.class );
    public static final AttributeKey<Integer> PAN_ANIMATION_ID = new AttributeKey<Integer>( "panAnimationId", Integer.class, SoundAnimationController.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        VOLUME_ANIMATION_ID,
        PITCH_ANIMATION_ID,
        PAN_ANIMATION_ID
    };
    
    private AnimationSystem animationSystem;
    
    private int volumeAnimationId = -1;
    private int pitchAnimationId = -1;
    private int panAnimationId = -1;

    SoundAnimationController( int id, FFContext context ) {
        super( id, context );
        animationSystem = context.get( FFContext.System.ANIMATION_SYSTEM );
    }

    public final int getVolumeAnimationId() {
        return volumeAnimationId;
    }

    public final void setVolumeAnimationId( int volumeAnimationId ) {
        this.volumeAnimationId = volumeAnimationId;
    }

    public final int getPitchAnimationId() {
        return pitchAnimationId;
    }

    public final void setPitchAnimationId( int pitchAnimationId ) {
        this.pitchAnimationId = pitchAnimationId;
    }

    public final int getPanAnimationId() {
        return panAnimationId;
    }

    public final void setPanAnimationId( int panAnimationId ) {
        this.panAnimationId = panAnimationId;
    }
    
    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) ) );
        return attributeKeys;
    }

    @Override
    public final void fromAttributeMap( AttributeMap attributes ) {
        super.fromAttributeMap( attributes );
        
        volumeAnimationId = attributes.getValue( VOLUME_ANIMATION_ID, volumeAnimationId );
        pitchAnimationId = attributes.getValue( PITCH_ANIMATION_ID, pitchAnimationId );
        panAnimationId = attributes.getValue( PAN_ANIMATION_ID, panAnimationId );
    }

    @Override
    public final void toAttributeMap( AttributeMap attributes ) {
        super.toAttributeMap( attributes );
        
        attributes.put( VOLUME_ANIMATION_ID, volumeAnimationId );
        attributes.put( PITCH_ANIMATION_ID, pitchAnimationId );
        attributes.put( PAN_ANIMATION_ID, panAnimationId );
    }

    @Override
    public final void update( long time, Sound sound ) {
        if ( volumeAnimationId >= 0 ) {
            FloatAnimation animation = animationSystem.getAnimation( FloatAnimation.class, volumeAnimationId );
            if ( animation != null ) {
                if ( animation.isActive() ) {
                    float newVolume = animation.get( sound.indexedId(), sound.volume );
                    if ( newVolume != sound.volume ) {
                        sound.volume = newVolume;
                    }
                    lowerSystemFacade.soundAttributesChanged( sound );
                }
            } else {
                volumeAnimationId = -1;
            }
        } 
        if ( pitchAnimationId >= 0 ) {
            FloatAnimation animation = animationSystem.getAnimation( FloatAnimation.class, pitchAnimationId );
            if ( animation != null ) {
                if ( animation.isActive() ) {
                    float newPitch = animation.get( sound.indexedId(), sound.pitch );
                    if ( newPitch != sound.pitch ) {
                        sound.pitch = newPitch;
                    }
                    lowerSystemFacade.soundAttributesChanged( sound );
                }
            } else {
                pitchAnimationId = -1;
            }
        }
        if ( panAnimationId >= 0 ) {
            FloatAnimation animation = animationSystem.getAnimation( FloatAnimation.class, panAnimationId );
            if ( animation != null ) {
                if ( animation.isActive() ) {
                    float newPan = animation.get( sound.indexedId(), sound.pan );
                    if ( newPan != sound.pan ) {
                        sound.pan = newPan;
                    }
                    lowerSystemFacade.soundAttributesChanged( sound );
                }
            } else {
                panAnimationId = -1;
            }
        } 
    }

}