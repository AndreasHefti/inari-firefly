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
package com.inari.firefly.component.event;

import com.inari.commons.event.Event;
import com.inari.firefly.component.ComponentSystem;

public final class ComponentSystemEvent extends Event<ComponentSystemEventListener> {
    
    public enum Type {
        INITIALISED,
        DISPOSED
    }
    
    public final Type type;
    public final ComponentSystem componentSystem;
    
    public ComponentSystemEvent( Type type, ComponentSystem componentSystem ) {
        this.type = type;
        this.componentSystem = componentSystem;
    }

    @Override
    public final void notify( ComponentSystemEventListener listener ) {
        listener.onComponentSystemEvent( this );
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "ComponentSystemEvent [type=" );
        builder.append( type );
        builder.append( ", componentSystem=" );
        builder.append( componentSystem.getClass().getSimpleName() );
        builder.append( "]" );
        return builder.toString();
    }

}
