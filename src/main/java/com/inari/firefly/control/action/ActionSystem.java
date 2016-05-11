package com.inari.firefly.control.action;

import java.util.Iterator;

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public final class ActionSystem extends ComponentSystem<ActionSystem> {
    
    public static final FFSystemTypeKey<ActionSystem> SYSTEM_KEY = FFSystemTypeKey.create( ActionSystem.class );
    
    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        Action.TYPE_KEY
    };

    private DynArray<Action> actions;
    
    ActionSystem() {
        super( SYSTEM_KEY );
        actions = new DynArray<Action>();
    }
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        context.registerListener( ActionSystemEvent.TYPE_KEY, this );
    }
    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( ActionSystemEvent.TYPE_KEY, this );
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

    public final ActionBuilder getActionBuilder() {
        return new ActionBuilder();
    }

    @Override
    public final SystemComponentKey<?>[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter[] {
            new ActionBuilderAdapter( this )
        };
    };

    public final class ActionBuilder extends SystemComponentBuilder {
        
        protected ActionBuilder() {
            super( context );
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

    public final class ActionBuilderAdapter extends SystemBuilderAdapter<Action> {
        
        protected ActionBuilderAdapter( ActionSystem system ) {
            super( system, getActionBuilder() );
        }
        
        @Override
        public final SystemComponentKey<Action> componentTypeKey() {
            return Action.TYPE_KEY;
        }
        @Override
        public final Action getComponent( int id ) {
            return actions.get( id );
        }
        @Override
        public void deleteComponent( int id ) {
            deleteAction( id );
        }
        @Override
        public final Iterator<Action> getAll() {
            return actions.iterator();
        }
        @Override
        public final void deleteComponent( String name ) {
            deleteAction( getActionId( name ) );
            
        }
        @Override
        public final Action getComponent( String name ) {
            return getAction( getActionId( name ) );
        }
    }

}
