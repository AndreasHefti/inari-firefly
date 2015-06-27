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
package com.inari.firefly.system.event;

import com.inari.commons.event.Event;
import com.inari.firefly.system.View;

public final class ViewEvent extends Event<ViewEventListener> {
    
    public static enum Type {
        VIEW_CREATED,
        VIEW_ACTIVATED,
        VIEW_DISPOSED,
        VIEW_DELETED
    }
    
    public final View view;
    public final Type type;

    public ViewEvent( View view, Type type ) {
        this.type = type;
        this.view = view;
    }

    @Override
    public final void notify( ViewEventListener listener ) {
        listener.onViewEvent( this );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "ViewEvent [type=" );
        builder.append( type );
        builder.append( ", view=" );
        builder.append( view.indexedId() );
        builder.append( "]" );
        return builder.toString();
    }

    

}
