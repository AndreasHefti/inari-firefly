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
package com.inari.firefly.system;

import com.inari.commons.event.Event;

public final class UpdateEvent extends Event<UpdateEventListener>{

    long lastUpdateTime = 0;
    long time = 0;
    long timeElapsed = 0;
    long update = 0;

    public UpdateEvent( long systemTime, long time, long timeElapsed, long update ) {
        this.lastUpdateTime = systemTime;
        this.time = time;
        this.timeElapsed = timeElapsed;
        this.update = update;
    }

    public final long getTime() {
        return time;
    }

    public final long systemTime() {
        return lastUpdateTime;
    }

    /** Use this if the game loop works with elapsed time on update */
    public final long getTimeElapsed() {
        return timeElapsed;
    }

    public final long getUpdate() {
        return update;
    }

    @Override
    public final void notify( UpdateEventListener listener ) {
        listener.update( this );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "UpdateEvent [update=" );
        builder.append( update );
        builder.append( ", timeElapsed=" );
        builder.append( timeElapsed );
        builder.append( "]" );
        return builder.toString();
    }

}
