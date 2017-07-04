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

import java.util.ArrayDeque;

import com.inari.commons.event.Event;

public final class AssetEvent extends Event<AssetEventListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( AssetEvent.class );
    private static final ArrayDeque<AssetEvent> POOL = new ArrayDeque<AssetEvent>( 2 );
    
    public static enum Type {
        ASSET_CREATED,
        ASSET_LOADED,
        ASSET_DISPOSED,
        ASSET_DELETED
    }
    
    Asset asset;
    Type eventType;

    AssetEvent() {
        super( TYPE_KEY );
        restore();
    }

    @Override
    protected final void notify( AssetEventListener listener ) {
        listener.onAssetEvent( this );
    }

    public final Asset getAsset() {
        return asset;
    }

    @Override
    protected final void restore() {
        asset = null;
        eventType = null;
        
        POOL.addLast( this );
    }
    
    static final AssetEvent create( final Type eventType, final Asset asset ) {
        final AssetEvent result;
        if ( POOL.isEmpty() ) {
            result = new AssetEvent();
            POOL.addLast( result );
        } else {
            result = POOL.removeLast();
        }
        
        result.eventType = eventType;
        result.asset = asset;
        
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "AssetEvent [eventType=" );
        builder.append( eventType );
        builder.append( ", assetId=" );
        builder.append( asset.index() );
        builder.append( "]" );
        return builder.toString();
    }

}
