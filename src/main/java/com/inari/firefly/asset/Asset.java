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
package com.inari.firefly.asset;

import java.util.HashSet;
import java.util.Set;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.Disposable;
import com.inari.firefly.Loadable;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.dynattr.DynamicAttribueMapper;
import com.inari.firefly.component.dynattr.DynamicAttributeMap;
import com.inari.firefly.component.dynattr.DynamicAttributedComponent;
import com.inari.firefly.system.component.SystemComponent;

public abstract class Asset extends SystemComponent implements Loadable, Disposable, DynamicAttributedComponent {
    
    public static final SystemComponentKey<Asset> TYPE_KEY = SystemComponentKey.create( Asset.class );
    
    protected boolean loaded = false;
    
    private DynamicAttributeMap dynamicAttributeMap = new DynamicAttributeMap();
    
    protected Asset( int assetIntId ) {
        super( assetIntId );
    }
    
    public abstract int getInstanceId();
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

    public final boolean isLoaded() {
        return loaded;
    }
    
    @Override
    public final Class<? extends Asset> componentType() {
        return this.getClass();
    }

    @Override
    public boolean hasDynamicAttributes() {
        return DynamicAttribueMapper.hasDynamicAttributes( componentType() );
    }
    
    @Override
    public final <A> void setDynamicAttribute( AttributeKey<A> key, A value ) {
        dynamicAttributeMap.setDynamicAttribute( key, value, componentType() );
    }
    
    @Override
    public final <A> A getDynamicAttribute( AttributeKey<A> key ) {
        return dynamicAttributeMap.getDynamicAttribute( key );
    }

    @Override
    public void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        if ( hasDynamicAttributes() ) {
            dynamicAttributeMap.fromAttributeMap( attributes, this );
        }
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        if ( hasDynamicAttributes() ) {
            dynamicAttributeMap.toAttributeMap( attributes, this );
        }
    }
    
    protected Set<AttributeKey<?>> attributeKeys( Set<AttributeKey<?>> attributeKeys ) {
        return dynamicAttributeMap.attributeKeys( this, new HashSet<AttributeKey<?>>( attributeKeys ) );
    }

    protected IntBag dependsOn() {
        return null;
    }
    
    protected void checkNotAlreadyLoaded() {
        if ( loaded ) {
            throw new IllegalStateException( "Asset: " + name + " is already loaded and can not be modified" );
        } 
    }

}
