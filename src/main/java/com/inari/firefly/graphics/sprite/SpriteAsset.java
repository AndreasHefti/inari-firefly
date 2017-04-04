/*******************************************************************************
 * Copyright (c) 2015 - 2016, Andreas Hefti, inarisoft@yahoo.de 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/ 
package com.inari.firefly.graphics.sprite;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.SpriteData;
import com.inari.firefly.system.utils.Disposable;

public final class SpriteAsset extends Asset implements SpriteData {
    
    public static final AttributeKey<String> TEXTURE_ASSET_NAME = AttributeKey.createString( "textureAssetName", SpriteAsset.class );
    public static final AttributeKey<Integer> TEXTURE_ASSET_ID = AttributeKey.createInt( "textureAssetId", SpriteAsset.class );
    public static final AttributeKey<Rectangle> TEXTURE_REGION  = AttributeKey.createRectangle( "textureRegion", SpriteAsset.class );
    public static final AttributeKey<Boolean> HORIZONTAL_FLIP = AttributeKey.createBoolean( "horizontalFlip", ESprite.class );
    public static final AttributeKey<Boolean> VERTICAL_FLIP = AttributeKey.createBoolean( "verticalFlip", ESprite.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        TEXTURE_ASSET_ID,
        TEXTURE_REGION,
        HORIZONTAL_FLIP,
        VERTICAL_FLIP
    );
    
    private int textureAssetId;
    private final Rectangle textureRegion;
    private final IntBag dependsOn = new IntBag( 1, -1 );
    
    private int textureId = -1;
    private int spriteId = -1;
    
    private boolean horizontalFlip = false;
    private boolean verticalFlip = false;
    
    SpriteAsset( int assetId ) {
        super( assetId );
        textureRegion = new Rectangle();
    }

    public final int getTextureAssetId() {
        return textureAssetId;
    }

    public final void setTextureAssetId( int textureAssetId ) {
        this.textureAssetId = textureAssetId;
        dependsOn.clear();
        dependsOn.set( 0, textureAssetId );
    }

    @Override
    public final int getInstanceId( int index ) {
        return spriteId;
    }
    
    public final int getSpriteId() {
        return spriteId;
    }

    @Override
    protected final IntBag dependsOn() {
        return dependsOn;
    }
    
    public final int getTextureId() {
        return textureId;
    }

    public final Rectangle getTextureRegion() {
        return textureRegion;
    }

    public final void setTextureRegion( Rectangle textureRegion ) {
        checkNotAlreadyLoaded();
        this.textureRegion.x = textureRegion.x;
        this.textureRegion.y = textureRegion.y;
        this.textureRegion.width = textureRegion.width;
        this.textureRegion.height = textureRegion.height;
    }
    
    public final boolean isHorizontalFlip() {
        return horizontalFlip;
    }

    public final void setHorizontalFlip( boolean horizontalFlip ) {
        this.horizontalFlip = horizontalFlip;
    }

    public final boolean isVerticalFlip() {
        return verticalFlip;
    }

    public final void setVerticalFlip( boolean verticalFlip ) {
        this.verticalFlip = verticalFlip;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return JavaUtils.unmodifiableSet( super.attributeKeys(), ATTRIBUTE_KEYS );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        checkNotAlreadyLoaded();
        super.fromAttributes( attributes );
        
        setTextureAssetId( attributes.getIdForName( TEXTURE_ASSET_NAME, TEXTURE_ASSET_ID, Asset.TYPE_KEY, textureAssetId ) );
        Rectangle textureRegion = attributes.getValue( TEXTURE_REGION );
        if ( textureRegion != null ) {
            setTextureRegion( textureRegion );
        }
        
        horizontalFlip = attributes.getValue( HORIZONTAL_FLIP, horizontalFlip );
        verticalFlip = attributes.getValue( VERTICAL_FLIP, verticalFlip );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        attributes.put( TEXTURE_ASSET_ID, textureAssetId );
        attributes.put( TEXTURE_REGION, new Rectangle( textureRegion ) );
        attributes.put( HORIZONTAL_FLIP, horizontalFlip );
        attributes.put( VERTICAL_FLIP, verticalFlip );
    }

    @Override
    public final Disposable load( FFContext context ) {
        if ( loaded ) {
            return this;
        }
        
        textureId = context.getSystem( AssetSystem.SYSTEM_KEY ).getAssetInstanceId( textureAssetId );
        spriteId = context.getGraphics().createSprite( this );
        
        return this;
    }

    @Override
    public final void dispose( FFContext context ) {
        if ( !loaded ) {
            return;
        }
        
        context.getGraphics().disposeSprite( spriteId );
        spriteId = -1;
        textureId = -1;
    }

}
