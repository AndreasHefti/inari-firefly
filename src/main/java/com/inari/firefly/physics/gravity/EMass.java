package com.inari.firefly.physics.gravity;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public final class EMass extends EntityComponent {
    
    public static final EntityComponentTypeKey<EMass> TYPE_KEY = EntityComponentTypeKey.create( EMass.class );

    public static final AttributeKey<Float> MASS = AttributeKey.createFloat( "mass", EMass.class );
    public static final AttributeKey<Boolean> ON_GROUND = AttributeKey.createBoolean( "onGround", EMass.class );
    public static final AttributeKey<Boolean> ACTIVE = AttributeKey.createBoolean( "noGravityAspects", EMass.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        MASS,
        ON_GROUND,
        ACTIVE
    );
    
    float mass;
    boolean onGround;
    boolean active;
    
    long fallingStartTime;
    
    protected EMass() {
        super( TYPE_KEY ); 
    }
    
    @Override
    public final void resetAttributes() {
        mass = 0f;
        onGround = false;
        active = false;
    }

    public final float getMass() {
        return mass;
    }

    public final void setMass( float mass ) {
        this.mass = mass;
    }

    public final boolean isOnGround() {
        return onGround;
    }

    public final void setOnGround( boolean onGround ) {
        this.onGround = onGround;
    }

    public final boolean isActive() {
        return active;
    }

    public final void setActive( boolean active ) {
        this.active = active;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return ATTRIBUTE_KEYS;
    }
    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        mass = attributes.getValue( MASS, mass );
        onGround = attributes.getValue( ON_GROUND, onGround );
        active = attributes.getValue( ACTIVE, active );
    }
    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( MASS, mass );
        attributes.put( ON_GROUND, onGround );
        attributes.put( ACTIVE, active );
    }

}
