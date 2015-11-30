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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.inari.commons.StringUtils;
import com.inari.firefly.asset.event.AssetEvent;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public class AssetSystem extends ComponentSystem<AssetSystem> {
    
    public static final FFSystemTypeKey<AssetSystem> SYSTEM_KEY = FFSystemTypeKey.create( AssetSystem.class );

    public static final String DEFAULT_GROUP_NAME = "FF_DEFAULT_ASSET_GROUP";
    private static final SystemComponentKey[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        Asset.TYPE_KEY
    };
    
    private final Map<AssetNameKey, Asset> assets;
    private final Map<AssetTypeKey, Asset> typeMapping;
    
    
    AssetSystem() {
        super( SYSTEM_KEY );
        assets = new LinkedHashMap<AssetNameKey, Asset>();
        typeMapping = new LinkedHashMap<AssetTypeKey, Asset>();
    }

    @Override
    public void init( FFContext context ) {
        super.init( context );
    }

    @Override
    public final void dispose( FFContext context ) {
        clear();
    }
    
    public final Asset getAsset( AssetNameKey key ) {
        return assets.get( key );
    }
    
    public final Asset getAsset( AssetTypeKey key ) {
        return typeMapping.get( key );
    }
    
    public final <A extends Asset> A getAsset( AssetNameKey assetKey, Class<A> assetType ) {
        Asset asset = getAsset( assetKey );
        if ( asset == null ) {
            return null;
        }
        
        return assetType.cast( asset );
    }
    
    public final <A extends Asset> A getAsset( AssetTypeKey assetKey, Class<A> assetType ) {
        Asset asset = getAsset( assetKey );
        if ( asset == null ) {
            return null;
        }
        
        return assetType.cast( asset );
    }

    public final AssetBuilder getAssetBuilder() {
        return new AssetBuilder();
    }
    
    public final void loadAsset( AssetNameKey key ) {
        Asset asset = getAsset( key );
        checkAsset( key, asset );
        loadAsset( asset );
    }
    
    public final void loadAsset( AssetTypeKey key ) {
        Asset asset = getAsset( key );
        checkAsset( key, asset );
        loadAsset( asset );
    }
    
    public final void loadAssets( String group ) {
        if ( !hasGroup( group ) ) {
            return;
        }
        
        for ( AssetNameKey assetKey : getAssetNameKeysOfGroup( group ) ) {
            Asset asset = assets.get( assetKey );
            if ( !asset.loaded ) {
                loadAsset( asset );
            }
        }
    }
    
    public final Collection<AssetNameKey> getNameKeys() {
        return assets.keySet();
    }
    
    public final Collection<AssetTypeKey> getTypeKeys() {
        return typeMapping.keySet();
    }

    public final AssetTypeKey getAssetTypeKey( AssetNameKey assetNameKey ) {
        Asset asset = getAsset( assetNameKey );
        if ( asset == null ) {
            return null;
        }
        return asset.typeKey;
    }

    public final boolean isAssetLoaded( AssetNameKey key ) {
        Asset asset = getAsset( key );
        checkAsset( key, asset );
        return asset.loaded;
    }
    
    public final void disposeAsset( AssetNameKey key ) {
        Asset asset = getAsset( key );
        if ( asset == null ) {
            return;
        }
        
        if ( !asset.loaded ) {
            return;
        }
        
        disposeAsset( asset );
    }
    
    public final void disposeAsset( AssetTypeKey key ) {
        Asset asset = getAsset( key );
        if ( asset == null ) {
            return;
        }
        
        if ( !asset.loaded ) {
            return;
        }
        
        disposeAsset( asset );
    }
    
    public final void disposeAssets( String group ) {
        if ( !hasGroup( group ) ) {
            return;
        }
        
        for ( AssetNameKey assetKey : getAssetNameKeysOfGroup( group ) ) {
            Asset asset = assets.get( assetKey );
            if ( asset.loaded ) {
                disposeAsset( asset );
            }
        }
    }
    
    public final void deleteAsset( AssetNameKey key ) {
        Asset asset = getAsset( key );
        if ( asset == null ) {
            return;
        }
        deleteAsset( asset );
    }
    
    public final void deleteAsset( AssetTypeKey key ) {
        Asset asset = getAsset( key );
        if ( asset == null ) {
            return;
        }
        deleteAsset( asset );
    }
    
    public final void deleteAssets( String group ) {
        if ( !hasGroup( group ) ) {
            return;
        }
        
        Collection<AssetTypeKey> assetsToDeleteAlso = null;
        Collection<AssetNameKey> assetNameKeysOfGroup = getAssetNameKeysOfGroup( group );
        for ( AssetNameKey key : assetNameKeysOfGroup ) {
            Asset asset = assets.get( key );
            if ( asset.loaded ) {
                Collection<AssetTypeKey> alsoDisposedAssets = disposeAsset( asset );
                if ( assetsToDeleteAlso == null ) {
                    assetsToDeleteAlso = alsoDisposedAssets;
                } else {
                    assetsToDeleteAlso.addAll( assetsToDeleteAlso );
                }
            } 
            delete( asset.typeKey );
        }
        
        if ( assetsToDeleteAlso != null ) {
            for ( AssetTypeKey key : assetsToDeleteAlso ) {
                delete( key );
            }
        }
    }
    
    public final boolean hasAsset( AssetNameKey key ) {
        return assets.containsKey( key );
    }
    
    public final boolean hasAsset( AssetTypeKey key ) {
        return typeMapping.containsKey( key );
    }
    
    public final int getAssetId( AssetNameKey key ) {
        AssetTypeKey assetTypeKey = getAssetTypeKey( key );
        if ( assetTypeKey == null ) {
            return -1;
        }
        
        return assetTypeKey.id;
    }
    
    public final int getAssetId( String group, String name ) {
        return getAssetId( new AssetNameKey( group, name ) );
    }
    
    public final boolean hasGroup( String group ) {
        for ( AssetNameKey key : assets.keySet() ) {
            if ( key.group.equals( group ) ) {
                return true;
            }
        }
        return false;
    }
    
    public final void clear() {
        for ( Asset asset : assets.values() ) {
            disposeAsset( asset );
        }
        
        assets.clear();
        typeMapping.clear();
    }

    @Override
    public final SystemComponentKey[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }
    
    public final Collection<AssetNameKey> getAssetNameKeysOfGroup( String group ) {
        Collection<AssetNameKey> assetsOfGroup = new ArrayList<AssetNameKey>();
        for ( AssetNameKey key : assets.keySet() ) {
            if ( key.group.equals( group ) ) {
                assetsOfGroup.add( key );
            }
        }
        return assetsOfGroup;
    }
    
    public final Collection<AssetTypeKey> getAssetTypeKeysOfGroup( String group ) {
        Collection<AssetTypeKey> assetsOfGroup = new ArrayList<AssetTypeKey>();
        for ( AssetNameKey key : assets.keySet() ) {
            if ( key.group.equals( group ) ) {
                assetsOfGroup.add( assets.get( key ).typeKey );
            }
        }
        return assetsOfGroup;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
            new AssetBuilderAdapter( this )
        };
    }

    private Collection<AssetTypeKey> disposeAsset( Asset asset ) {
        Collection<AssetTypeKey> toDisposeFirst = findeAssetsToDisposeFirst( asset.typeKey );
        if ( !toDisposeFirst.isEmpty() ) {
            for ( AssetTypeKey key : toDisposeFirst ) {
                Asset toDispose = typeMapping.get( key );
                if ( toDispose.loaded ) {
                    dispose( toDispose );
                }
            }
        }
        
        dispose( asset );
        return toDisposeFirst;
    }
    
    private Collection<AssetTypeKey> findeAssetsToDisposeFirst( AssetTypeKey typeKey ) {
        Set<AssetTypeKey> result = new HashSet<AssetTypeKey>();
        for ( Asset asset : assets.values() ) {
            AssetTypeKey[] disposeFirst = asset.dependsOn();
            if ( disposeFirst != null ) {
                for ( int i = 0; i < disposeFirst.length; i++ ) {
                    if ( typeKey.equals( disposeFirst[ i ] ) ) {
                        result.add( asset.typeKey );
                        break;
                    }
                }
            }
        }
        return result;
    }
    
    private void deleteAsset( Asset asset ) {
        if ( asset.loaded ) {
            Collection<AssetTypeKey> alsoDisposedAssets = disposeAsset( asset );
            if ( alsoDisposedAssets != null ) {
                for ( AssetTypeKey typeKey : alsoDisposedAssets ) {
                    delete( typeKey );
                }
            }
        }
        
        delete( asset.typeKey );
    }
    
    private void delete( AssetTypeKey assetKey ) {
        Asset deleted = typeMapping.remove( assetKey );
        if ( deleted == null ) {
            return;
        }
        
        assets.remove( new AssetNameKey( deleted.group, deleted.getName() ) );
        context.notify( new AssetEvent( deleted, AssetEvent.Type.ASSET_DELETED ) );
        deleted.dispose();
    }

    private void dispose( Asset asset ) {
        context.notify( new AssetEvent( asset, AssetEvent.Type.ASSET_DISPOSED ) );
        asset.loaded = false;
    }

    private void loadAsset( Asset asset ) {
        AssetTypeKey[] loadFirst = asset.dependsOn();
        if ( loadFirst != null ) {
            for ( int i = 0; i < loadFirst.length; i++ ) {
                Asset assetToLoadFirst = typeMapping.get( loadFirst[ i ] );
                if ( !assetToLoadFirst.loaded ) {
                    load( assetToLoadFirst );
                }
            }
        }
        
        load( asset );
    }
    
    private void load( Asset asset ) {
        context.notify( new AssetEvent( asset, AssetEvent.Type.ASSET_LOADED ) );
        asset.loaded = true;
    }
    
    private void checkAsset( AssetTypeKey key, Asset asset ) {
        if ( asset == null ) {
            throw new IllegalArgumentException( "No Asset found for: " + key );
        }
    }
    
    private void checkAsset( AssetNameKey key, Asset asset ) {
        if ( asset == null ) {
            throw new IllegalArgumentException( "No Asset found for: " + key );
        }
    }
    
    public final class AssetBuilder extends SystemComponentBuilder {
        
        private AssetBuilder() {}
        
        @Override
        public final SystemComponentKey systemComponentKey() {
            return Asset.TYPE_KEY;
        }

        @Override
        public int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            attributes.put( Component.INSTANCE_TYPE_NAME, componentType.getName() );
            Asset asset = getInstance( componentId );
            
            asset.fromAttributes( attributes );
            
            if ( asset.getName() == null ) {
                asset.setName( "New Asset" );
            }
            
            if ( StringUtils.isBlank( asset.group ) ) {
                asset.group = DEFAULT_GROUP_NAME;
            }
            
            AssetNameKey assetKey = new AssetNameKey( asset.group, asset.getName() );

            if ( hasAsset( assetKey ) ) {
                throw new ComponentCreationException( "There is already an Asset with key: " + assetKey + " registered for this AssetSystem" );
            }

            assets.put( assetKey, asset );
            typeMapping.put( asset.typeKey, asset );
            postInit( asset, context );
            
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
        public final SystemComponentKey componentTypeKey() {
            return Asset.TYPE_KEY;
        }
        @Override
        public final Asset get( int id, Class<? extends Asset> subtype ) {
            return getAsset( new AssetTypeKey( id, subtype ) );
        }
        @Override
        public final void delete( int id, Class<? extends Asset> subtype ) {
            deleteAsset( new AssetTypeKey( id, subtype ) );
        }
        
        @Override
        public final Iterator<Asset> getAll() {
            return assets.values().iterator();
        }
    }

}
