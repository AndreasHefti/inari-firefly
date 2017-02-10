package com.inari.firefly;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.inari.commons.geom.Rectangle;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.TextureAsset;
import com.inari.firefly.graphics.sprite.ESprite;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.system.external.FFGraphics;

public class SystemTests extends FFTest {
    
    private static final String TEXTURE_ASSET_NAME = "boulderDashTextureAsset";
    private static final String SPRITE_ASSET_NAME = "spriteAsset";
    
    @Test
    public void createTextureAndSpriteAndOneEntity() {
        AssetSystem assetSystem = ffContext.getSystem( AssetSystem.SYSTEM_KEY );
        EntitySystem entitySystem = ffContext.getSystem( EntitySystem.SYSTEM_KEY );
        FFGraphics lowerSystemMock = ffContext.getGraphics();
        
        assetSystem
            .getAssetBuilder( TextureAsset.class )
                .set( TextureAsset.NAME, TEXTURE_ASSET_NAME )
                .set( TextureAsset.RESOURCE_NAME, "origTiles.png" )
            .build();
         assetSystem
            .getAssetBuilder( SpriteAsset.class )
                .set( SpriteAsset.NAME, SPRITE_ASSET_NAME )
                .set( SpriteAsset.TEXTURE_ASSET_ID, assetSystem.getAssetId( TEXTURE_ASSET_NAME ) )
                .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 0, 32, 32 ) )
            .build()
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
                .set( ESprite.SPRITE_ID, assetSystem.getAssetInstanceId( SPRITE_ASSET_NAME ) )
            .build()
            ;
        
        entitySystem.activateEntity( entityId );
        
        assertEquals( 
            "LowerSystemFacadeMock ["
            + "loadedAssets=[origTiles.png,sprite:0 : [x=0,y=0,width=32,height=32]], "
            + "views=[BASE_VIEW], "
            + "log=[]]", 
            lowerSystemMock.toString() 
        );
        
        firefly.update();
        firefly.render();
        
        assertEquals( 
            "LowerSystemFacadeMock ["
            + "loadedAssets=[origTiles.png,sprite:0 : [x=0,y=0,width=32,height=32]], "
            + "views=[BASE_VIEW], "
            + "log=[startRendering::View(BASE_VIEW), renderSprite::Sprite(1), endRendering::View(BASE_VIEW), flush]]", 
            lowerSystemMock.toString() 
        );
    }

}
