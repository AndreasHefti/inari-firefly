package com.inari.firefly.control.state;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.aspect.AspectGroup;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public final class EState extends EntityComponent {
    
    public static final EntityComponentTypeKey<EState> TYPE_KEY = EntityComponentTypeKey.create( EState.class );
    public static final AspectGroup ASPECT_GROUP = new AspectGroup( "EState" );
    
    public static final AttributeKey<DynArray<Aspect>> STATE_ASPECTS = AttributeKey.createForDynArray( "stateAspects", EState.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        STATE_ASPECTS
    };
    
    private final Aspects stateAspects = ASPECT_GROUP.createAspects();

    protected EState() {
        super( TYPE_KEY );
        resetAttributes();
    }
    
    @Override
    public final void resetAttributes() {
        stateAspects.clear();
    }
    
    public final void addStateAspect( Aspect aspect ) {
        stateAspects.set( aspect );
    }
    
    public final void addStateAspects( Aspects aspects ) {
        stateAspects.set( aspects );
    }
    
    public final void setStateAspects( Aspects aspects ) {
        stateAspects.set( aspects );
    }
    
    public final IntBag getStateAspectValues() {
        return stateAspects.getValues();
    }
    
    public final void setStateAspect( Aspect aspect ) {
        stateAspects.set( aspect );
    }
    
    public final void resetStateAspect( Aspect aspect ) {
        stateAspects.reset( aspect );
    }
    
    public final boolean hasStateAspect( Aspect aspect ) {
        return stateAspects.contains( aspect );
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
//        if ( attributes.contains( STATE_ASPECTS ) ) {
//            setStateAspects( attributes.getValue( STATE_ASPECTS ) );
//        }
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
      //  attributes.put( STATE_ASPECTS, stateAspects.getValues() );
    }

}
