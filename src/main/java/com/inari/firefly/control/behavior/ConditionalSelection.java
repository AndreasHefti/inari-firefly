package com.inari.firefly.control.behavior;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.control.behavior.Action.ActionState;
import com.inari.firefly.system.FFContext;

public final class ConditionalSelection extends BehaviorNode {
    
    public static final AttributeKey<DynArray<Mapping>> NODE_MAPPING = AttributeKey.createDynArray( "subNodeMapping", ConditionalSelection.class, Mapping.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        NODE_MAPPING
    );
    
    private final DynArray<Mapping> subNodeMapping;

    protected ConditionalSelection( int index ) {
        super( index );
        subNodeMapping = DynArray.create( Mapping.class, 10, 10 );
    }

    @Override
    final void nextAction( int entityId, EBehavoir behavior, FFContext context ) {
        int actualSubNodeId = behavior.getSubNodeMapping( index );
        if ( actualSubNodeId >= 0 ) {
            final BehaviorNode subNode = context.getSystemComponent( BehaviorNode.TYPE_KEY, actualSubNodeId );
            subNode.nextAction( entityId, behavior, context );
            if ( behavior.runningActionId >= 0 ) {
                return;
            }
        }
        
        behavior.removeNodeMapping( index );
        // find next action
        for ( int i = 0; i < subNodeMapping.capacity(); i++ ) {
            Mapping mapping = subNodeMapping.get( i );
            if ( mapping == null ) {
                continue;
            }
            if ( mapping.condition.check( entityId, behavior, context ) == ActionState.RUNNING ) {
                final BehaviorNode subNode = context.getSystemComponent( BehaviorNode.TYPE_KEY, mapping.nodeMapping );
                subNode.nextAction( entityId, behavior, context );
                if ( behavior.runningActionId >= 0 ) {
                    behavior.setNodeMapping( index, subNode.index() );
                    return;
                }
            }
        }
    }
    

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return JavaUtils.unmodifiableSet( super.attributeKeys(), ATTRIBUTE_KEYS );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        if ( attributes.contains( NODE_MAPPING ) ) {
            subNodeMapping.clear();
            subNodeMapping.addAll( attributes.getValue( NODE_MAPPING ) );
        } 
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( NODE_MAPPING, subNodeMapping );
    }
    
    public static final class Mapping {
        final BCondition condition;
        final int nodeMapping;
        
        public Mapping( BCondition condition, int nodeMapping ) {
            super();
            this.condition = condition;
            this.nodeMapping = nodeMapping;
        }
    }

}
