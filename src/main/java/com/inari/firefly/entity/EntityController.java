/*******************************************************************************
 * Copyright (c) 2015, Andreas Hefti, inarisoft@yahoo.de 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/ 
package com.inari.firefly.entity;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.aspect.IndexedAspect;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.control.Controller;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.entity.event.EntityActivationListener;


public abstract class EntityController extends Controller implements EntityActivationListener {
    
    protected IEventDispatcher eventDispatcher;
    protected EntitySystem entitySystem;
    
    protected EntityController( int id, FFContext context ) {
        super( id );
        eventDispatcher = context.get( FFContext.EVENT_DISPATCHER );
        entitySystem = context.get( FFContext.System.ENTITY_SYSTEM );
        
        eventDispatcher.register( EntityActivationEvent.class, this );
    }

    @Override
    public final void dispose( FFContext context ) {
        eventDispatcher.unregister( EntityActivationEvent.class, this );
    }

    @Override
    public final void onEntityActivationEvent( EntityActivationEvent event ) {
        switch ( event.eventType ) {
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
    
    @Override
    public final boolean match( IndexedAspect aspect ) {
        return aspect.contains( getControlledComponentTypeId() );
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
