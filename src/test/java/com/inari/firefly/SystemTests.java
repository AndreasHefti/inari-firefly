package com.inari.firefly;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.inari.commons.geom.Rectangle;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.Entity;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FireFly;
import com.inari.firefly.system.ILowerSystemFacade;

public class SystemTests {
    
    private static final AssetNameKey TEXTURE_ASSET_KEY = new AssetNameKey( "origTilesResource", "boulderDashTextureAsset" );
    private static final AssetNameKey SPRITE_ASSET_KEY = new AssetNameKey( "origTilesResource", "spriteAsset" );
    
    @Test
    public void createTextureAndSpriteAndOneEntity() {
        FireFly firefly = new FireFly( LowerSystemFacadeMock.class, InputMock.class );
        FFContext context = firefly.getContext();
        AssetSystem assetSystem = context.getComponent( FFContext.Systems.ASSET_SYSTEM );
        EntitySystem entitySystem = context.getComponent( FFContext.Systems.ENTITY_SYSTEM );
        ILowerSystemFacade lowerSystemMock = context.getComponent( FFContext.LOWER_SYSTEM_FACADE );
        
        assetSystem
            .getAssetBuilder( TextureAsset.class )
                .setAttribute( TextureAsset.NAME, TEXTURE_ASSET_KEY.name )
                .setAttribute( TextureAsset.ASSET_GROUP, TEXTURE_ASSET_KEY.group )
                .setAttribute( TextureAsset.RESOURCE_NAME, "origTiles.png" )
            .buildAndNext( SpriteAsset.class )
                .setAttribute( SpriteAsset.NAME, SPRITE_ASSET_KEY.name )
                .setAttribute( SpriteAsset.ASSET_GROUP, SPRITE_ASSET_KEY.group )
                .setAttribute( SpriteAsset.TEXTURE_ID, assetSystem.getAssetTypeKey( TEXTURE_ASSET_KEY ).id )
                .setAttribute( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 0, 32, 32 ) )
            .build()
            ;
          
        Entity entity = entitySystem
            .getEntityBuilder()
                .setAttribute( ETransform.VIEW_ID, 0 )
                .setAttribute( ETransform.XPOSITION, 0 )
                .setAttribute( ETransform.XPOSITION, 0 )
                .setAttribute( ESprite.SPRITE_ID, assetSystem.getAssetTypeKey( SPRITE_ASSET_KEY ).id )
            .build()
            ;
        
        assertEquals( 
            "LowerSystemFacadeMock [" +
            "loadedAssets=[], " +
            "views=[BASE_VIEW], " +
            "sounds=[], " +
            "log=[]]", 
            lowerSystemMock.toString() 
        );

        assetSystem.loadAsset( TEXTURE_ASSET_KEY );
        assetSystem.loadAsset( SPRITE_ASSET_KEY );
        entitySystem.activate( entity.getId() );
        
        assertEquals( 
            "LowerSystemFacadeMock [" +
            "loadedAssets=[boulderDashTextureAsset, spriteAsset], " +
            "views=[BASE_VIEW], " +
            "sounds=[], " +
            "log=[]]", 
            lowerSystemMock.toString() 
        );
        
        firefly.update();
        firefly.render();
        
        assertEquals( 
            "LowerSystemFacadeMock [" +
            "loadedAssets=[boulderDashTextureAsset, spriteAsset], " +
            "views=[BASE_VIEW], " +
            "sounds=[], " +
            "log=[startRendering::View(BASE_VIEW), renderSprite::Sprite(0), endRendering::View(BASE_VIEW), flush]]", 
            lowerSystemMock.toString() 
        );
    }

}
