package com.inari.firefly.physics.collision;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.graphics.view.Layer;
import com.inari.firefly.system.component.SystemComponent;

public final class ContactConstraint extends SystemComponent {
    
    public static final SystemComponentKey<ContactConstraint> TYPE_KEY = SystemComponentKey.create( ContactConstraint.class );
    public static final AttributeKey<Integer> LAYER_ID = AttributeKey.createInt( "layerId", ContactConstraint.class );
    public static final AttributeKey<String> LAYER_NAME = AttributeKey.createString( "layerName", ContactConstraint.class );
    public static final AttributeKey<Rectangle> CONTACT_BOUNDS = AttributeKey.createRectangle( "contactBounds", ContactConstraint.class );
    public static final AttributeKey<Aspects> MATERIAL_TYPE_FILTER = AttributeKey.createAspects( "materialTypeFilter", ContactConstraint.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        LAYER_ID,
        CONTACT_BOUNDS,
        MATERIAL_TYPE_FILTER
    );
    
    int layerId = -1;
    final Rectangle contactBounds;
    final Aspects materialTypeFilter;

    protected ContactConstraint( int index ) {
        super( index );
        
        contactBounds = new Rectangle();
        materialTypeFilter = CollisionSystem.MATERIAL_ASPECT_GROUP.createAspects();
    }

    public final IIndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

    public final int getLayerId() {
        return layerId;
    }

    public final void setLayerId( int layerId ) {
        this.layerId = layerId;
    }

    public final Rectangle getContactBounds() {
        return contactBounds;
    }

    public final Aspects getMaterialTypeFilter() {
        return materialTypeFilter;
    }
    
    public final boolean isFiltering() {
        return !materialTypeFilter.isEmpty();
    }

    public final int width() {
        return contactBounds.width;
    }
    
    public final int height() {
        return contactBounds.height;
    }
    
    public final int pivotX() {
        return contactBounds.x;
    }
    
    public final int pivotY() {
        return contactBounds.y;
    }
    
    public final Contacts createContacts() {
        return new Contacts( this.index );
    }
    
    public final boolean match( final Aspect materialType ) {
        if ( !isFiltering() ) {
            return true;
        } else {
            return ( materialType != null && materialTypeFilter.contains( materialType ) );
        }
    }
    
    public final Set<AttributeKey<?>> attributeKeys() {
        return ATTRIBUTE_KEYS;
    }

    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        layerId = attributes.getIdForName( LAYER_NAME, LAYER_ID, Layer.TYPE_KEY, layerId );
        contactBounds.setFrom( attributes.getValue( CONTACT_BOUNDS, contactBounds ) );

        if ( attributes.contains( MATERIAL_TYPE_FILTER ) ) {
            materialTypeFilter.clear();
            materialTypeFilter.set( attributes.getValue( MATERIAL_TYPE_FILTER ) );
        }
    }

    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( LAYER_ID, layerId );
        attributes.put( CONTACT_BOUNDS, contactBounds );
        attributes.put( MATERIAL_TYPE_FILTER, materialTypeFilter );
    }

}
