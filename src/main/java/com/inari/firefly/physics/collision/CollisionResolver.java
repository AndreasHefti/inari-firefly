package com.inari.firefly.physics.collision;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.component.SystemComponent;

public abstract class CollisionResolver extends SystemComponent {
    
    public static final SystemComponentKey<CollisionResolver> TYPE_KEY = SystemComponentKey.create( CollisionResolver.class );
    
    public static final AttributeKey<Boolean> SEPARATE_AXIS = AttributeKey.createBoolean( "separateAxis", CollisionResolver.class );
    public static final AttributeKey<Boolean> Y_AXIS_FIRST = AttributeKey.createBoolean( "yAxisFirst", CollisionResolver.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        SEPARATE_AXIS,
        Y_AXIS_FIRST
    );
    
    protected boolean separateAxis = true;
    protected boolean yAxisFirst = false;

    protected CollisionResolver( int id ) {
        super( id );
    }
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

    public final boolean isSeparateAxis() {
        return separateAxis;
    }

    public final void setSeparateAxis( boolean separateAxis ) {
        this.separateAxis = separateAxis;
    }

    public final boolean yAxisFirst() {
        return yAxisFirst;
    }

    public final void yAxisFirst( boolean yAxisFirst ) {
        this.yAxisFirst = yAxisFirst;
    }
    
    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return JavaUtils.unmodifiableSet( super.attributeKeys(), ATTRIBUTE_KEYS );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        separateAxis = attributes.getValue( SEPARATE_AXIS, separateAxis );
        yAxisFirst = attributes.getValue( Y_AXIS_FIRST, yAxisFirst );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        attributes.put( SEPARATE_AXIS, separateAxis );
        attributes.put( Y_AXIS_FIRST, yAxisFirst );
    }

    public abstract void resolve( int entityId );

}
