package com.inari.firefly.control.behavior;

import java.util.Set;

import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.control.behavior.Action.ActionState;
import com.inari.firefly.system.FFContext;

public class ConditionalLeaf extends BehaviorNode {
    
    public static final AttributeKey<Integer> ACTION_ID = AttributeKey.createInt( "actionId", ConditionalLeaf.class );
    public static final AttributeKey<BCondition> RUN_CONDITION = new AttributeKey<>( "runCondition", BCondition.class, ConditionalLeaf.class );
    
    private int actionId;
    private BCondition runCondition;

    protected ConditionalLeaf( int index ) {
        super( index );
    }

    @Override
    final void nextAction( int entityId, final EBehavoir behavior, final FFContext context ) {
        if ( behavior.actionState == ActionState.RUNNING ) {
            // run further, succeed or fail
            behavior.actionState = runCondition.check( entityId, behavior, context );
        } else {
            // start action
            behavior.runningActionId = actionId;
            behavior.actionState = ActionState.RUNNING;
        }
    }
    
    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.add( ACTION_ID );
        attributeKeys.add( RUN_CONDITION );
        return attributeKeys;
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        actionId = attributes.getValue( ACTION_ID, actionId );
        runCondition = attributes.getValue( RUN_CONDITION, runCondition );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( ACTION_ID, actionId );
        attributes.put( RUN_CONDITION, runCondition );
    }

}
