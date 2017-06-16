package com.inari.firefly.physics.gravity;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public final class EMass extends EntityComponent {
    
    public static final EntityComponentTypeKey<EMass> TYPE_KEY = EntityComponentTypeKey.create( EMass.class );

    public static final AttributeKey<Float> MASS = AttributeKey.createFloat( "mass", EMass.class );
    public static final AttributeKey<Aspect> ON_GROUND_ASPECT = AttributeKey.createAspect( "onGroundAspect", EMass.class );
    public static final AttributeKey<Aspects> NO_GRAVITY_ASPECTS = AttributeKey.createAspects ( "noGravityAspects", EMass.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        MASS,
        ON_GROUND_ASPECT,
        NO_GRAVITY_ASPECTS
    );
    
    float mass;
    Aspect onGroundAspect;
    Aspects noGravityAspects;
    
    long fallingStartTime;
    
    protected EMass() {
        super( TYPE_KEY ); 
    }
    
    @Override
    public final void resetAttributes() {
        mass = 0f;
        onGroundAspect = null;
        noGravityAspects = null;
    }

    public final float getMass() {
        return mass;
    }

    public final void setMass( float mass ) {
        this.mass = mass;
    }

    public final Aspect getOnGroundAspect() {
        return onGroundAspect;
    }

    public final void setOnGroundAspect( Aspect onGroundAspect ) {
        this.onGroundAspect = onGroundAspect;
    }

    public final Aspects getNoGravityAspects() {
        return noGravityAspects;
    }

    public final void setNoGravityAspects( Aspects noGravityAspects ) {
        this.noGravityAspects = noGravityAspects;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return ATTRIBUTE_KEYS;
    }
    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        mass = attributes.getValue( MASS, mass );
        onGroundAspect = attributes.getValue( ON_GROUND_ASPECT, onGroundAspect );
        noGravityAspects = attributes.getValue( NO_GRAVITY_ASPECTS, noGravityAspects );
    }
    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( MASS, mass );
        attributes.put( ON_GROUND_ASPECT, onGroundAspect );
        attributes.put( NO_GRAVITY_ASPECTS, noGravityAspects );
    }

}
