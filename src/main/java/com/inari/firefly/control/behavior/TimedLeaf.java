package com.inari.firefly.control.behavior;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.SystemComponentType;

public final class TimedLeaf extends BehaviorNode {
    
    public static final SystemComponentType COMPONENT_TYPE = new SystemComponentType( BehaviorNode.TYPE_KEY, TimedLeaf.class );
    public static final AttributeKey<Integer> ACTION_ID = AttributeKey.createInt( "actionId", TimedLeaf.class );
    public static final AttributeKey<Long> DURATION = AttributeKey.createLong( "duration", TimedLeaf.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        ACTION_ID,
        DURATION
    );
    
    private int actionId;
    private long duration;
    
    private long runStartTime;

    protected TimedLeaf( int index ) {
        super( index );
    }

    @Override
    final void nextAction( int entityId, EBehavoir behavior, FFContext context ) {
        if ( behavior.actionState == EBehavoir.ActionState.RUNNING ) {
            if ( context.getTime() - runStartTime > duration ) {
                behavior.runningActionId = -1;
                behavior.actionState = EBehavoir.ActionState.SUCCESS;
                return;
            }
        } else {
            // start action
            behavior.runningActionId = actionId;
            behavior.actionState = EBehavoir.ActionState.RUNNING;
            runStartTime = context.getTime();
        }
    }
    
    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return JavaUtils.unmodifiableSet( super.attributeKeys(), ATTRIBUTE_KEYS );
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
