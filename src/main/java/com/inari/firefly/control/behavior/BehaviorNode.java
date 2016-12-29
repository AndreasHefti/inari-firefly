package com.inari.firefly.control.behavior;

import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.SystemComponent;

public abstract class BehaviorNode extends SystemComponent {
    
    public static final SystemComponentKey<BehaviorNode> TYPE_KEY = SystemComponentKey.create( BehaviorNode.class );
    
    protected BehaviorNode( int index ) {
        super( index );
    }

    @Override
    public final IIndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }
    
    abstract void nextAction( int entityId, final EBehavoir behavior, final FFContext context );

}
