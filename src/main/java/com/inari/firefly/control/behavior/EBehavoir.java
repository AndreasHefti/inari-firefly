package com.inari.firefly.control.behavior;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.control.behavior.EBehavoir.ActionState;
import com.inari.firefly.entity.EntityComponent;

public final class EBehavoir extends EntityComponent {
    
    public enum ActionState {
        SUCCESS,
        FAILURE,
        RUNNING
    }

    public static final EntityComponentTypeKey<EBehavoir> TYPE_KEY = EntityComponentTypeKey.create( EBehavoir.class );
    
    public static final AttributeKey<Integer> ROOT_NODE_ID = AttributeKey.createInt( "rootNodeId", EBehavoir.class );
    public static final AttributeKey<String> ROOT_NODE_NAME = AttributeKey.createString( "rootNodeName", EBehavoir.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        ROOT_NODE_ID
    );

    private int rootNodeId;
    
    private final Map<Integer, Integer> nodeMapping = new HashMap<Integer, Integer>();
    int runningActionId;
    EBehavoir.ActionState actionState;

    protected EBehavoir( EntityComponentTypeKey<?> indexedTypeKey ) {
        super( indexedTypeKey );
        resetAttributes();
    }

    public final int getRootNodeId() {
        return rootNodeId;
    }

    public final void setRootNodeId( int rootNodeId ) {
        this.rootNodeId = rootNodeId;
    }
    
    public final void setNodeMapping( int nodeId, int subNodeId ) {
        nodeMapping.put( nodeId, subNodeId );
    }
    
    public final int getSubNodeMapping( int nodeId ) {
        if ( !nodeMapping.containsKey( nodeId ) ) {
            return -1;
        }
        
        return nodeMapping.get( nodeId );
    }
    
    public final void removeNodeMapping( int nodeId ) {
        nodeMapping.remove( nodeId );
    }
    
    public final void clearNodeMapping() {
        nodeMapping.clear();
    }

    public final EBehavoir.ActionState getActionState() {
        return actionState;
    }

    final void setActionState( EBehavoir.ActionState actionState ) {
        this.actionState = actionState;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return ATTRIBUTE_KEYS;
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        rootNodeId = attributes.getIdForName( ROOT_NODE_NAME, ROOT_NODE_ID, BehaviorNode.TYPE_KEY, rootNodeId );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( ROOT_NODE_ID, rootNodeId );
    }

    @Override
    public final void resetAttributes() {
        rootNodeId = -1;
        actionState = EBehavoir.ActionState.SUCCESS;
        nodeMapping.clear();
        runningActionId = -1;
    }

}
