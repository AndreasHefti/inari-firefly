package com.inari.firefly.control.state;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.aspect.AspectsBuilder;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public final class EState extends EntityComponent {
    
    public static final EntityComponentTypeKey<EState> TYPE_KEY = EntityComponentTypeKey.create( EState.class );
    
    public static final AttributeKey<IntBag> STATE_ASPECTS = new AttributeKey<IntBag>( "stateAspects", IntBag.class, EState.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        STATE_ASPECTS
    };
    
    private final Aspects stateAspects = AspectsBuilder.createWithCapacity( 10 );

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
        stateAspects.add( aspects );
    }
    
    public final void setStateAspects( Aspects aspects ) {
        stateAspects.clear();
        stateAspects.add( aspects );
    }
    
    public final void setStateAspects( IntBag values ) {
        stateAspects.clear();
        IntIterator iterator = values.iterator();
        while ( iterator.hasNext() ) {
            stateAspects.set( iterator.next() );
        }
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
        if ( attributes.contains( STATE_ASPECTS ) ) {
            setStateAspects( attributes.getValue( STATE_ASPECTS ) );
        }
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( STATE_ASPECTS, stateAspects.getValues() );
    }

}
