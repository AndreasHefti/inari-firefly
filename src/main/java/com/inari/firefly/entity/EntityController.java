package com.inari.firefly.entity;

import com.inari.commons.event.IEventDispatcher;
import com.inari.firefly.FFContext;
import com.inari.firefly.control.Controller;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.entity.event.EntityActivationListener;


public abstract class EntityController extends Controller implements EntityActivationListener {
    
    protected IEventDispatcher eventDispatcher;
    protected IEntitySystem entitySystem;
    
    protected EntityController( int id, FFContext context ) {
        super( id );
        eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
        entitySystem = context.get( FFContext.System.ENTITY_SYSTEM );
        
        eventDispatcher.register( EntityActivationEvent.class, this );
    }

    @Override
    public final void dispose( FFContext context ) {
        eventDispatcher.unregister( EntityActivationEvent.class, this );
    }

    @Override
    public final void onEntityActivationEvent( EntityActivationEvent event ) {
        switch ( event.type ) {
            case ENTITY_ACTIVATED: {
                if ( hasControllerId( event.entityId, indexedId ) ) {
                    componentIds.add( event.entityId );
                }
                break;
            } 
            case ENTITY_DEACTIVATED: {
                componentIds.remove( event.entityId );
                break;
            }
            default: {}
        }
    }

    @Override
    public final void update( long time ) {
        for ( int i = 0; i < componentIds.length(); i++ ) {
            int entityId = componentIds.get( i );
            if ( entityId >= 0 ) {
                update( time, entityId );
            }
        }
    }

    protected abstract int getControlledComponentTypeId();
    
    protected abstract void update( long time, int entityId );
    
    private final boolean hasControllerId( int entityId, int controllerId ) {
        EntityComponent component = entitySystem.getComponent( entityId, getControlledComponentTypeId() );
        if ( component == null ) {
            return false;
        }
        
        return component.getControllerId() == controllerId;
    }

}