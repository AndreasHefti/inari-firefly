package com.inari.firefly;

import java.util.ArrayList;
import java.util.Collection;

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.asset.event.AssetEvent;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.sprite.SpriteRenderable;
import com.inari.firefly.system.ILowerSystemFacade;
import com.inari.firefly.system.Input;
import com.inari.firefly.system.View;
import com.inari.firefly.system.event.ViewEvent;

public class LowerSystemFacadeMock implements ILowerSystemFacade {
    
    private static final Input INPUT = new Input() {};
    private final DynArray<Asset> loadedAssets = new DynArray<Asset>();
    private final DynArray<View> views = new DynArray<View>();
    private final DynArray<Sound> sounds = new DynArray<Sound>();
    
    private final Collection<String> log = new ArrayList<String>();
    

    @Override
    public void onAssetEvent( AssetEvent event ) {
        switch ( event.eventType ) {
            case ASSET_LOADED: {
                loadedAssets.add( event.asset );
                break;
            }
            case ASSET_DISPOSED: 
            case ASSET_DELETED: {
                loadedAssets.remove( event.asset );
            }
            default: {}
        }
    }

    @Override
    public void onViewEvent( ViewEvent event ) {
        switch ( event.eventType ) {
            case VIEW_ACTIVATED: {
                views.add( event.view );
                break;
            }
            case VIEW_DISPOSED: 
            case VIEW_DELETED: {
                views.remove( event.view );
            }
            default: {}
        }
    }
    
    @Override
    public void onSoundEvent( SoundEvent event ) {
        switch ( event.eventType ) {
            case PLAY_SOUND: {
                sounds.add( event.sound );
                break;
            }
            case STOP_PLAYING: {
                sounds.remove( event.sound );
            }
            default: {}
        }
    }

    @Override
    public void dispose( FFContext context ) {
        loadedAssets.clear();
        views.clear();
        sounds.clear();
        log.clear();
    }

    @Override
    public void startRendering( View view ) {
        log.add( "startRendering::View(" + view.index() + ")" );
    }

    @Override
    public void renderSprite( SpriteRenderable renderableSprite, ETransform transform ) {
        log.add( "renderSprite::Sprite(" + renderableSprite.getSpriteId() + ")" );
    }

    @Override
    public void renderSprite( SpriteRenderable renderableSprite, float xpos, float ypos ) {
        log.add( "renderSprite::Sprite(" + renderableSprite.getSpriteId() + ")" );
    }

    @Override
    public void endRendering( View view ) {
        log.add( "endRendering::View(" + view.index() + ")" );
    }
    
    @Override
    public void soundAttributesChanged( Sound sound ) {
        log.add( "soundAttributesChanged::Sound(" + sound.index() + ")" );
    }

    @Override
    public void flush() {
        // NOOP
    }

    @Override
    public int getScreenWidth() {
        return 100;
    }

    @Override
    public int getScreenHeight() {
        return 100;
    }

    @Override
    public void init( FFContext context ) {
        // NOOP
    }

    @Override
    public Input getInput() {
        return INPUT;
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
