package com.inari.firefly.control.state;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.indexed.Indexed;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public final class EState extends EntityComponent {
    
    public static final EntityComponentTypeKey<EState> TYPE_KEY = EntityComponentTypeKey.create( EState.class );
    
    public static final AttributeKey<Integer> STATE = new AttributeKey<Integer>( "state", Integer.class, EState.class );
    public static final AttributeKey<IntBag> STATE_FLAGS = new AttributeKey<IntBag>( "stateFlags", IntBag.class, EState.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        STATE,
        STATE_FLAGS
    };
    
    
    private int state;
    private final BitSet stateFlags = new BitSet( 10 );

    protected EState() {
        super( TYPE_KEY );
        resetAttributes();
    }
    
    @Override
    public final void resetAttributes() {
        state = -1;
        stateFlags.clear();
    }
    
    public final int getState() {
        return state;
    }

    public final void setState( int state ) {
        this.state = state;
    }
    
    public final void setStateFlag( Indexed indexed ) {
        stateFlags.set( indexed.index() );
    }
    
    public final void resetStateFlag( Indexed indexed ) {
        stateFlags.set( indexed.index(), false );
    }

    public final boolean hasStateFlag( Indexed indexed ) {
        return stateFlags.get( indexed.index() );
    }
    
    public final void setStateFlags( IntBag stateFlags ) {
        this.stateFlags.clear();
        IntIterator iterator = stateFlags.iterator();
        while( iterator.hasNext() ) {
            this.stateFlags.set( iterator.next() );
        }
    }
    
    public final IntBag getStateFalgs() {
        IntBag result = new IntBag( stateFlags.size(), -1 );
        for ( int i = stateFlags.nextSetBit( 0 ); i >= 0; i = stateFlags.nextSetBit( i+1 ) ) {
             result.add( i );
        }
        return result;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        state = attributes.getValue( STATE, state );
        if ( attributes.contains( STATE_FLAGS ) ) {
            setStateFlags( attributes.getValue( STATE_FLAGS ) );
        }
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( STATE, state );
        attributes.put( STATE_FLAGS, getStateFalgs() );
    }

}
