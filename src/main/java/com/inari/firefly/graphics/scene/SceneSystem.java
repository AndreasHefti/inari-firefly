package com.inari.firefly.graphics.scene;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.functional.Callback;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.FFInitException;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentMap;
import com.inari.firefly.system.external.FFTimer;

public final class SceneSystem extends ComponentSystem<SceneSystem> implements SceneEventListener, UpdateEventListener {
    
    public static final FFSystemTypeKey<SceneSystem> SYSTEM_KEY = FFSystemTypeKey.create( SceneSystem.class );
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        Scene.TYPE_KEY
    );
    
    private SystemComponentMap<Scene> scenes;
    private IntBag activeScenes;

    protected SceneSystem() {
        super( SYSTEM_KEY );
    }

    @Override
    public void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        scenes = new SystemComponentMap<>( this, Scene.TYPE_KEY );
        activeScenes = new IntBag( 10, -1, 5 );
        
        context.registerListener( UpdateEvent.TYPE_KEY, this );
        context.registerListener( SceneEvent.TYPE_KEY, this );
    }
    
    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            scenes.getBuilderAdapter()
        );
    }

    public final void runScene( String sceneName, final Callback callback ) {
        final Scene scene = scenes.get( sceneName );
        if ( scene != null && !scene.running ) {
            scene.running = true;
            scene.paused = false;
            scene.callback = callback;
            scene.run( context );
            activeScenes.add( scene.index() );
        }
    }

    public final void pauseScene( String sceneName ) {
        final Scene scene = scenes.get( sceneName );
        if ( scene != null && scene.running ) {
            scene.paused = true;
        }
    }

    public final void pauseAll() {
        final IntIterator iterator = activeScenes.iterator();
        while( iterator.hasNext() ) {
            scenes.get( iterator.next() ).paused = true;
        }
    }
    
    public final void resumeScene( String sceneName ) {
        final Scene scene = scenes.get( sceneName );
        if ( scene != null && scene.running && scene.paused ) {
            scene.paused = false;
        }
    }

    public final void resumeAll() {
        final IntIterator iterator = activeScenes.iterator();
        while( iterator.hasNext() ) {
            scenes.get( iterator.next() ).paused = false;
        }
    }

    public final void stopScene( String sceneName ) {
        stopScene( scenes.get( sceneName ) );
    }

    public final void stopAll() {
        final IntIterator iterator = activeScenes.iterator();
        while( iterator.hasNext() ) {
            stopScene( scenes.get( iterator.next() ) );
        }
    }

    public final void update( final FFTimer timer ) {
        final IntIterator iterator = activeScenes.iterator();
        while( iterator.hasNext() ) {
            final Scene scene = scenes.get( iterator.next() );
            if ( scene == null ) {
                continue;
            }
            
            scene.update( context );
            
            if ( !scene.running ) {
                stopScene( scene );
            }
        }
    }

    private void stopScene( Scene scene ) {
        if ( scene == null ) {
            return;
        }
        
        scene.callback.callback();

        if ( scene.runAgain ) { 
            scene.callback = null;
            scene.running = false;
            scene.paused = false;
            scene.reset( context );
        } else {
            scenes.delete( scene.index() );
        }
        
        activeScenes.remove( scene.index() );
    }
    
    public final  void clearSystem() {
        scenes.clear();
        activeScenes.clear();
    }

    public final void dispose( FFContext context ) {
        context.disposeListener( UpdateEvent.TYPE_KEY, this );
        context.disposeListener( SceneEvent.TYPE_KEY, this );
        clearSystem();
    }

}
