package com.inari.firefly.graphics.sprite;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.SystemComponentType;
import com.inari.firefly.system.external.SpriteData;
import com.inari.firefly.system.utils.Disposable;

public class SpriteSetAsset extends Asset {
    
    public static final SystemComponentType COMPONENT_TYPE = new SystemComponentType( Asset.TYPE_KEY, SpriteSetAsset.class );
    public static final AttributeKey<String> TEXTURE_ASSET_NAME = AttributeKey.createString( "textureAssetName", SpriteSetAsset.class );
    public static final AttributeKey<Integer> TEXTURE_ASSET_ID = AttributeKey.createInt( "textureAssetId", SpriteSetAsset.class );
    public static final AttributeKey<DynArray<Sprite>> SPRITE_DATA = AttributeKey.createDynArray( "spriteData", SpriteSetAsset.class, Sprite.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        TEXTURE_ASSET_ID,
        SPRITE_DATA
    );
    
    private final SpriteDataContainer spriteDataContainer = new SpriteDataContainer();
    
    private final DynArray<Sprite> spriteData = DynArray.create( Sprite.class, 30 ) ;

    protected SpriteSetAsset( int assetIntId ) {
        super( assetIntId );
    }
    
    public final int getTextureAssetId() {
        return dependsOn;
    }

    public final void setTextureAssetId( int textureAssetId ) {
        dependsOn = textureAssetId;
    }
    
    public final int getInstanceId( Sprite sprite ) {
        return sprite.instanceId;
    }

    @Override
    public final int getInstanceId( int index ) {
        if ( !loaded ) {
            return -1;
        }
        
        return spriteData.get( index ).instanceId;
    }
    
    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return JavaUtils.unmodifiableSet( super.attributeKeys(), ATTRIBUTE_KEYS );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        checkNotAlreadyLoaded();
        super.fromAttributes( attributes );
        
        setTextureAssetId( attributes.getIdForName( TEXTURE_ASSET_NAME, TEXTURE_ASSET_ID, Asset.TYPE_KEY, dependsOn ) );
        spriteData.clear();
        if ( attributes.contains( SPRITE_DATA ) ) {
            spriteData.addAll( attributes.getValue( SPRITE_DATA ) );
        }
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        attributes.put( TEXTURE_ASSET_ID, dependsOn );
        DynArray<Sprite> _spriteData = DynArray.create( Sprite.class, spriteData.capacity() );
        _spriteData.addAll( spriteData );
        attributes.put( SPRITE_DATA, _spriteData );
    }

    @Override
    public Disposable load( FFContext context ) {
        if ( loaded ) {
            return this;
        }
        
        spriteDataContainer.textureId = context.getSystem( AssetSystem.SYSTEM_KEY ).getAssetInstanceId( dependsOn );
        for ( int i = 0; i < spriteData.capacity(); i++ ) {
            Sprite sprite = spriteData.get( i );
            if ( sprite == null ) {
                continue;
            }
            
            spriteDataContainer.spriteData = sprite;
            sprite.instanceId = context.getGraphics().createSprite( spriteDataContainer );
        }
        
        
        return this;
    }

    @Override
    public void dispose( FFContext context ) {
        if ( !loaded ) {
            return;
        }
        
        for ( int i = 0; i < spriteData.capacity(); i++ ) {
            Sprite sprite = spriteData.get( i );
            if ( sprite == null ) {
                continue;
            }
            context.getGraphics().disposeSprite( sprite.instanceId );
            sprite.instanceId = -1;
        }
        
        spriteDataContainer.textureId = -1;
    }

    
    public static final class Sprite {
        
        public final Rectangle textureRegion;
        public final boolean flipHorizontal, flipVertical;
        
        int instanceId = -1;
        
        public Sprite( int x, int y, int width, int height ) {
            this.textureRegion = new Rectangle( x, y, width, height );
            flipHorizontal = false;
            flipVertical = false;
        }
        
        public Sprite( int x, int y, int width, int height, boolean flipHorizontal, boolean flipVertical ) {
            this.textureRegion = new Rectangle( x, y, width, height );
            this.flipHorizontal = flipHorizontal;
            this.flipVertical = flipVertical;
        }
        
        public final int getInstanceId() {
            return instanceId;
        }
    }
    
    private final class SpriteDataContainer implements SpriteData {
        
        int textureId;
        Sprite spriteData;

        @Override
        public final int getTextureId() {
            return textureId;
        }

        @Override
        public final Rectangle getTextureRegion() {
            return spriteData.textureRegion;
        }

        @Override
        public final boolean isHorizontalFlip() {
            return spriteData.flipHorizontal;
        }

        @Override
        public final boolean isVerticalFlip() {
            return spriteData.flipVertical;
        }
    };

}
