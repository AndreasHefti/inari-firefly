package com.inari.firefly.control.behavior;

import java.util.Set;

import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.control.behavior.Action.ActionState;
import com.inari.firefly.system.FFContext;

public final class TimedLeaf extends BehaviorNode {
    
    public static final AttributeKey<Integer> ACTION_ID = AttributeKey.createInt( "actionId", TimedLeaf.class );
    public static final AttributeKey<Long> DURATION = AttributeKey.createLong( "actionId", TimedLeaf.class );
    
    private int actionId;
    private long duration;
    
    private long runStartTime;

    protected TimedLeaf( int index ) {
        super( index );
    }

    @Override
    final void nextAction( int entityId, EBehavoir behavior, FFContext context ) {
        if ( behavior.actionState == ActionState.RUNNING ) {
            if ( context.getTime() - runStartTime > duration ) {
                behavior.runningActionId = -1;
                behavior.actionState = ActionState.SUCCESS;
                return;
            }
        } else {
            // start action
            behavior.runningActionId = actionId;
            behavior.actionState = ActionState.RUNNING;
            runStartTime = context.getTime();
        }
    }
    
    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.add( ACTION_ID );
        attributeKeys.add( DURATION );
        return attributeKeys;
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        actionId = attributes.getValue( ACTION_ID, actionId );
        duration = attributes.getValue( DURATION, duration );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( ACTION_ID, actionId );
        attributes.put( DURATION, duration );
    }

}
