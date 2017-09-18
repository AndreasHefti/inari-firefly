package com.inari.firefly.control.action;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.firefly.FFInitException;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;
import com.inari.firefly.system.component.SystemComponentMap;

public class EntityActionSystem extends ComponentSystem<EntityActionSystem> implements EntityActionEventListener {
    
    public static final FFSystemTypeKey<EntityActionSystem> SYSTEM_KEY = FFSystemTypeKey.create( EntityActionSystem.class );
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        Action.TYPE_KEY
    );

    private final SystemComponentMap<Action> actions;
    
    EntityActionSystem() {
        super( SYSTEM_KEY );
        actions = new SystemComponentMap<>( this, Action.TYPE_KEY, 20, 10 );
    }

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        context.registerListener( EntityActionEvent.TYPE_KEY, this );
    }
    
    public final void clearSystem() {
        actions.clear();
    }
    
    public final void performAction( int actionId, int entityId ) {
        Action action = actions.get( actionId );
        if ( action != null ) {
            action.action( entityId );
        }
    }
    
    public void dispose( FFContext context ) {
        clearSystem();
        
        context.disposeListener( EntityActionEvent.TYPE_KEY, this );
    }

    public Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    public Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            actions.getBuilderAdapter()
        );
    }
    
    public final SystemComponentBuilder getActionBuilder( Class<? extends Action> componentType ) {
        if ( componentType == null ) {
            throw new IllegalArgumentException( "componentType is needed for SystemComponentBuilder for component: " + Action.TYPE_KEY.name() );
        }
        
        return actions.getBuilder( componentType );
    }
}
