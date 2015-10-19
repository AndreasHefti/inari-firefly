package com.inari.firefly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.inari.commons.event.IEventDispatcher;
import com.inari.firefly.asset.event.AssetEvent;
import com.inari.firefly.renderer.SpriteRenderable;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.LowerSystemFacade;
import com.inari.firefly.system.view.View;
import com.inari.firefly.system.view.event.ViewEvent;

public class LowerSystemFacadeMock implements LowerSystemFacade {
    
    private final Collection<String> loadedAssets = new ArrayList<String>();
    private final Collection<String> views = new ArrayList<String>();
    private final Collection<String> sounds = new ArrayList<String>();
    
    private final Collection<String> log = new ArrayList<String>();
    
    private IEventDispatcher eventDispatcher;
    
    @Override
    public void init( FFContext context ) {
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        
        eventDispatcher.register( AssetEvent.class, this );
        eventDispatcher.register( ViewEvent.class, this );
    }
    
    @Override
    public void dispose( FFContext context ) {
        eventDispatcher.unregister( AssetEvent.class, this );
        eventDispatcher.unregister( ViewEvent.class, this );
        
        clear();
    }

    public void clear() {
        loadedAssets.clear();
        views.clear();
        sounds.clear();
        log.clear();
    }

    @Override
    public void onAssetEvent( AssetEvent event ) {
        switch ( event.eventType ) {
            case ASSET_LOADED: {
                loadedAssets.add( event.asset.getName() );
                break;
            }
            case ASSET_DISPOSED: 
            case ASSET_DELETED: {
                loadedAssets.remove( event.asset.getName() );
            }
            default: {}
        }
    }

    @Override
    public void onViewEvent( ViewEvent event ) {
        switch ( event.eventType ) {
            case VIEW_CREATED: {
                views.add( event.view.getName() );
                break;
            }
            case VIEW_DELETED: {
                views.remove( event.view.getName() );
            }
            default: {}
        }
    }

    @Override
    public long playSound( int soundId, int chanel, boolean looping, float volume, float pitch, float pan ) {
        log.add( "playSound" );
        return soundId;
    }

    @Override
    public void changeSound( int soundId, long instanceId, float volume, float pitch, float pan ) {
        log.add( "changeSound" );
    }

    @Override
    public void stopSound( int soundId, long instanceId ) {
        log.add( "stopSound" );
    }

    @Override
    public void playMusic( int soundId, boolean looping, float volume, float pan ) {
        log.add( "playMusic" );
    }

    @Override
    public void changeMusic( int soundId, float volume, float pan ) {
        log.add( "changeMusic" );
    }

    @Override
    public void stopMusic( int soundId ) {
        log.add( "stopMusic" );
    }

    @Override
    public void startRendering( View view ) {
        log.add( "startRendering::View(" + view.getName() + ")" );
    }
    
    @Override
    public void renderSprite( SpriteRenderable spriteRenderable, float xpos, float ypos ) {
        log.add( "renderSprite::Sprite(" + spriteRenderable.getSpriteId() + ")" );
    }

    @Override
    public void renderSprite( SpriteRenderable spriteRenderable, float x, float y, float pivotx, float pivoty, float scalex, float scaley,float rotation ) {
        log.add( "renderSprite::Sprite(" + spriteRenderable.getSpriteId() + ")" );
    }

    @Override
    public void endRendering( View view ) {
        log.add( "endRendering::View(" + view.getName() + ")" );
    }

    @Override
    public void flush( Iterator<View> virtualViews ) {
        log.add( "flush" );
    }

    @Override
    public int getScreenWidth() {
        return 100;
    }

    @Override
    public int getScreenHeight() {
        return 100;
    }
    
    public String loadedAssets() {
        return loadedAssets.toString();
    }
    
    public String views() {
        return views.toString();
    }
    
    public String sounds() {
        return sounds.toString();
    }
    
    public String log() {
        return log.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "LowerSystemFacadeMock [loadedAssets=" );
        builder.append( loadedAssets );
        builder.append( ", views=" );
        builder.append( views );
        builder.append( ", sounds=" );
        builder.append( sounds );
        builder.append( ", log=" );
        builder.append( log );
        builder.append( "]" );
        return builder.toString();
    }

}
