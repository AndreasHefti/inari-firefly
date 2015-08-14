package com.inari.firefly;

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

public class SystemTests {
    
    private static final AssetNameKey TEXTURE_ASSET_KEY = new AssetNameKey( "origTilesResource", "boulderDashTextureAsset" );
    private static final AssetNameKey SPRITE_ASSET_KEY = new AssetNameKey( "origTilesResource", "spriteAsset" );
    
    @Test
    public void createTextureAndSpriteAndOneEntity() {
        FireFly firefly = new FireFly( LowerSystemFacadeMock.class );
        FFContext context = firefly.getContext();
        AssetSystem assetSystem = context.getComponent( FFContext.Systems.ASSET_SYSTEM );
        EntitySystem entitySystem = context.getComponent( FFContext.Systems.ENTITY_SYSTEM );
        
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
                .setAttribute( ETransform.XPOSITION, 0 )
                .setAttribute( ETransform.XPOSITION, 0 )
                .setAttribute( ESprite.SPRITE_ID, assetSystem.getAssetTypeKey( SPRITE_ASSET_KEY ).id )
                .setAttribute( ESprite.VIEW_ID, 0 )
            .build()
            ;

        assetSystem.loadAsset( TEXTURE_ASSET_KEY );
        assetSystem.loadAsset( SPRITE_ASSET_KEY );
        entitySystem.activate( entity.getId() );
        
        firefly.update();
        firefly.render();
    }

}
