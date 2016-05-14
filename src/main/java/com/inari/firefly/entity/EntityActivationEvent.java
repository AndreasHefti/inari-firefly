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

import com.inari.commons.event.AspectedEvent;
import com.inari.commons.lang.aspect.Aspects;

public final class EntityActivationEvent extends AspectedEvent<EntityActivationListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( EntityActivationEvent.class );
    
    public enum Type {
        ENTITY_ACTIVATED,
        ENTITY_DEACTIVATED
    }
    
    public final int entityId;
    public final Aspects entityComponentAspects;
    public final Type eventType;
    
    public EntityActivationEvent( int entityId, Aspects entityComponentAspects, Type eventType ) {
        super( TYPE_KEY );
        this.entityId = entityId;
        this.entityComponentAspects = entityComponentAspects;
        this.eventType = eventType;
    }

    @Override
    public final Aspects getAspects() {
        return entityComponentAspects;
    }

    @Override
    protected final void notify( EntityActivationListener listener ) {
        listener.onEntityActivationEvent( this );
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

}
