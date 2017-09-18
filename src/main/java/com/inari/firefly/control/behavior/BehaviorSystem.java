package com.inari.firefly.control.behavior;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.FFInitException;
import com.inari.firefly.control.action.Action;
import com.inari.firefly.control.action.EntityActionSystem;
import com.inari.firefly.entity.EntityActivationEvent;
import com.inari.firefly.entity.EntityActivationListener;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;
import com.inari.firefly.system.component.SystemComponentMap;
import com.inari.firefly.system.external.FFTimer;

public final class BehaviorSystem extends ComponentSystem<BehaviorSystem> implements UpdateEventListener, EntityActivationListener {
    
    public static final FFSystemTypeKey<BehaviorSystem> SYSTEM_KEY = FFSystemTypeKey.create( BehaviorSystem.class );
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        BehaviorNode.TYPE_KEY
    );

    private final SystemComponentMap<BehaviorNode> behaviorNodes;
    private final IntBag entityIds;
    private EntityActionSystem actionSystem;
    
    BehaviorSystem() {
        super( SYSTEM_KEY );
        behaviorNodes = SystemComponentMap.create( this, BehaviorNode.TYPE_KEY, 20, 10 );
        entityIds = new IntBag( 50, -1 );
    }
    
    @Override
    public final void init( final FFContext context ) throws FFInitException {
        super.init( context );
        
        actionSystem = context.getSystem( EntityActionSystem.SYSTEM_KEY );
        
        context.registerListener( UpdateEvent.TYPE_KEY, this );
        context.registerListener( EntityActivationEvent.TYPE_KEY, this );
    }
    
    public final boolean match( final Aspects aspects ) {
        return aspects.contains( EBehavoir.TYPE_KEY );
    }

    public final void entityActivated( int entityId, final Aspects aspects ) {
        entityIds.add( entityId );
    }

    public final void entityDeactivated( int entityId, final Aspects aspects ) {
        entityIds.remove( entityId );
    }

    public final void dispose( final FFContext context ) {
        clearSystem();
        
        context.disposeListener( UpdateEvent.TYPE_KEY, this );
        context.disposeListener( EntityActivationEvent.TYPE_KEY, this );
        
        actionSystem = null;
    }

    public final void clearSystem() {
        behaviorNodes.clear();
    }

    public final void update( final FFTimer timer ) {
        final int nullValue = entityIds.getNullValue();
        for ( int i = 0; i < entityIds.length(); i++ ) {
            final int entityId = entityIds.get( i );
            if ( nullValue == entityId ) {
                continue;
            }
            
            final EBehavoir behavior = context.getEntityComponent( entityId, EBehavoir.TYPE_KEY );
            if ( behavior.runningActionId < 0 ) {
                behaviorNodes.get( behavior.getRootNodeId() ).nextAction( entityId, behavior, context );
            }
            if ( behavior.runningActionId >= 0 ) {
                actionSystem.performAction( behavior.runningActionId, entityId );
            }
        }
    }

    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            behaviorNodes.getBuilderAdapter()
        );
    }
    
    public final SystemComponentBuilder getBehaviorNodeBuilder( Class<? extends BehaviorNode> componentType ) {
        if ( componentType == null ) {
            throw new IllegalArgumentException( "componentType is needed for SystemComponentBuilder for component: " + Action.TYPE_KEY.name() );
        }
        return behaviorNodes.getBuilder( componentType );
    }
}
