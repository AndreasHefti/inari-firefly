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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.firefly.Disposable;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.FFGraphics;

public final class TextureAsset extends Asset {
    
    public static final AttributeKey<String> RESOURCE_NAME = new AttributeKey<String>( "resourceName", String.class, TextureAsset.class );
    
    private static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = new HashSet<AttributeKey<?>>( Arrays.<AttributeKey<?>>asList( new AttributeKey[] { 
        RESOURCE_NAME,
    } ) );
    
    private String resourceName;
    
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

    public final String getResourceName() {
        return resourceName;
    }
    
    public final void setResourceName( String resourceName ) {
        checkNotAlreadyLoaded();
        this.resourceName = resourceName;
    }
    
    public final int getWidth() {
        return width;
    }

    public final void setWidth( int width ) {
        checkNotAlreadyLoaded();
        this.width = width;
    }

    public final int getHeight() {
        return height;
    }

    public final void setHeight( int height ) {
        checkNotAlreadyLoaded();
        this.height = height;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( ATTRIBUTE_KEYS );
        return super.attributeKeys( attributeKeys );
    }
    
    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        resourceName = attributes.getValue( RESOURCE_NAME, resourceName );
    }
    
    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        attributes.put( RESOURCE_NAME, resourceName );
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
