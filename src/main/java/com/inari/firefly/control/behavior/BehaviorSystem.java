package com.inari.firefly.control.behavior;

import java.util.Iterator;
import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.list.DynArray;
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
import com.inari.firefly.system.external.FFTimer;

public final class BehaviorSystem extends ComponentSystem<BehaviorSystem> implements UpdateEventListener, EntityActivationListener {
    
    public static final FFSystemTypeKey<BehaviorSystem> SYSTEM_KEY = FFSystemTypeKey.create( BehaviorSystem.class );
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        BehaviorNode.TYPE_KEY
    );

    private final DynArray<BehaviorNode> behaviorNodes;
    private final IntBag entityIds;
    
    private EntityActionSystem actionSystem;
    
    BehaviorSystem() {
        super( SYSTEM_KEY );
        behaviorNodes = DynArray.create( BehaviorNode.class, 20, 10 );
        entityIds = new IntBag( 50, -1 );
    }
    
    @Override
    public final void init( final FFContext context ) throws FFInitException {
        super.init( context );
        
        actionSystem = context.getSystem( EntityActionSystem.SYSTEM_KEY );
        
        context.registerListener( UpdateEvent.TYPE_KEY, this );
        context.registerListener( EntityActivationEvent.TYPE_KEY, this );
    }
    
    @Override
    public final boolean match( final Aspects aspects ) {
        return aspects.contains( EBehavoir.TYPE_KEY );
    }

    @Override
    public final void entityActivated( int entityId, final Aspects aspects ) {
        entityIds.add( entityId );
    }

    @Override
    public final void entityDeactivated( int entityId, final Aspects aspects ) {
        entityIds.remove( entityId );
    }
    
    public final BehaviorNode getBehaviorNode( String name ) {
        int behaviorNodeId = getBehaviorNodeId( name );
        if ( behaviorNodeId >= 0 ) {
            return behaviorNodes.get( behaviorNodeId );
        }
        
        return null;
    }
    
    public final BehaviorNode getBehaviorNode( int nodeId ) {
        if ( behaviorNodes.contains( nodeId ) ) {
            return behaviorNodes.get( nodeId );
        }
        
        return null;
    }
    
    public final int getBehaviorNodeId( String name ) {
        for( int i = 0; i < behaviorNodes.capacity(); i++ ) {
            BehaviorNode behaviorNode = behaviorNodes.get( i );
            if ( behaviorNode == null ) {
                continue;
            }
            if ( name.equals( behaviorNode.getName() ) ) {
                return behaviorNode.index();
            }
        }
        
        return -1;
    }
    
    @Override
    public final void dispose( final FFContext context ) {
        context.disposeListener( UpdateEvent.TYPE_KEY, this );
        context.disposeListener( EntityActivationEvent.TYPE_KEY, this );
        
        actionSystem = null;
    }
    
    public final void disposeBehaviorNode( BehaviorNode node ) {
        if ( node == null ) {
            return;
        }
        
        node.dispose();
    }
    
    public final void deleteBehaviorNode( int nodeId ) {
        if ( behaviorNodes.contains( nodeId ) ) {
            disposeBehaviorNode( behaviorNodes.remove( nodeId ) );
        }
    }
    
    @Override
    public final void clearSystem() {
        entityIds.clear();
        for ( BehaviorNode node : behaviorNodes ) {
            disposeBehaviorNode( node );
        }
        
        behaviorNodes.clear();
    }

    @Override
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

    @Override
    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            new BehaviorNodeBuilderAdapter()
        );
    }
    
    public final SystemComponentBuilder getBehaviorNodeBuilder( Class<? extends BehaviorNode> componentType ) {
        if ( componentType == null ) {
            throw new IllegalArgumentException( "componentType is needed for SystemComponentBuilder for component: " + Action.TYPE_KEY.name() );
        }
        return new BehaviorNodeBuilder( componentType );
    }
    
    private final class BehaviorNodeBuilder extends SystemComponentBuilder {
        
        private BehaviorNodeBuilder( Class<? extends BehaviorNode> componentType ) {
            super( context, componentType );
        }
        
        @Override
        public final SystemComponentKey<BehaviorNode> systemComponentKey() {
            return BehaviorNode.TYPE_KEY;
        }

        public int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            BehaviorNode result = createSystemComponent( componentId, componentType, context );
            behaviorNodes.set( result.index(), result );
            return result.index();
        }
    }

    private final class BehaviorNodeBuilderAdapter extends SystemBuilderAdapter<BehaviorNode> {
        private BehaviorNodeBuilderAdapter() {
            super( BehaviorSystem.this, BehaviorNode.TYPE_KEY );
        }
        @Override
        public final SystemComponentBuilder createComponentBuilder( Class<? extends BehaviorNode> componentType ) {
            return getBehaviorNodeBuilder( componentType );
        }
        @Override
        public final BehaviorNode get( int id ) {
            return behaviorNodes.get( id );
        }
        @Override
        public void delete( int id ) {
            deleteBehaviorNode( id );
        }
        @Override
        public final Iterator<BehaviorNode> getAll() {
            return behaviorNodes.iterator();
        }
        @Override
        public final int getId( String name ) {
            return getBehaviorNodeId( name );
        }
        @Override
        public final void activate( int id ) {
            throw new UnsupportedOperationException( "BehaviorNode is not activable" );
        }
        @Override
        public final void deactivate( int id ) {
            throw new UnsupportedOperationException( "BehaviorNode is not activable" );
        }
    }

}
