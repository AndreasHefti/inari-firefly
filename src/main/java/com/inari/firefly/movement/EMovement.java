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
    public static final AttributeKey<Integer> CONTROLLER_ID = new AttributeKey<Integer>( "controllerId", Integer.class, EMovement.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        VELOCITY_X,
        VELOCITY_Y,
        CONTROLLER_ID,
    };
    
    private final Vector2f velocityVector;
    private int controllerId = -1;

    EMovement() {
        velocityVector = new Vector2f( 0, 0 );
    }

    public final Vector2f getVelocityVector() {
        return velocityVector;
    }

    public final boolean isMoving() {
        return ( velocityVector.dx != 0 || velocityVector.dy != 0 );
    }

    @Override
    public final int getControllerId() {
        return controllerId;
    }

    public final void setControllerId( int controllerId ) {
        this.controllerId = controllerId;
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
        controllerId = attributes.getValue( CONTROLLER_ID, controllerId );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( VELOCITY_X, velocityVector.dx );
        attributes.put( VELOCITY_Y, velocityVector.dy );
        attributes.put( CONTROLLER_ID, controllerId );
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "EMovement [velocityVector=" );
        builder.append( velocityVector );
        builder.append( ", controllerId=" );
        builder.append( controllerId );
        builder.append( "]" );
        return builder.toString();
    }

}
