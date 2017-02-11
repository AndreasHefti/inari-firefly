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
package com.inari.firefly.graphics.view;

import java.util.ArrayDeque;

import com.inari.commons.event.Event;

public final class ViewEvent extends Event<ViewEventListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( ViewEvent.class );
    
    private static final ArrayDeque<ViewEvent> POOL = new ArrayDeque<ViewEvent>( 2 );
    
    public static enum Type {
        VIEW_CREATED,
        VIEW_ACTIVATED,
        VIEW_DISPOSED,
        VIEW_DELETED
    }
    
    View view;
    Type eventType;
    
    ViewEvent() {
        super( TYPE_KEY );
    }
    
    public final View getView() {
        return view;
    }
    
    public final Type getType() {
        return eventType;
    }
    
    public final boolean isOfType( Type type ) {
        return eventType == type;
    }

    @Override
    protected final void notify( ViewEventListener listener ) {
        listener.onViewEvent( this );
    }
    
    @Override
    protected final void restore() {
        eventType = null;
        view = null;
        
        POOL.addLast( this );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "ViewEvent [eventType=" );
        builder.append( eventType );
        builder.append( ", view=" );
        builder.append( view.index() );
        builder.append( "]" );
        return builder.toString();
    }
    
    public static final ViewEvent create( final Type type, final View view ) {
        final ViewEvent result;
        if ( POOL.isEmpty() ) {
            result = new ViewEvent();
            POOL.addLast( result );
        } else {
            result = POOL.removeLast();
        }

        result.eventType = type;
        result.view = view;
        
        return result;
    }
}
