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
package com.inari.firefly.system;

import com.inari.commons.event.Event;
import com.inari.commons.geom.Rectangle;

public final class RenderEvent extends Event<RenderEventListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( RenderEvent.class );

    /** Use this if the game loop works with approximation time on rendering */
    long approximationTime;
    /** Defines current View for rendering */
    int viewId;
    
    int layerId;
    /** Defines a clipping area */
    final Rectangle clip = new Rectangle();
    
    public RenderEvent() {
        super( TYPE_KEY );
    }
    
    public final long getApproximationTime() {
        return approximationTime;
    }
    
    public final int getViewId() {
        return viewId;
    }

    public final int getLayerId() {
        return layerId;
    }

    public final Rectangle getClip() {
        return clip;
    }

    @Override
    public final void notify( RenderEventListener listener ) {
        listener.render( this );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "RenderEvent [viewId=" );
        builder.append( viewId );
        builder.append( ", clip=" );
        builder.append( clip );
        builder.append( ", approximationTime=" );
        builder.append( approximationTime );
        builder.append( "]" );
        return builder.toString();
    }

}
