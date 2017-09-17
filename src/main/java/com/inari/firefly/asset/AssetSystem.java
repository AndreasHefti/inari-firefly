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

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.Activation;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentMap;
import com.inari.firefly.system.component.SystemComponentMap.BuilderAdapter;
import com.inari.firefly.system.component.SystemComponentNameMap;

public class AssetSystem extends ComponentSystem<AssetSystem> {
    
    public static final FFSystemTypeKey<AssetSystem> SYSTEM_KEY = FFSystemTypeKey.create( AssetSystem.class );
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        Asset.TYPE_KEY
    );
    
    private final SystemComponentNameMap<Asset> assets;
    private final IntBag toDisposeFirst = new IntBag( 1, -1 );
    
    AssetSystem() {
        super( SYSTEM_KEY );
        assets = SystemComponentNameMap.create( 
            Asset.TYPE_KEY,
            new Activation() {
                @Override public final void activate( int id ) { loadAsset( id ); }
                @Override public final void deactivate( int id ) { disposeAsset( id ); }
            },
            new BuilderAdapter<Asset>() {
                @Override public final void finishBuild( Asset component ) { build( component ); }
                @Override public final void finishDeletion( Asset component ) { deleteAsset( component ); }
            }
        );
    }

    @Override
    public void init( FFContext context ) {
        super.init( context );
    }
    
    public final SystemComponentMap<Asset> assetMap() {
        return assets;
    }

    public final void loadAsset( String name ) {
        assets.activate( assets.getId( name ) );
    }

    public final boolean isLoaded( String name ) {
        return assets.get( assets.getId( name ) ).isLoaded();
    }

    public final int getAssetInstanceId( int assetId ) {
        return getAssetInstanceId( assets.get( assetId ) );
    }
    
    public final int getAssetInstanceId( String assetName ) {
        return getAssetInstanceId( assets.getId( assetName ) );
    }

    @Override
    public final void clearSystem() {
        assets.clear();
    }

    @Override
    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }
    
    public SystemBuilderAdapter<Asset> getAssetBuilderAdapter() {
        return assets.getBuilderAdapter( context, this );
    }

    @Override
    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            getAssetBuilderAdapter()
        );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        clearSystem();
    }
    
    
    private int getAssetInstanceId( Asset asset ) {
        if ( !asset.loaded ) {
            throw new IllegalStateException( "Asset with name: " + asset.getName() + " not loaded" );
        }
        
        return asset.getInstanceId();
    }
    
    private final void loadAsset( int assetId ) {
        loadAsset( assets.get( assetId ) );
    }

    private final void disposeAsset( int assetId ) {
        Asset asset = assets.get( assetId );
        if ( asset == null ) {
            return;
        }
        
        if ( !asset.loaded ) {
            return;
        }
        
        disposeAsset( asset );
    }
    
    private IntBag disposeAsset( Asset asset ) {
        findeAssetsToDisposeFirst( asset.index() );
        if ( !toDisposeFirst.isEmpty() ) {
            for ( int i = 0; i < toDisposeFirst.length(); i++ ) {
                if ( toDisposeFirst.isEmpty( i ) ) {
                    continue;
                }
                Asset toDispose = assets.get( toDisposeFirst.get( i ) );
                if ( toDispose.loaded ) {
                    dispose( toDispose );
                }
            }
        }
        
        dispose( asset );
        return toDisposeFirst;
    }
    
    private void findeAssetsToDisposeFirst( int assetId ) {
        toDisposeFirst.clear();
        for ( Asset asset : assets.map ) {
            if ( asset.dependsOn >= 0 ) {
                if ( asset.dependsOn == assetId ) {
                    toDisposeFirst.add( asset.dependsOn );
                    break;
                }
            }
        }
    }
    
    private void deleteAsset( Asset asset ) {
        if ( asset.loaded ) {
            IntBag alsoDisposedAssets = disposeAsset( asset );
            if ( alsoDisposedAssets != null ) {
                for ( int i = 0; i < alsoDisposedAssets.length(); i++ ) {
                    if ( alsoDisposedAssets.isEmpty( i ) ) {
                        continue;
                    }

                    assets.delete( alsoDisposedAssets.get( i ) );
                }
            }
        }
        
        context.notify( AssetEvent.create( AssetEvent.Type.ASSET_DELETED, asset ) );
    }

    private void dispose( Asset asset ) {
        asset.dispose( context );
        context.notify( AssetEvent.create( AssetEvent.Type.ASSET_DISPOSED, asset ) );
        asset.loaded = false;
    }

    private void loadAsset( Asset asset ) {
        int loadFirst = asset.dependsOn();
        if ( loadFirst >= 0 ) {
            Asset assetToLoadFirst = assets.get( loadFirst );
            if ( !assetToLoadFirst.loaded ) {
                loadAsset( assetToLoadFirst );
            }
        }
        
        asset.load( context );
        context.notify( AssetEvent.create( AssetEvent.Type.ASSET_LOADED, asset ) );
        asset.loaded = true;
    }
    
    private void build( Asset asset ) {
        if ( asset.getName() == null ) {
            asset.setName( context.createNextNoName() );
        }
        
        context.notify( AssetEvent.create( AssetEvent.Type.ASSET_CREATED, asset ) );
    }

}
