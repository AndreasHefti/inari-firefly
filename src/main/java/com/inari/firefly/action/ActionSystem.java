package com.inari.firefly.action;

import java.util.HashSet;
import java.util.Set;

import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.action.event.ActionEvent;
import com.inari.firefly.action.event.ActionEventListener;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.ComponentBuilderHelper;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;

public final class ActionSystem 
    implements
        ComponentSystem,
        ActionEventListener {
    
    public static final TypedKey<ActionSystem> CONTEXT_KEY = TypedKey.create( "FF_ACTION_SYSTEM", ActionSystem.class );
    
    private FFContext context;
    
    private DynArray<Action> actions;
    
    ActionSystem() {
        actions = new DynArray<Action>();
    }
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        this.context = context;
        
        context.getComponent( FFContext.EVENT_DISPATCHER ).register( ActionEvent.class, this );
    }

    @Override
    public final void dispose( FFContext context ) {
        context.getComponent( FFContext.EVENT_DISPATCHER ).unregister( ActionEvent.class, this );
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

    @Override
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public final <C> ComponentBuilder<C> getComponentBuilder( Class<C> actionType ) {
        if ( Action.class.isAssignableFrom( actionType ) ) {
            return new ActionBuilder( this, actionType );
        }
        
        throw new IllegalArgumentException( "Unsupported Component type for ActionSystem Builder. Type: " + actionType );
    }
    
    public final <A extends Action> ActionBuilder<A> getActionBuilder( Class<A> actionType ) {
        return new ActionBuilder<A>( this, actionType );
    }

    private static final Set<Class<?>> SUPPORTED_COMPONENT_TYPES = new HashSet<Class<?>>();
    @Override
    public final Set<Class<?>> supportedComponentTypes() {
        if ( SUPPORTED_COMPONENT_TYPES.isEmpty() ) {
            SUPPORTED_COMPONENT_TYPES.add( Action.class );
        }
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final void fromAttributes( Attributes attributes ) {
        fromAttributes( attributes, BuildType.CLEAR_OLD );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public final void fromAttributes( Attributes attributes, BuildType buildType ) {
        if ( buildType == BuildType.CLEAR_OLD ) {
            clear();
        }
        for ( Class<? extends Action> actionSubType : attributes.getAllSubTypes( Action.class ) ) {
            new ComponentBuilderHelper<Action>() {
                @Override
                public Action get( int id ) {
                    return actions.get( id );
                }
                @Override
                public void delete( int id ) {
                    deleteAction( id );
                }
            }.buildComponents( Action.class, buildType, (ActionBuilder<Action>) getActionBuilder( actionSubType ), attributes );
        }
    }

    @Override
    public final void toAttributes( Attributes attributes ) {
        ComponentBuilderHelper.toAttributes( attributes, Action.class, actions );
    }

    public final class ActionBuilder<C extends Action> extends BaseComponentBuilder<C> {
        
        private final Class<C> actionType;

        protected ActionBuilder( ComponentBuilderFactory componentFactory, Class<C> actionType ) {
            super( componentFactory );
            this.actionType = actionType;
        }

        @Override
        public C build( int componentId ) {
            attributes.put( Component.INSTANCE_TYPE_NAME, actionType.getName() );
            C result = getInstance( context, componentId );
            result.fromAttributes( attributes );
            actions.set( result.index(), result );
            
            postInit( result, context );
            
            return result;
        }
    }

    

}
