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
package com.inari.firefly.physics.movement;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.geom.Vector2f;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.system.external.FFTimer;
import com.inari.firefly.system.external.FFTimer.UpdateScheduler;

public final class EMovement extends EntityComponent {
    
    public static final EntityComponentTypeKey<EMovement> TYPE_KEY = EntityComponentTypeKey.create( EMovement.class );
    
    public static final AttributeKey<Boolean> ACTIVE = AttributeKey.createBoolean( "active", EMovement.class );
    public static final AttributeKey<Float> VELOCITY_X = AttributeKey.createFloat( "dx", EMovement.class );
    public static final AttributeKey<Float> VELOCITY_Y = AttributeKey.createFloat( "dy", EMovement.class );
    public static final AttributeKey<Float> ACCELERATION_X = AttributeKey.createFloat( "ax", EMovement.class );
    public static final AttributeKey<Float> ACCELERATION_Y = AttributeKey.createFloat( "ay", EMovement.class );
    public static final AttributeKey<Float> MASS = AttributeKey.createFloat( "mass", EMovement.class );
    public static final AttributeKey<Float> UPDATE_RESOLUTION = AttributeKey.createFloat( "updateResolution", EMovement.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        ACTIVE,
        VELOCITY_X,
        VELOCITY_Y,
        ACCELERATION_X,
        ACCELERATION_Y,
        MASS,
        UPDATE_RESOLUTION
    );
    
    
    final Vector2f velocity = new Vector2f( 0, 0 );
    final Vector2f acceleration = new Vector2f( 0, 0 );
    float mass;
    UpdateScheduler updateScheduler = null;
    
    boolean active;
    boolean onGround;
    float updateResolution;

    public EMovement() {
        super( TYPE_KEY );
        resetAttributes();
    }

    @Override
    public final void resetAttributes() {
        active = false;
        setVelocityX( 0f );
        setVelocityY( 0f );
        setAccelerationX( 0f );
        setAccelerationY( 0f );
        updateResolution = -1;
        updateScheduler = null;
        mass = 0f;
        onGround = false;
    }

    public final boolean isActive() {
        return active;
    }

    public final void setActive( boolean active ) {
        this.active = active;
    }
    
    public final void setVelocityX( float velocityX ) {
        velocity.dx = velocityX;
    }

    public final float getVelocityX() {
        return velocity.dx;
    }
    
    public final void setVelocityY( float velocityY ) {
        velocity.dy = velocityY;
    }
    
    public final float getVelocityY() {
        return velocity.dy;
    }
    
    public final void setVelocity( float velocityX, float velocityY ) {
        velocity.dx = velocityX;
        velocity.dy = velocityY;
    }
    
    public final void setAccelerationX( float acc ) {
        acceleration.dx = acc;
    }

    public final float getAccelerationX() {
        return acceleration.dx;
    }
    
    public final void setAccelerationY( float acc ) {
        acceleration.dy = acc;
    }
    
    public final float getAccelerationY() {
        return acceleration.dy;
    }
    
    public final void setAcceleration( float accX, float accY ) {
        acceleration.dx = accX;
        acceleration.dy = accY;
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

    public final float getUpdateResolution() {
        return updateResolution;
    }

    public final void setUpdateResolution( float updateResolution ) {
        this.updateResolution = updateResolution;
    }
    
    final boolean needsUpdate( final FFTimer timer ) {
        if ( updateResolution <= 0 ) {
            return true;
        }
        
        if ( updateScheduler == null ) {
            updateScheduler = timer.createUpdateScheduler( updateResolution );
        }
        
        return updateScheduler.needsUpdate();
    }

    public final boolean isMoving() {
        return active && ( velocity.dx != 0 || velocity.dy != 0 );
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return ATTRIBUTE_KEYS;
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        active = attributes.getValue( ACTIVE, active );
        velocity.dx = attributes.getValue( VELOCITY_X, velocity.dx );
        velocity.dy = attributes.getValue( VELOCITY_Y, velocity.dy );
        acceleration.dx = attributes.getValue( ACCELERATION_X, acceleration.dx );
        acceleration.dy = attributes.getValue( ACCELERATION_Y, acceleration.dy );
        mass = attributes.getValue( MASS, mass );
        setUpdateResolution( attributes.getValue( UPDATE_RESOLUTION, updateResolution ) );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( ACTIVE, active );
        attributes.put( VELOCITY_X, velocity.dx );
        attributes.put( VELOCITY_Y, velocity.dy );
        attributes.put( ACCELERATION_X, acceleration.dx );
        attributes.put( ACCELERATION_Y, acceleration.dy );
        attributes.put( UPDATE_RESOLUTION, updateResolution );
    }

}
