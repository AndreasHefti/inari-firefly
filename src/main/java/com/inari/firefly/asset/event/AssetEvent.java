package com.inari.firefly.asset.event;

import com.inari.commons.event.Event;
import com.inari.firefly.asset.Asset;

public final class AssetEvent extends Event<AssetEventListener> {
    
    public static enum Type {
        ASSET_CREATED,
        ASSET_LOADED,
        ASSET_DISPOSED,
        ASSET_DELETED
    }
    
    public final Asset asset;
    public final Class<? extends Asset> assetType;
    public final Type type;

    public AssetEvent( Asset asset, Type type ) {
        this.asset = asset;
        assetType = asset.getIndexedObjectType();
        this.type = type;
    }

    @Override
    public final void notify( AssetEventListener listener ) {
        listener.onAssetEvent( this );
    }

}
