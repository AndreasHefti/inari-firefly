package com.inari.firefly.graphics.sprite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.inari.commons.geom.Rectangle;
import com.inari.firefly.Disposable;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.graphics.TextureAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.FFGraphics;

public final class SpriteGroupAsset extends Asset {
    
    public static final AttributeKey<Integer> TEXTURE_ASSET_ID = new AttributeKey<Integer>( "textureAssetId", Integer.class, Asset.class );
    public static final AttributeKey<Rectangle[]> TEXTURE_REGIONS  = new AttributeKey<Rectangle[]>( "textureRegions", Rectangle[].class, Asset.class );
    private static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = new HashSet<AttributeKey<?>>( Arrays.<AttributeKey<?>>asList( new AttributeKey[] { 
        TEXTURE_ASSET_ID,
        TEXTURE_REGIONS
    } ) );
    
    private int textureAssetId;
    private final List<Rectangle> textureRegions;
    
    private int[] spriteIds;
    
    protected SpriteGroupAsset( int assetIntId ) {
        super( assetIntId );
        textureAssetId = -1;
        textureRegions = new ArrayList<Rectangle>();
        spriteIds = null;
    }

    @Override
    public final int getInstanceId( int index ) {
        if ( index < 0 || spriteIds == null || index >= spriteIds.length ) {
            return -1;
        }
        
        return spriteIds[ index ];
    }

    public final int getTextureAssetId() {
        return textureAssetId;
    }

    public final void setTextureAssetId( int textureAssetId ) {
        checkNotAlreadyLoaded();
        this.textureAssetId = textureAssetId;
    }

    public final List<Rectangle> getTextureRegions() {
        return textureRegions;
    }
    
    public final void setTextureRegions( List<Rectangle> textureRegions ) {
        checkNotAlreadyLoaded();
        
        this.textureRegions.clear();
        if ( textureRegions == null ) {
            return;
        }
        
        this.textureRegions.addAll( textureRegions );
    }
    
    public final SpriteGroupAsset addTextureRegion( Rectangle textureRegion ) {
        checkNotAlreadyLoaded();
        textureRegions.add( textureRegion );
        return this;
    }
    
    public final SpriteGroupAsset addTextureRegion( Rectangle textureRegion, int index ) {
        checkNotAlreadyLoaded();
        textureRegions.add( index, textureRegion );
        return this;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( ATTRIBUTE_KEYS );
        return super.attributeKeys( attributeKeys );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        checkNotAlreadyLoaded();
        super.fromAttributes( attributes );
        
        textureAssetId = attributes.getValue( TEXTURE_ASSET_ID, textureAssetId );
        Rectangle[] regions = attributes.getValue( TEXTURE_REGIONS );
        if ( regions != null ) {
            setTextureRegions( Arrays.asList( regions ) );
        }
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        attributes.put( TEXTURE_ASSET_ID, textureAssetId );
        
        Rectangle[] regions = new Rectangle[ textureRegions.size() ];
        attributes.put( TEXTURE_REGIONS, textureRegions.toArray( regions ) );
    }

    @Override
    public final Disposable load( FFContext context ) {
        if ( loaded ) {
            return this;
        }
        
        TextureAsset textureAsset = context.getSystem( AssetSystem.SYSTEM_KEY ).getAssetAs( textureAssetId, TextureAsset.class );
        if ( !textureAsset.isLoaded() ) {
            textureAsset.load( context );
        }
        
        FFGraphics graphics = context.getGraphics();
        spriteIds = new int[ textureRegions.size() ];
        
        int index = 0;
        for ( Rectangle textureRegion : textureRegions ) {
            spriteIds[ index ] = graphics.createSprite( textureAsset.getInstanceId(), textureRegion );
            index++;
        }
        
        return this;
    }

    @Override
    public final void dispose( FFContext context ) {
        if ( !loaded ) {
            return;
        }
        
        FFGraphics graphics = context.getGraphics();
        for ( int i = 0; i < spriteIds.length; i++ ) {
            graphics.disposeSprite( spriteIds[ i ] );
        }
        spriteIds = null;
    }

}
