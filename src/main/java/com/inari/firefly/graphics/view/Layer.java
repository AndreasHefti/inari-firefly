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
package com.inari.firefly.graphics.view;

import java.util.Arrays;
import java.util.Set;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.system.component.SystemComponent;

public final class Layer extends SystemComponent {
    
    public static final SystemComponentKey<Layer> TYPE_KEY = SystemComponentKey.create( Layer.class );
    
    public static final String DEFAULT_LAYER_NAME = "LAYER_";
    
    public static final AttributeKey<String> VIEW_NAME = new AttributeKey<String>( "viewName", String.class, Layer.class );
    public static final AttributeKey<Integer> VIEW_ID = new AttributeKey<Integer>( "viewId", Integer.class, Layer.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        VIEW_ID
    };

    private int viewId;
    
    boolean active = false;
    
    Layer( int layerId ) {
        super( layerId );
    }
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

    public int getViewId() {
        return viewId;
    }

    public final boolean isActive() {
        return active;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( Arrays.asList( ATTRIBUTE_KEYS ) );
        return attributeKeys;
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        viewId = attributes.getIdForName( VIEW_NAME, VIEW_ID, View.TYPE_KEY, -1 );
        if ( viewId < 0 ) {
            throw new ComponentCreationException( "Missing mandatory viewId attribute" );
        }
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( VIEW_ID, viewId );
    }

}
