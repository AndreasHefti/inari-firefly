package com.inari.firefly.system;

import java.util.Arrays;
import java.util.Set;

import com.inari.firefly.component.NamedIndexedComponent;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.build.ComponentCreationException;

public final class Layer extends NamedIndexedComponent {
    
    public static final String DEFAULT_LAYER_NAME = "LAYER_";
    
    public static final AttributeKey<Integer> VIEW_ID = new AttributeKey<Integer>( "viewId", Integer.class, Layer.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        VIEW_ID
    };

    private int viewId;
    
    Layer( int layerId ) {
        super( layerId );
    }

    public int getViewId() {
        return viewId;
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
        
        viewId = attributes.getValue( VIEW_ID, -1 );
        if ( viewId < 0 ) {
            throw new ComponentCreationException( "Missing mandatory viewId attribute" );
        }
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( VIEW_ID, viewId );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "Layer [viewId=" );
        builder.append( viewId );
        builder.append( ", name=" );
        builder.append( name );
        builder.append( ", indexedId=" );
        builder.append( indexedId() );
        builder.append( "]" );
        return builder.toString();
    }

}
