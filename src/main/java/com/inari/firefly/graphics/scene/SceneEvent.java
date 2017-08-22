package com.inari.firefly.graphics.scene;

import com.inari.commons.event.Event;
import com.inari.commons.lang.functional.Callback;
import com.inari.firefly.system.FFContext;

public final class SceneEvent extends Event<SceneEventListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( SceneEvent.class );
    private static final SceneEvent SINGLETON_EVENT = new SceneEvent();
    
    private Type type;
    private String sceneName;
    private Callback callback;

    public static enum Type {
        RUN,
        PAUSE,
        PAUSE_ALL,
        RESUME,
        RESUME_ALL,
        STOP,
        STOP_ALL
    }

    private SceneEvent() {
        super( TYPE_KEY );
        restore();
    }
    
    @Override
    protected final void restore() {
        type = null;
        sceneName = null;
        callback = null;
    }

    @Override
    protected final void notify( SceneEventListener listener ) {
        switch ( type ) {
            case RUN: listener.runScene( sceneName, callback ); break;
            case PAUSE: listener.pauseScene( sceneName ); break;
            case PAUSE_ALL: listener.pauseAll(); break;
            case RESUME: listener.resumeScene( sceneName ); break;
            case RESUME_ALL: listener.resumeAll();
            case STOP: listener.stopScene( sceneName ); break;
            case STOP_ALL: listener.stopAll(); break;
        }
    }
    
    /** NOT THREAD SAVE */
    public static final void notify( FFContext context, Type type, String sceneName ) {
        notify( context, type, sceneName, null );
    }
    
    /** NOT THREAD SAVE */
    public static final void notify( FFContext context, Type type, String sceneName, Callback callback ) {
        SINGLETON_EVENT.type = type;
        SINGLETON_EVENT.sceneName = sceneName;
        SINGLETON_EVENT.callback = callback;
        
        context.notify( SINGLETON_EVENT );
    }

}
