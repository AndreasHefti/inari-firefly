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
package com.inari.firefly.graphics;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.functional.IntFunction;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.FFGraphics;
import com.inari.firefly.system.external.TextureData;
import com.inari.firefly.system.utils.Disposable;

public final class TextureAsset extends Asset implements TextureData {
    
    public static final AttributeKey<String> RESOURCE_NAME = AttributeKey.createString( "resourceName", TextureAsset.class );
    public static final AttributeKey<Boolean> MIP_MAP = AttributeKey.createBoolean( "mipmap", TextureAsset.class );
    public static final AttributeKey<IntFunction> COLOR_CONVERTER = new AttributeKey<IntFunction>( "colorConverter", IntFunction.class, TextureAsset.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        RESOURCE_NAME,
        MIP_MAP,
        COLOR_CONVERTER
    );
    
    private String resourceName;
    private boolean mipmap;
    private IntFunction colorConverter;
    
    private int width;
    private int height;
    private int textureId = -1;
    
    TextureAsset( int assetId ) {
        super( assetId );
    }
    
    @Override
    public final int getInstanceId( int index ) {
        return textureId;
    }
    
    public int getTextureId() {
        return textureId;
    }

    @Override
    public final String getResourceName() {
        return resourceName;
    }
    
    public final void setResourceName( String resourceName ) {
        checkNotAlreadyLoaded();
        this.resourceName = resourceName;
    }

    public final boolean isMipmap() {
        return mipmap;
    }

    public final void setMipmap( boolean mipmap ) {
        this.mipmap = mipmap;
    }

    public final IntFunction getColorConverter() {
        return colorConverter;
    }

    public final void setColorConverter( IntFunction colorConverter ) {
        this.colorConverter = colorConverter;
    }

    public final int getTextureWidth() {
        return width;
    }

    @Override
    public final void setTextureWidth( int width ) {
        checkNotAlreadyLoaded();
        this.width = width;
    }

    public final int getTextureHeight() {
        return height;
    }

    @Override
    public final void setTextureHeight( int height ) {
        checkNotAlreadyLoaded();
        this.height = height;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return JavaUtils.unmodifiableSet( super.attributeKeys(), ATTRIBUTE_KEYS );
    }
    
    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        resourceName = attributes.getValue( RESOURCE_NAME, resourceName );
        mipmap = attributes.getValue( MIP_MAP, mipmap );
        colorConverter = attributes.getValue( COLOR_CONVERTER, colorConverter );
    }
    
    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        attributes.put( RESOURCE_NAME, resourceName );
        attributes.put( MIP_MAP, mipmap );
        attributes.put( COLOR_CONVERTER, colorConverter );
    }

    @Override
    public final Disposable load( FFContext context ) {
        if ( loaded ) {
            return this;
        }
        
        FFGraphics graphics = context.getGraphics();
        textureId = graphics.createTexture( this );
        return this;
    }

    @Override
    public final void dispose( FFContext context ) {
        if ( !loaded ) {
            return;
        }
        
        context.getGraphics().disposeTexture( textureId );
        textureId = -1;
        width = -1;
        height = 1;
    }

}
