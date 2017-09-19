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
package com.inari.firefly.physics.movement;

import com.inari.commons.event.Event;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.system.FFContext;

public final class MoveEvent extends Event<MoveEventListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( MoveEvent.class );
    private static final MoveEvent SINGLETON_EVENT = new MoveEvent();
    
    private IntBag entityIds;
    
    private MoveEvent() {
        super( TYPE_KEY );
    }
    
    void add( int entityId ) {
        entityIds.add( entityId );
    }
    
    public final IntBag movedEntityIds() {
        return entityIds;
    }

    protected final void notify( MoveEventListener listener ) {
        listener.onMoveEvent( this );
    }

    @Override
    protected final void restore() {
        entityIds = null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "MoveEvent [entityIds=" );
        builder.append( entityIds );
        builder.append( "]" );
        return builder.toString();
    }
    
    // NOTE: this is not thread save
    public final static FFContext notify( final FFContext context, final IntBag entityIds ) {
        SINGLETON_EVENT.entityIds = entityIds;
        return context.notify( SINGLETON_EVENT );
    }

}
