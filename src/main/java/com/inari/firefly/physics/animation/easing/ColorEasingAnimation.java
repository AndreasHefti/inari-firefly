package com.inari.firefly.physics.animation.easing;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.physics.animation.ValueAnimation;

public final class ColorEasingAnimation extends ValueAnimation<RGBColor> {

    public static final AttributeKey<EasingData> EASING_DATA_RED = AttributeKey.createEasingData( "easingDataRed", EasingAnimation.class );
    public static final AttributeKey<EasingData> EASING_DATA_GREEN = AttributeKey.createEasingData( "easingDataGreen", EasingAnimation.class );
    public static final AttributeKey<EasingData> EASING_DATA_BLUE = AttributeKey.createEasingData( "easingDataBlue", EasingAnimation.class );
    public static final AttributeKey<EasingData> EASING_DATA_ALPHA = AttributeKey.createEasingData( "easingDataAlpha", EasingAnimation.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        EASING_DATA_RED,
        EASING_DATA_GREEN,
        EASING_DATA_BLUE,
        EASING_DATA_ALPHA
    );
    
    private EasingData easingDataRed;
    private EasingData easingDataGreen;
    private EasingData easingDataBlue;
    private EasingData easingDataAlpha;
    
    private long time;
    
    protected ColorEasingAnimation( int id ) {
        super( id );
        easingDataRed = null;
        easingDataGreen = null;
        easingDataBlue = null;
        easingDataAlpha = null;
    }
    
    @Override
    public final void update() {
        time = context.getTime() - startTime;
        boolean active = update( easingDataRed ) || 
                         update( easingDataGreen ) || 
                         update( easingDataBlue ) || 
                         update( easingDataAlpha );
        if ( !active ) {
            finish();
        }
    }
    
    private final boolean update( final EasingData easingData ) {
        if ( easingData == null ) {
            return false;
        }
        
        if ( time > startTime + easingData.duration ) {
            if ( looping ) {
                reset();
                startTime = time;
            } else {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public final RGBColor getInitValue() {
        return getValue( -1, new RGBColor() );
    }
    
    @Override
    public RGBColor getValue( int componentId, RGBColor currentValue ) {
        currentValue.r = calc( easingDataRed, currentValue.r );
        currentValue.g = calc( easingDataGreen, currentValue.g );
        currentValue.b = calc( easingDataBlue, currentValue.b );
        currentValue.a = calc( easingDataAlpha, currentValue.a );
        
        return currentValue;
    }
    
    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return JavaUtils.unmodifiableSet( super.attributeKeys(), ATTRIBUTE_KEYS );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        easingDataRed = attributes.getValue( EASING_DATA_RED, easingDataRed );
        easingDataGreen = attributes.getValue( EASING_DATA_GREEN, easingDataGreen );
        easingDataBlue = attributes.getValue( EASING_DATA_BLUE, easingDataBlue );
        easingDataAlpha = attributes.getValue( EASING_DATA_ALPHA, easingDataAlpha );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        attributes.put( EASING_DATA_RED, easingDataRed );
        attributes.put( EASING_DATA_GREEN, easingDataGreen );
        attributes.put( EASING_DATA_BLUE, easingDataBlue );
        attributes.put( EASING_DATA_ALPHA, easingDataAlpha );
    }
    

    private float calc( EasingData easingData, float currentValue ) {
        if ( easingData == null ) {
            return currentValue;
        }
        return easingData.easingType.calc( time , easingData.startValue, easingData.changeInValue, easingData.duration );
    }

}
