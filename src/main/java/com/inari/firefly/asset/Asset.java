/*******************************************************************************
 * Copyright (c) 2015, Andreas Hefti, inarisoft@yahoo.de 
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
package com.inari.firefly.asset;

import java.util.HashSet;
import java.util.Set;

import com.inari.firefly.component.NamedIndexedComponent;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.dynattr.DynamicAttribueMapper;
import com.inari.firefly.component.dynattr.DynamicAttributeMap;
import com.inari.firefly.component.dynattr.DynamicAttributedComponent;

public abstract class Asset extends NamedIndexedComponent implements DynamicAttributedComponent {
    
    public static final AttributeKey<String> ASSET_GROUP = new AttributeKey<String>( "group", String.class, Asset.class );
    
    protected boolean loaded = false;
    protected String group;
    protected final AssetTypeKey typeKey;
    
    private DynamicAttributeMap dynamicAttributeMap = new DynamicAttributeMap();
    
    protected Asset( int assetId ) {
        super( assetId );
        typeKey = new AssetTypeKey( super.index, getComponentType() );
    }

    public boolean isLoaded() {
        return loaded;
    }
    @Override
    public final Class<? extends Asset> indexedObjectType() {
        return getComponentType();
    }
    
    @Override
    public abstract Class<? extends Asset> getComponentType();
    
    public final AssetTypeKey getTypeKey() {
        return typeKey;
    }

    @Override
    public boolean hasDynamicAttributes() {
        return DynamicAttribueMapper.hasDynamicAttributes( getComponentType() );
    }
    
    @Override
    public final <A> void setDynamicAttribute( AttributeKey<A> key, A value ) {
        dynamicAttributeMap.setDynamicAttribute( key, value, getComponentType() );
    }
    
    @Override
    public final <A> A getDynamicAttribute( AttributeKey<A> key ) {
        return dynamicAttributeMap.getDynamicAttribute( key );
    }

    @Override
    public void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        group = attributes.getValue( ASSET_GROUP, group );
        
        if ( hasDynamicAttributes() ) {
            dynamicAttributeMap.fromAttributeMap( attributes, this );
        }
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        attributes.put( ASSET_GROUP, group );
        
        if ( hasDynamicAttributes() ) {
            dynamicAttributeMap.toAttributeMap( attributes, this );
        }
    }
    
    protected Set<AttributeKey<?>> attributeKeys( Set<AttributeKey<?>> attributeKeys ) {
        return dynamicAttributeMap.attributeKeys( this, new HashSet<AttributeKey<?>>( attributeKeys ) );
    }

    protected AssetTypeKey[] dependsOn() {
        return null;
    }
    
    protected void checkNotAlreadyLoaded() {
        if ( loaded ) {
            throw new IllegalStateException( "Asset: " + group + " " + name + " is already loaded and can not be modified" );
        } 
    }

}
