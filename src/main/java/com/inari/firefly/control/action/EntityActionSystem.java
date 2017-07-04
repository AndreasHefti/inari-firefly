package com.inari.firefly.control.action;

import java.util.Iterator;
import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public class EntityActionSystem extends ComponentSystem<EntityActionSystem> implements EntityActionEventListener {
    
    public static final FFSystemTypeKey<EntityActionSystem> SYSTEM_KEY = FFSystemTypeKey.create( EntityActionSystem.class );
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        Action.TYPE_KEY
    );

    private final DynArray<Action> actions;
    
    EntityActionSystem() {
        super( SYSTEM_KEY );
        actions = DynArray.create( Action.class, 20, 10 );
    }

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        context.registerListener( EntityActionEvent.TYPE_KEY, this );
    }

    public final Action getAction( int actionId ) {
        if ( !actions.contains( actionId ) ) {
            return null;
        }
        
        return actions.get( actionId );
    }
    
    public final Action getAction( String actionName ) {
        int actionId = getActionId( actionName );
        if ( actionId >= 0 ) {
            return actions.get( actionId );
        }
        
        return null;
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
        if ( actions.contains( actionId ) ) {
            disposeAction( actions.remove( actionId ) );
        }
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
    public void dispose( FFContext context ) {
        clear();
        
        context.disposeListener( EntityActionEvent.TYPE_KEY, this );
    }

    @Override
    public Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            new ActionBuilderAdapter()
        );
    }
    
    public final SystemComponentBuilder getActionBuilder( Class<? extends Action> componentType ) {
        if ( componentType == null ) {
            throw new IllegalArgumentException( "componentType is needed for SystemComponentBuilder for component: " + Action.TYPE_KEY.name() );
        }
        return new ActionBuilder( componentType );
    }
    
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
            super( EntityActionSystem.this, Action.TYPE_KEY );
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
