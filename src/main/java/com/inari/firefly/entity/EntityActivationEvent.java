/*******************************************************************************
 * Copyright (c) 2015 - 2016, Andreas Hefti, inarisoft@yahoo.de 
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

import java.util.ArrayDeque;

import com.inari.commons.event.AspectedEvent;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.firefly.graphics.view.ViewEvent;

public final class EntityActivationEvent extends AspectedEvent<EntityActivationListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( EntityActivationEvent.class );
    
    private static final ArrayDeque<EntityActivationEvent> POOL = new ArrayDeque<EntityActivationEvent>( 2 );
    
    public enum Type {
        ENTITY_ACTIVATED,
        ENTITY_DEACTIVATED
    }
    
    int entityId;
    final Aspects entityComponentAspects = EntityComponent.ASPECT_TYPE.createAspects();
    Type eventType;
    
    EntityActivationEvent() {
        super( TYPE_KEY );
    }

    @Override
    public final Aspects getAspects() {
        return entityComponentAspects;
    }

    @Override
    protected final void notify( final EntityActivationListener listener ) {
        switch( eventType ) {
            case ENTITY_ACTIVATED: {
                listener.entityActivated( entityId, entityComponentAspects );
                break;
            }
            case ENTITY_DEACTIVATED: {
                listener.entityDeactivated( entityId, entityComponentAspects );
            }
        }
    }

    @Override
    protected final void restore() {
        entityId = -1;
        eventType = null;
        entityComponentAspects.clear();
        
        POOL.addLast( this );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "EntityActivationEvent [eventType=" );
        builder.append( eventType );
        builder.append( ", entityId=" );
        builder.append( entityId );
        builder.append( "]" );
        return builder.toString();
    }
    
    public static final EntityActivationEvent create( int entityId, Type eventType, Aspects entityComponentAspects ) {
        final EntityActivationEvent result;
        if ( POOL.isEmpty() ) {
            result = new EntityActivationEvent();
            POOL.addLast( result );
        } else {
            result = POOL.removeLast();
        }
        
        result.entityId = entityId;
        result.eventType = eventType;
        result.entityComponentAspects.set( entityComponentAspects );
        
        return result;
    }

}
