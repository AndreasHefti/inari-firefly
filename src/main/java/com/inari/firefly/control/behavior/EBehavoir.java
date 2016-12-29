package com.inari.firefly.control.behavior;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.control.behavior.Action.ActionState;
import com.inari.firefly.entity.EntityComponent;

public final class EBehavoir extends EntityComponent {
    
    public static final EntityComponentTypeKey<EBehavoir> TYPE_KEY = EntityComponentTypeKey.create( EBehavoir.class );
    
    public static final AttributeKey<Integer> ROOT_NODE_ID = AttributeKey.createInt( "rootNodeId", EBehavoir.class );
    public static final AttributeKey<String> ROOT_NODE_NAME = AttributeKey.createString( "rootNodeName", EBehavoir.class );

    private int rootNodeId;
    
    private final Map<Integer, Integer> nodeMapping = new HashMap<Integer, Integer>();
    int runningActionId;
    ActionState actionState;

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
    
//    public final void stepIn( int nodeId ) {
//        pathToRoot.add( nodeId );
//    }
//    
//    public final int stepOut() {
//        if ( pathToRoot.size() <= 0 ) {
//            return -1;
//        }
//        
//        return pathToRoot.removeAt( pathToRoot.size() - 1 );
//    }
//
//    final int getActiveNodeId() {
//        if ( actionState != ActionState.RUNNING ) {
//            return rootNodeId;
//        }
//        
//        if ( pathToRoot.isEmpty() ) {
//            return rootNodeId;
//        } 
//        
//        return pathToRoot.get( pathToRoot.size() - 1 );
//    }

    public final ActionState getActionState() {
        return actionState;
    }

    final void setActionState( ActionState actionState ) {
        this.actionState = actionState;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ROOT_NODE_ID, ROOT_NODE_NAME ) );
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
        actionState = ActionState.SUCCESS;
        nodeMapping.clear();
        runningActionId = -1;
    }

}
