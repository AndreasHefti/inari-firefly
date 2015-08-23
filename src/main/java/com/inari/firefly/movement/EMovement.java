/*******************************************************************************
 * Copyright (c) 2015, Andreas Hefti, inarisoft@yahoo.de 
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
package com.inari.firefly.movement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.Vector2f;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public final class EMovement extends EntityComponent {
    
    public static final int COMPONENT_TYPE = Indexer.getIndexForType( EMovement.class, EntityComponent.class );
    
    public static final AttributeKey<Float> VELOCITY_X = new AttributeKey<Float>( "dx", Float.class, EMovement.class );
    public static final AttributeKey<Float> VELOCITY_Y = new AttributeKey<Float>( "dy", Float.class, EMovement.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        VELOCITY_X,
        VELOCITY_Y
    };
    
    private final Vector2f velocityVector;

    public EMovement() {
        super();
        velocityVector = new Vector2f( 0, 0 );
    }

    public final Vector2f getVelocityVector() {
        return velocityVector;
    }

    public final boolean isMoving() {
        return ( velocityVector.dx != 0 || velocityVector.dy != 0 );
    }
    
    @Override
    public final Class<? extends Component> getComponentType() {
        return EMovement.class;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        velocityVector.dx = attributes.getValue( VELOCITY_X, velocityVector.dx );
        velocityVector.dy = attributes.getValue( VELOCITY_Y, velocityVector.dy );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( VELOCITY_X, velocityVector.dx );
        attributes.put( VELOCITY_Y, velocityVector.dy );
    }

}
