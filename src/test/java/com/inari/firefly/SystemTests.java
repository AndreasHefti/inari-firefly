package com.inari.firefly;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.inari.commons.event.EventDispatcher;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FireFly;
import com.inari.firefly.system.external.FFGraphics;

public class SystemTests {
    
    private static final String TEXTURE_ASSET_NAME = "boulderDashTextureAsset";
    private static final String SPRITE_ASSET_NAME = "spriteAsset";
    
    @Test
    public void createTextureAndSpriteAndOneEntity() {
        Indexer.clear();
        FireFly firefly = new FireFlyMock( new EventDispatcher() );
        FFContext context = firefly.getContext();
        AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
        EntitySystem entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        FFGraphics lowerSystemMock = context.getGraphics();
        
        assetSystem
            .getAssetBuilder()
                .set( TextureAsset.NAME, TEXTURE_ASSET_NAME )
                .set( TextureAsset.RESOURCE_NAME, "origTiles.png" )
            .buildAndNext( TextureAsset.class )
                .set( SpriteAsset.NAME, SPRITE_ASSET_NAME )
                .set( SpriteAsset.TEXTURE_ASSET_ID, assetSystem.getAssetId( TEXTURE_ASSET_NAME ) )
                .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 0, 32, 32 ) )
            .build( SpriteAsset.class )
            ;
        
        assertEquals( 
            "LowerSystemFacadeMock [" +
            "loadedAssets=[], " +
            "views=[BASE_VIEW], " +
            "log=[]]", 
            lowerSystemMock.toString() 
        );
        
        assetSystem.loadAsset( TEXTURE_ASSET_NAME );
        assetSystem.loadAsset( SPRITE_ASSET_NAME );
          
        int entityId = entitySystem
            .getEntityBuilder()
                .set( ETransform.VIEW_ID, 0 )
                .set( ETransform.XPOSITION, 0 )
                .set( ETransform.XPOSITION, 0 )
                .set( ESprite.SPRITE_ID, assetSystem.getAssetInstanceId( SPRITE_ASSET_NAME ) )
            .build()
            ;
        
        entitySystem.activateEntity( entityId );
        
        assertEquals( 
            "LowerSystemFacadeMock [" +
            "loadedAssets=[boulderDashTextureAsset,spriteAsset], " +
            "views=[BASE_VIEW], " +
            "log=[]]", 
            lowerSystemMock.toString() 
        );
        
        firefly.update();
        firefly.render();
        
        assertEquals( 
            "LowerSystemFacadeMock [" +
            "loadedAssets=[boulderDashTextureAsset,spriteAsset], " +
            "views=[BASE_VIEW], " +
            "log=[startRendering::View(BASE_VIEW), renderSprite::Sprite(1), endRendering::View(BASE_VIEW), flush]]", 
            lowerSystemMock.toString() 
        );
    }

}
