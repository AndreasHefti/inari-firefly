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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.FFInitException;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public class AssetSystem extends ComponentSystem<AssetSystem> {
    
    public static final FFSystemTypeKey<AssetSystem> SYSTEM_KEY = FFSystemTypeKey.create( AssetSystem.class );

    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        Asset.TYPE_KEY
    };
    
    private final DynArray<Asset> assets;
    private final Map<String, Asset> nameMapping;
    
    AssetSystem() {
        super( SYSTEM_KEY );
        assets = new DynArray<Asset>();
        nameMapping = new HashMap<String, Asset>();
    }

    @Override
    public void init( FFContext context ) {
        super.init( context );
    }

    @Override
    public final void dispose( FFContext context ) {
        clear();
    }
    
    public final Asset getAsset( String assetName ) {
        return nameMapping.get( assetName );
    }
    
    public final <A extends Asset> A getAssetAs( String assetName, Class<A> subType ) {
        Asset asset = getAsset( assetName );
        if ( asset == null ) {
            return null;
        }
        
        return subType.cast( asset );
    }
    
    public final Asset getAsset( int assetId ) {
        return assets.get( assetId );
    }
    
    public final <A extends Asset> A getAssetAs( int assetId, Class<A> subType ) {
        Asset asset = getAsset( assetId );
        if ( asset == null ) {
            return null;
        }
        
        return subType.cast( asset );
    }
    
    public final int getAssetInstanceId( int assetId ) {
        return getAssetInstanceId( getAsset( assetId ) );
    }
    
    public final int getAssetInstanceId( String assetName ) {
        return getAssetInstanceId( getAsset( assetName ) );
    }

    private int getAssetInstanceId( Asset asset ) {
        if ( !asset.loaded ) {
            throw new IllegalStateException( "Asset with name: " + asset.getName() + " not loaded" );
        }
        
        return asset.getInstanceId();
    }

    public final AssetBuilder getAssetBuilder() {
        return new AssetBuilder();
    }
    
    public final void loadAsset( String assetName ) {
        loadAsset( getAsset( assetName ) );
    }
    
    public final void loadAsset( int assetId ) {
        loadAsset( getAsset( assetId ) );
    }
    
    public final boolean isLoaded( int assetId ) {
        Asset asset = getAsset( assetId );
        if ( asset == null ) {
            return false;
        }
        
        return asset.loaded;
    }
    
    public final boolean isLoaded( String assetName ) {
        Asset asset = getAsset( assetName );
        if ( asset == null ) {
            return false;
        }
        
        return asset.loaded;
    }

    
    public final void disposeAsset( String assetName ) {
        Asset asset = getAsset( assetName );
        if ( asset == null ) {
            return;
        }
        
        if ( !asset.loaded ) {
            return;
        }
        
        disposeAsset( asset );
    }
    
    public final void disposeAsset( int assetId ) {
        Asset asset = getAsset( assetId );
        if ( asset == null ) {
            return;
        }
        
        if ( !asset.loaded ) {
            return;
        }
        
        disposeAsset( asset );
    }
    
    public final void deleteAsset( String assetName ) {
        Asset asset = getAsset( assetName );
        if ( asset == null ) {
            return;
        }
        deleteAsset( asset );
    }
    
    public final void deleteAsset( int assetId ) {
        Asset asset = getAsset( assetId );
        if ( asset == null ) {
            return;
        }
        deleteAsset( asset );
    }

    public final boolean hasAsset( String assetName ) {
        Asset asset = getAsset( assetName );
        return ( asset != null );
    }
    
    public final boolean hasAsset( int assetId ) {
        return assets.contains( assetId );
    }
    
    public final int getAssetId( String assetName ) {
        Asset asset = getAsset( assetName );
        if ( asset == null ) {
            return -1;
        }
        
        return asset.getId();
    }
    
    public final void clear() {
        for ( Asset asset : assets ) {
            deleteAsset( asset );
        }
        
        assets.clear();
        nameMapping.clear();
    }

    @Override
    public final SystemComponentKey<?>[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
            new AssetBuilderAdapter( this )
        };
    }

    private IntBag disposeAsset( Asset asset ) {
        IntBag toDisposeFirst = findeAssetsToDisposeFirst( asset.getId() );
        if ( !toDisposeFirst.isEmpty() ) {
            IntIterator iterator = toDisposeFirst.iterator();
            while ( iterator.hasNext() ) {
                Asset toDispose = getAsset( iterator.next() );
                if ( toDispose.loaded ) {
                    dispose( toDispose );
                }
            }
        }
        
        dispose( asset );
        return toDisposeFirst;
    }
    
    private IntBag findeAssetsToDisposeFirst( int assetId ) {
        IntBag result = new IntBag( 1, -1 );
        for ( Asset asset : assets ) {
            IntBag disposeFirst = asset.dependsOn();
            if ( disposeFirst != null ) {
                IntIterator iterator = disposeFirst.iterator();
                while ( iterator.hasNext() ) {
                    int next = iterator.next();
                    if ( next == assetId ) {
                        result.add( next );
                        break;
                    }
                }
            }
        }
        return result;
    }
    
    private void deleteAsset( Asset asset ) {
        if ( asset.loaded ) {
            IntBag alsoDisposedAssets = disposeAsset( asset );
            if ( alsoDisposedAssets != null ) {
                IntIterator iterator = alsoDisposedAssets.iterator();
                while ( iterator.hasNext() ) {
                    delete( iterator.next() );
                }
            }
        }
        
        delete( asset.getId() );
    }
    
    private void delete( int assetId ) {
        Asset deleted = assets.remove( assetId );
        if ( deleted == null ) {
            return;
        }
        
        nameMapping.remove( deleted.getName() );
        
        context.notify( new AssetEvent( deleted, AssetEvent.Type.ASSET_DELETED ) );
        deleted.dispose();
    }

    private void dispose( Asset asset ) {
        asset.dispose( context );
        context.notify( new AssetEvent( asset, AssetEvent.Type.ASSET_DISPOSED ) );
        asset.loaded = false;
    }

    private void loadAsset( Asset asset ) {
        IntBag loadFirst = asset.dependsOn();
        if ( loadFirst != null ) {
            IntIterator iterator = loadFirst.iterator();
            while ( iterator.hasNext() ) {
                Asset assetToLoadFirst = getAsset( iterator.next() );
                if ( !assetToLoadFirst.loaded ) {
                    load( assetToLoadFirst );
                }
            }
        }
        
        load( asset );
    }
    
    private void load( Asset asset ) {
        asset.load( context );
        context.notify( new AssetEvent( asset, AssetEvent.Type.ASSET_LOADED ) );
        asset.loaded = true;
    }

    
    public final class AssetBuilder extends SystemComponentBuilder {
        
        private AssetBuilder() {
            super( context );
        }
        
        @Override
        public final SystemComponentKey<Asset> systemComponentKey() {
            return Asset.TYPE_KEY;
        }

        @Override
        public int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            Asset asset = createSystemComponent( componentId, componentType, context );
            if ( nameMapping.containsKey( asset.getName() ) ) {
                throw new FFInitException( "There is already a Asset with name: " + asset.getName() );
            }

            assets.set( asset.getId(), asset );
            nameMapping.put( asset.getName(), asset );

            context.notify( new AssetEvent( asset, AssetEvent.Type.ASSET_CREATED ) );
            
            if ( activate ) {
                load( asset );
            }
            
            return asset.getId();
        }
    }
    
    private final class AssetBuilderAdapter extends SystemBuilderAdapter<Asset> {
        public AssetBuilderAdapter( AssetSystem system ) {
            super( system, new AssetBuilder() );
        }
        @Override
        public final SystemComponentKey<Asset> componentTypeKey() {
            return Asset.TYPE_KEY;
        }
        @Override
        public final Asset getComponent( int id ) {
            return getAsset( id );
        }
        @Override
        public final void deleteComponent( int id ) {
            deleteAsset( id );
        }
        
        @Override
        public final Iterator<Asset> getAll() {
            return assets.iterator();
        }
        @Override
        public final void deleteComponent( String name ) {
            deleteAsset( getAssetId( name ) );
        }
        @Override
        public final Asset getComponent( String name ) {
            return getAsset( getAssetId( name ) );
        }
    }

}
