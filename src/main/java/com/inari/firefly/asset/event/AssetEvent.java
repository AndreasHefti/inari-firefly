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
    
    private final Asset asset;
    private final Type type;

    public AssetEvent( Asset asset, Type type ) {
        this.asset = asset;
        this.type = type;
    }

    public final Asset getAsset() {
        return asset;
    }

    public final Type getType() {
        return type;
    }

    @Override
    public final void notify( AssetEventListener listener ) {
        listener.onAssetEvent( this );
    }

}
