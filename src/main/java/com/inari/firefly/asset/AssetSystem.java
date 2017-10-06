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
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentMap.BuilderListener;
import com.inari.firefly.system.component.SystemComponentNameMap;

public class AssetSystem extends ComponentSystem<AssetSystem> {
    
    public static final FFSystemTypeKey<AssetSystem> SYSTEM_KEY = FFSystemTypeKey.create( AssetSystem.class );
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        Asset.TYPE_KEY
    );
    
    final SystemComponentNameMap<Asset> assets;
    final IntBag dependingAssetIds = new IntBag( 1, -1 );
    
    AssetSystem() {
        super( SYSTEM_KEY );
        assets = new SystemComponentNameMap<>( 
            this, Asset.TYPE_KEY,
            new BuilderListener<Asset>() {
                public final void notifyBuild( Asset component ) { build( component ); }
                public final void notifyActivation( int id ) { loadAsset( id ); }
                public final void notifyDeactivation( int id ) { disposeAsset( id ); }
                public final void notifyDeletion( Asset component ) { deleteAsset( component ); }
            }
        );
    }

    @Override
    public void init( FFContext context ) {
        super.init( context );
    }
    
    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }
 
    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            assets.getBuilderAdapter()
        );
    }
    
    public final void loadAsset( int assetId ) {
        loadAsset( assets.get( assetId ) );
    }

    public final void loadAsset( String name ) {
        assets.activate( assets.getId( name ) );
    }
    
    public final boolean isLoaded( int assetId ) {
        return assets.get( assetId ).isLoaded();
    }

    public final boolean isLoaded( String name ) {
        return assets.get( assets.getId( name ) ).isLoaded();
    }

    public final int getAssetInstanceId( int assetId ) {
        final Asset asset = assets.get( assetId );
        if ( !asset.loaded ) {
            throw new IllegalStateException( "Asset with name: " + asset.getName() + " not loaded" );
        }
        
        return asset.getInstanceId();
    }
    
    public final int getAssetInstanceId( String assetName ) {
        return getAssetInstanceId( assets.getId( assetName ) );
    }

    public final void clearSystem() {
        assets.clear();
    }

    public final void dispose( FFContext context ) {
        clearSystem();
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
        findeDependingAssets( asset.index() );
        if ( !dependingAssetIds.isEmpty() ) {
            for ( int i = 0; i < dependingAssetIds.length(); i++ ) {
                if ( dependingAssetIds.isEmpty( i ) ) {
                    continue;
                }
                Asset toDispose = assets.get( dependingAssetIds.get( i ) );
                if ( toDispose.loaded ) {
                    dispose( toDispose );
                }
            }
        }
        
        dispose( asset );
        return dependingAssetIds;
    }
    
    private void dispose( Asset asset ) {
        asset.dispose( context );
        context.notify( AssetEvent.create( AssetEvent.Type.ASSET_DISPOSED, asset ) );
        asset.loaded = false;
    }
    
    private void findeDependingAssets( int assetId ) {
        dependingAssetIds.clear();
        for ( Asset asset : assets.map ) {
            if ( asset.dependsOn >= 0 ) {
                if ( asset.dependsOn == assetId ) {
                    dependingAssetIds.add( asset.index() );
                    break;
                }
            }
        }
    }
    
    private void deleteAsset( Asset asset ) {
        if ( asset.loaded ) {
            disposeAsset( asset );
            if ( dependingAssetIds != null ) {
                for ( int i = 0; i < dependingAssetIds.length(); i++ ) {
                    if ( dependingAssetIds.isEmpty( i ) ) {
                        continue;
                    }

                    assets.delete( dependingAssetIds.get( i ) );
                }
            }
        }
        
        context.notify( AssetEvent.create( AssetEvent.Type.ASSET_DELETED, asset ) );
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
