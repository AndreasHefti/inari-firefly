package com.inari.firefly.system;

import com.inari.commons.event.Event;

public final class PostRenderEvent extends Event<PostRenderEventListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( PostRenderEvent.class );

    private final FFContext context;
    
    PostRenderEvent( FFContext context ) {
        super( TYPE_KEY );
        this.context = context;
    }

    @Override
    protected void notify( PostRenderEventListener listener ) {
        listener.postRendering( context );
    }

}
