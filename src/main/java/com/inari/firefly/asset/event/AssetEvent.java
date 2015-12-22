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
package com.inari.firefly.asset.event;

import com.inari.commons.event.Event;
import com.inari.firefly.asset.Asset;

public final class AssetEvent extends Event<AssetEventListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( AssetEvent.class );
    
    public static enum Type {
        ASSET_CREATED,
        ASSET_LOADED,
        ASSET_DISPOSED,
        ASSET_DELETED
    }
    
    public final Asset asset;
    public final Type eventType;

    public AssetEvent( Asset asset, Type eventType ) {
        super( TYPE_KEY );
        this.asset = asset;
        this.eventType = eventType;
    }

    @Override
    public final void notify( AssetEventListener listener ) {
        listener.onAssetEvent( this );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "AssetEvent [eventType=" );
        builder.append( eventType );
        builder.append( ", assetType=" );
        builder.append( "]" );
        return builder.toString();
    }

}
