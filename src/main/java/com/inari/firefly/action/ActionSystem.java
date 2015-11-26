package com.inari.firefly.action;

import java.util.Iterator;

import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.action.event.ActionEvent;
import com.inari.firefly.action.event.ActionEventListener;
import com.inari.firefly.component.Component;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public final class ActionSystem extends ComponentSystem implements ActionEventListener {
    
    private static final SystemComponentKey[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        Action.TYPE_KEY
    };

    public static final TypedKey<ActionSystem> CONTEXT_KEY = TypedKey.create( "FF_ACTION_SYSTEM", ActionSystem.class );
    
    private DynArray<Action> actions;
    
    ActionSystem() {
        actions = new DynArray<Action>();
    }
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        context.registerListener( ActionEvent.class, this );
    }

    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( ActionEvent.class, this );
    }
    
    public final void deleteAction( int actionId ) {
        disposeAction( actions.get( actionId ) );
    }
    
    private void disposeAction( Action action ) {
        if ( action == null ) {
            return;
        }
        
        action.dispose( context );
        action.dispose();
    }
    
    public final void clear() {
        for ( Action action : actions ) {
            disposeAction( action );
        }
        
        actions.clear();
    }

    @Override
    public final void notifyActionEvent( ActionEvent event ) {
        performAction( event.actionId, event.entityId );
    }
    
    public final void performAction( int actionId, int entityId ) {
        Action action = actions.get( actionId );
        if ( action != null ) {
            action.performAction( entityId );
        }
    }

    public final ActionBuilder getActionBuilder() {
        return new ActionBuilder();
    }

    @Override
    public final SystemComponentKey[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter[] {
            new ActionBuilderAdapter( this )
        };
    };

    public final class ActionBuilder extends SystemComponentBuilder {
        
        protected ActionBuilder() {}
        
        @Override
        public final SystemComponentKey systemComponentKey() {
            return Action.TYPE_KEY;
        }

        public int doBuild( int componentId, Class<?> componentType ) {
            checkType( componentType );
            attributes.put( Component.INSTANCE_TYPE_NAME, componentType.getName() );
            Action result = getInstance( context, componentId );
            result.fromAttributes( attributes );
            actions.set( result.index(), result );
            
            postInit( result, context );
            
            return result.getId();
        }

    }

    public final class ActionBuilderAdapter extends SystemBuilderAdapter<Action> {
        
        protected ActionBuilderAdapter( ComponentSystem system ) {
            super( system, getActionBuilder() );
        }
        
        @Override
        public final SystemComponentKey componentTypeKey() {
            return Action.TYPE_KEY;
        }
        @Override
        public Action get( int id, Class<? extends Action> subtype ) {
            return actions.get( id );
        }
        @Override
        public void delete( int id, Class<? extends Action> subtype ) {
            deleteAction( id );
        }
        @Override
        public final Iterator<Action> getAll() {
            return actions.iterator();
        }
    }

}
