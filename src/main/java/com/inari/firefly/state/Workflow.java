package com.inari.firefly.state;

import java.util.Arrays;
import java.util.Set;

import com.inari.firefly.component.NamedIndexedComponent;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public final class Workflow extends NamedIndexedComponent {
    
    public static final AttributeKey<Integer> CURRENT_STATE_ID = new AttributeKey<Integer>( "currentStateId", Integer.class, Workflow.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        CURRENT_STATE_ID
    };
    
    private int currentStateId;

    Workflow( int workflowId ) {
        super( workflowId );
    }

    public final int getCurrentStateId() {
        return currentStateId;
    }

    public final void setCurrentStateId( int currentStateId ) {
        this.currentStateId = currentStateId;
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
        currentStateId = attributes.getValue( CURRENT_STATE_ID, currentStateId );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        attributes.put( NAME, name );
        attributes.put( CURRENT_STATE_ID, currentStateId );
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "Workflow [name=" );
        builder.append( name );
        builder.append( ", currentStateId=" );
        builder.append( currentStateId );
        builder.append( "]" );
        return builder.toString();
    }

}
