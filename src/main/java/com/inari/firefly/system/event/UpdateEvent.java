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
