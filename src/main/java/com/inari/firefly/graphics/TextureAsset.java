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
import com.inari.firefly.system.component.SystemComponentType;
import com.inari.firefly.system.external.FFGraphics;
import com.inari.firefly.system.external.TextureData;
import com.inari.firefly.system.utils.Disposable;

public final class TextureAsset extends Asset implements TextureData {
    
    public static final SystemComponentType COMPONENT_TYPE = new SystemComponentType( Asset.TYPE_KEY, TextureAsset.class );
    public static final AttributeKey<String> RESOURCE_NAME = AttributeKey.createString( "resourceName", TextureAsset.class );
    public static final AttributeKey<Boolean> MIP_MAP = AttributeKey.createBoolean( "mipmap", TextureAsset.class );
    public static final AttributeKey<Integer> WRAP_S = AttributeKey.createInt( "wrapS", TextureAsset.class );
    public static final AttributeKey<Integer> WRAP_T = AttributeKey.createInt( "wrapT", TextureAsset.class );
    public static final AttributeKey<Integer> MIN_FILTER = AttributeKey.createInt( "minFilter", TextureAsset.class );
    public static final AttributeKey<Integer> MAG_FILTER = AttributeKey.createInt( "magFilter", TextureAsset.class );
    public static final AttributeKey<IntFunction> COLOR_CONVERTER = new AttributeKey<IntFunction>( "colorConverter", IntFunction.class, TextureAsset.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        RESOURCE_NAME,
        MIP_MAP,
        WRAP_S,
        WRAP_T,
        MIN_FILTER,
        MAG_FILTER,
        COLOR_CONVERTER
    );
    
    private String resourceName;
    private boolean mipmap;
    private int wrapS;
    private int wrapT;
    private int minFilter;
    private int magFilter;
    private IntFunction colorConverter;
    
    private int width;
    private int height;
    private int textureId = -1;
    
    TextureAsset( int assetId ) {
        super( assetId );
        
        mipmap = false;
        wrapS = -1;
        wrapT = -1;
        minFilter = -1;
        magFilter = -1;
        colorConverter = null;
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

    public final int getWrapS() {
        return wrapS;
    }

    public final void setWrapS( int wrapS ) {
        this.wrapS = wrapS;
    }

    public final int getWrapT() {
        return wrapT;
    }

    public final void setWrapT( int wrapT ) {
        this.wrapT = wrapT;
    }

    public final int getMinFilter() {
        return minFilter;
    }

    public final void setMinFilter( int minFilter ) {
        this.minFilter = minFilter;
    }

    public final int getMagFilter() {
        return magFilter;
    }

    public final void setMagFilter( int magFilter ) {
        this.magFilter = magFilter;
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
        wrapS = attributes.getValue( WRAP_S, wrapS );
        wrapT = attributes.getValue( WRAP_T, wrapT );
        minFilter = attributes.getValue( MIN_FILTER, minFilter );
        magFilter = attributes.getValue( MAG_FILTER, magFilter );
        colorConverter = attributes.getValue( COLOR_CONVERTER, colorConverter );
    }
    
    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        attributes.put( RESOURCE_NAME, resourceName );
        attributes.put( MIP_MAP, mipmap );
        attributes.put( WRAP_S, wrapS );
        attributes.put( WRAP_T, wrapT );
        attributes.put( MIN_FILTER, minFilter );
        attributes.put( MAG_FILTER, magFilter );
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
