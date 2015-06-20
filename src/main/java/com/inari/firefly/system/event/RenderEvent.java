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
