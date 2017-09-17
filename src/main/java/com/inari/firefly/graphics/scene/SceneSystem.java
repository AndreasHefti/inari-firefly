package com.inari.firefly.graphics.scene;

import java.util.Iterator;
import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.functional.Callback;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.external.FFTimer;
import com.inari.firefly.system.component.SystemComponentBuilder;

public final class SceneSystem extends ComponentSystem<SceneSystem> implements SceneEventListener, UpdateEventListener {
    
    public static final FFSystemTypeKey<SceneSystem> SYSTEM_KEY = FFSystemTypeKey.create( SceneSystem.class );
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        Scene.TYPE_KEY
    );
    
    private DynArray<Scene> scenes;
    private DynArray<Scene> activeScenes;

    protected SceneSystem() {
        super( SYSTEM_KEY );
    }

    @Override
    public void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        scenes = DynArray.create( Scene.class );
        activeScenes = DynArray.create( Scene.class );
        
        context.registerListener( UpdateEvent.TYPE_KEY, this );
        context.registerListener( SceneEvent.TYPE_KEY, this );
    }

    public final Scene getScene( int id ) {
        return scenes.get( id );
    }

    public final int getSceneId( String name ) {
        if ( name == null ) {
            return -1;
        }
        
        for ( int i = 0; i < scenes.capacity(); i++ ) {
            Scene scene = scenes.get( i );
            if ( scene == null ) {
                continue;
            }
            
            if ( name.equals( scene.getName() ) ) {
                return scene.index();
            }
        }
        
        return -1;
    }

    @Override
    public final void runScene( String sceneName, final Callback callback ) {
        Scene scene = getScene( getSceneId( sceneName ) );
        if ( scene != null && !scene.running ) {
            scene.running = true;
            scene.paused = false;
            scene.callback = callback;
            scene.run( context );
            activeScenes.add( scene );
        }
    }

    @Override
    public final void pauseScene( String sceneName ) {
        Scene scene = getScene( getSceneId( sceneName ) );
        if ( scene != null && scene.running ) {
            scene.paused = true;
        }
    }

    @Override
    public final void pauseAll() {
        for ( int i = 0; i < activeScenes.capacity(); i++ ) {
            Scene scene = activeScenes.get( i );
            if ( scene == null ) {
                continue;
            }
            
            scene.paused = true;
        }
        
    }
    
    @Override
    public final void resumeScene( String sceneName ) {
        Scene scene = getScene( getSceneId( sceneName ) );
        if ( scene != null && scene.running && scene.paused ) {
            scene.paused = false;
        }
    }

    @Override
    public final void resumeAll() {
        for ( int i = 0; i < activeScenes.capacity(); i++ ) {
            Scene scene = activeScenes.get( i );
            if ( scene == null ) {
                continue;
            }
            
            scene.paused = false;
        }
    }

    @Override
    public final void stopScene( String sceneName ) {
        stopScene( getScene( getSceneId( sceneName ) ) );
    }

    @Override
    public final void stopAll() {
        for ( int i = 0; i < activeScenes.capacity(); i++ ) {
            Scene scene = activeScenes.get( i );
            if ( scene == null ) {
                continue;
            }
            
            stopScene( scene );
        }
    }

    @Override
    public final void update( final FFTimer timer ) {
        for ( int i = 0; i < activeScenes.capacity(); i++ ) {
            Scene scene = activeScenes.get( i );
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
            deleteScene( scene.index() );
        }
        
        activeScenes.remove( scene );
    }
    
    public final SystemComponentBuilder getSceneBuilder( Class<? extends Scene> type ) {
        if ( type == null ) {
            throw new IllegalArgumentException( "componentType is needed for SystemComponentBuilder for component: " + Scene.TYPE_KEY.name() );
        }
        
        return new SceneBuilder( type );
    }
    
    @Override
    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            new SceneBuilderAdapter()
        );
    }
    
    public final void deleteScene( int index ) {
        Scene removed = scenes.remove( index );
        if ( removed.running ) {
            activeScenes.remove( removed );
        }
        
        removed.dispose( context );
    }
    
    @Override
    public final  void clearSystem() {
        for ( int i = 0; i < scenes.capacity(); i++ ) {
            if ( scenes.contains( i ) ) {
                deleteScene( i );
            }
        }
        
        scenes.clear();
        activeScenes.clear();
    }

    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( UpdateEvent.TYPE_KEY, this );
        context.disposeListener( SceneEvent.TYPE_KEY, this );
        clearSystem();
    }
    
    private final class SceneBuilder extends SystemComponentBuilder {
        
        private SceneBuilder( Class<? extends Scene> componentType ) {
            super( context, componentType );
        }
        
        @Override
        public final SystemComponentKey<Scene> systemComponentKey() {
            return Scene.TYPE_KEY;
        }

        @Override
        public int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            Scene scene = createSystemComponent( componentId, componentType, context );
            scenes.set( scene.index(), scene );
            return scene.index();
        }
    }
    
    private final class SceneBuilderAdapter extends SystemBuilderAdapter<Scene> {
        private SceneBuilderAdapter() {
            super( SceneSystem.this, Scene.TYPE_KEY );
        }
        @Override
        public final SystemComponentBuilder createComponentBuilder( Class<? extends Scene> type ) {
            return getSceneBuilder( type );
        }
        @Override
        public final Scene get( int id ) {
            return getScene( id );
        }
        @Override
        public final void delete( int id ) {
            deleteScene( id );
        }
        @Override
        public final Iterator<Scene> getAll() {
            return scenes.iterator();
        }
        @Override
        public final int getId( String name ) {
            return getSceneId( name );
        }
        @Override
        public final void activate( int id ) {
            throw new UnsupportedOperationException();
        }
        @Override
        public final void deactivate( int id ) {
            throw new UnsupportedOperationException();
        }
    }

}
