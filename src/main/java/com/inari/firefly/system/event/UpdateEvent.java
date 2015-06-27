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

public final class UpdateEvent extends Event<UpdateEventListener>{
    
    /** Use this if the game loop works with elapsed time on update */
    private long timeElapsed;
    private long update = 0;

    public final long getTimeElapsed() {
        return timeElapsed;
    }

    public final void setTimeElapsed( long timeElapsed ) {
        this.timeElapsed = timeElapsed;
    }

    public final long getUpdate() {
        return update;
    }

    public final void setUpdate( long update ) {
        this.update = update;
    }

    @Override
    public final void notify( UpdateEventListener listener ) {
        listener.update( this );
    }

}
