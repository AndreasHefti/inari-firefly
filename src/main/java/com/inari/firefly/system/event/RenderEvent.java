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
import com.inari.commons.geom.Rectangle;

public final class RenderEvent extends Event<RenderEventListener> {

    /** Use this if the game loop works with approximation time on rendering */
    private long approximationTime;
    /** Defines current View for rendering */
    private int viewId;
    /** Defines a clipping area */
    private final Rectangle clip = new Rectangle();
    
    public final long getApproximationTime() {
        return approximationTime;
    }

    public final void setApproximationTime( long approximationTime ) {
        this.approximationTime = approximationTime;
    }
    
    public final int getViewId() {
        return viewId;
    }

    public final void setViewId( int viewId ) {
        this.viewId = viewId;
    }
    
    public Rectangle getClip() {
        return clip;
    }

    public void setClip( Rectangle clip ) {
        this.clip.fromOther( clip );
    }

    @Override
    public final void notify( RenderEventListener listener ) {
        listener.render( this );
    }

}
