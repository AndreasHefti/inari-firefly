package com.inari.firefly.control.behavior;

import java.util.Iterator;

import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.EntityActivationEvent;
import com.inari.firefly.entity.EntityActivationListener;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public final class BehaviorSystem extends ComponentSystem<BehaviorSystem> implements UpdateEventListener, EntityActivationListener {
    
    public static final FFSystemTypeKey<BehaviorSystem> SYSTEM_KEY = FFSystemTypeKey.create( BehaviorSystem.class );
    
    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        Action.TYPE_KEY
    };

    private final DynArray<BehaviorNode> behaviorNodes;
    private final DynArray<Action> actions;
    private final IntBag entityIds;
    
    BehaviorSystem() {
        super( SYSTEM_KEY );
        actions = DynArray.create( Action.class, 20, 10 );
        behaviorNodes = DynArray.create( BehaviorNode.class, 20, 10 );
        entityIds = new IntBag( 50, -1 );
    }
    
    @Override
    public final void init( final FFContext context ) throws FFInitException {
        super.init( context );
        
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
    
    
    @Override
    public final void dispose( final FFContext context ) {
        context.disposeListener( UpdateEvent.TYPE_KEY, this );
        context.disposeListener( EntityActivationEvent.TYPE_KEY, this );
    }
    
    public final Action getAction( int actionId ) {
        if ( !actions.contains( actionId ) ) {
            return null;
        }
        
        return actions.get( actionId );
    }
    
    public final <A extends Action> A getActionAs( int actionId, Class<A> subType ) {
        Action action = getAction( actionId );
        if ( action == null ) {
            return null;
        }
        
        return subType.cast( action );
    }
    
    public final int getActionId( String actionName ) {
        if ( actionName == null ) {
            return -1;
        }
        
        for ( Action action : actions ) {
            if ( actionName.equals( action.getName() ) ) {
                return action.index();
            }
        }
        
        return -1;
    }
    
    public final void deleteAction( int actionId ) {
        disposeAction( actions.get( actionId ) );
    }
    
    private void disposeAction( Action action ) {
        if ( action == null ) {
            return;
        }
        
        action.dispose();
    }
    
    public final void clear() {
        for ( Action action : actions ) {
            disposeAction( action );
        }
        
        actions.clear();
    }
    
    public final void performAction( int actionId, int entityId ) {
        Action action = actions.get( actionId );
        if ( action != null ) {
            action.action( entityId );
        }
    }
    
    @Override
    public final void update( UpdateEvent event ) {
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
                actions.get( behavior.runningActionId ).action( entityId );
            }
        }
    }

    public final SystemComponentBuilder getActionBuilder( Class<? extends Action> componentType ) {
        if ( componentType == null ) {
            throw new IllegalArgumentException( "componentType is needed for SystemComponentBuilder for component: " + Action.TYPE_KEY.name() );
        }
        return new ActionBuilder( componentType );
    }

    @Override
    public final SystemComponentKey<?>[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter[] {
            new ActionBuilderAdapter()
        };
    };

    private final class ActionBuilder extends SystemComponentBuilder {
        
        private ActionBuilder( Class<? extends Action> componentType ) {
            super( context, componentType );
        }
        
        @Override
        public final SystemComponentKey<Action> systemComponentKey() {
            return Action.TYPE_KEY;
        }

        public int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            Action result = createSystemComponent( componentId, componentType, context );
            actions.set( result.index(), result );
            return result.index();
        }
    }

    private final class ActionBuilderAdapter extends SystemBuilderAdapter<Action> {
        private ActionBuilderAdapter() {
            super( BehaviorSystem.this, Action.TYPE_KEY );
        }
        @Override
        public final SystemComponentBuilder createComponentBuilder( Class<? extends Action> componentType ) {
            return getActionBuilder( componentType );
        }
        @Override
        public final Action get( int id ) {
            return actions.get( id );
        }
        @Override
        public void delete( int id ) {
            deleteAction( id );
        }
        @Override
        public final Iterator<Action> getAll() {
            return actions.iterator();
        }
        @Override
        public final int getId( String name ) {
            return getActionId( name );
        }
        @Override
        public final void activate( int id ) {
            throw new UnsupportedOperationException( "Action is not activable" );
        }
        @Override
        public final void deactivate( int id ) {
            throw new UnsupportedOperationException( "Action is not activable" );
        }
    }

}
