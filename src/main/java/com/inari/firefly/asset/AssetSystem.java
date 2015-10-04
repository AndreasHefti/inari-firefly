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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.inari.commons.StringUtils;
import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.TypedKey;
import com.inari.firefly.asset.event.AssetEvent;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.ComponentBuilderHelper;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextInitiable;

public class AssetSystem implements FFContextInitiable, ComponentSystem, ComponentBuilderFactory {
    
    public static final TypedKey<AssetSystem> CONTEXT_KEY = TypedKey.create( "FF_ASSET_SYSTEM", AssetSystem.class );
    
    public static final String DEFAULT_GROUP_NAME = "FF_DEFAULT_ASSET_GROUP";
    
    private FFContext context;
    private IEventDispatcher eventDispatcher;
    
    private final Map<AssetNameKey, Asset> assets;
    private final Map<AssetTypeKey, Asset> typeMapping;
    
    
    AssetSystem() {
        assets = new LinkedHashMap<AssetNameKey, Asset>();
        typeMapping = new LinkedHashMap<AssetTypeKey, Asset>();
    }
    
    @Override
    public void init( FFContext context ) {
        this.context = context;
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
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

    @Override
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public final <C> ComponentBuilder<C> getComponentBuilder( Class<C> type ) {
        if ( !Asset.class.isAssignableFrom( type ) ) {
            throw new IllegalArgumentException( "The IComponentType is not a subtype of Asset." + type );
        }
        
        return new AssetBuilder( this, type, false );
    }

    public final <A extends Asset> AssetBuilder<A> getAssetBuilder( Class<A> assetType ) {
        return new AssetBuilder<A>( this, assetType, false );
    }
    
    public final <A extends Asset> AssetBuilder<A> getAssetBuilderWithAutoLoad( Class<A> assetType ) {
        return new AssetBuilder<A>( this, assetType, true );
    }
    
    public final void loadAsset( AssetNameKey key ) {
        Asset asset = getAsset( key );
        checkAsset( key, asset );
        loadAsset( asset );
    }
    
    public final void loadAssets( String group ) {
        checkGroup( group );
        for ( AssetNameKey assetKey : getAssetNameKeysOfGroup( group ) ) {
            Asset asset = assets.get( assetKey );
            if ( !asset.loaded ) {
                loadAsset( asset );
            }
        }
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
        checkAsset( key, asset );
        
        if ( !asset.loaded ) {
            return;
        }
        
        disposeAsset( asset );
    }
    
    public final void disposeAssets( String group ) {
        checkGroup( group );
        for ( AssetNameKey assetKey : getAssetNameKeysOfGroup( group ) ) {
            Asset asset = assets.get( assetKey );
            if ( asset.loaded ) {
                disposeAsset( asset );
            }
        }
    }
    
    public final void deleteAsset( AssetNameKey key ) {
        Asset asset = getAsset( key );
        checkAsset( key, asset );
        deleteAsset( asset );
    }
    
    public final void deleteAsset( AssetTypeKey key ) {
        Asset asset = getAsset( key );
        checkAsset( key, asset );
        deleteAsset( asset );
    }
    
    public final void deleteAssets( String group ) {
        checkGroup( group );
        Collection<AssetTypeKey> assetsToDeleteAlso = null;
        for ( AssetNameKey key : getAssetNameKeysOfGroup( group ) ) {
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
        
        for ( AssetTypeKey key : assetsToDeleteAlso ) {
            delete( key );
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
    
    private static final Set<Class<?>> SUPPORTED_COMPONENT_TYPES = new HashSet<Class<?>>();
    @Override
    public final Set<Class<?>> supportedComponentTypes() {
        if ( SUPPORTED_COMPONENT_TYPES.isEmpty() ) {
            SUPPORTED_COMPONENT_TYPES.add( Asset.class );
        }
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final void fromAttributes( Attributes attributes ) {
        fromAttributes( attributes, BuildType.CLEAR_OLD );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public final void fromAttributes( Attributes attributes, BuildType buildType ) {
        if ( buildType == BuildType.CLEAR_OLD ) {
            clear();
        }
        
        for ( final Class<? extends Asset> assetSubType : attributes.getAllSubTypes( Asset.class ) ) {
            new ComponentBuilderHelper<Asset>() {
                @Override
                public Asset get( int id ) {
                    return getAsset( new AssetTypeKey( id, assetSubType ) );
                }
                @Override
                public void delete( int id ) {
                    deleteAsset( new AssetTypeKey( id, assetSubType ) );
                }
            }.buildComponents( Asset.class, buildType, (AssetBuilder<Asset>) getAssetBuilder( assetSubType ), attributes );
        }
    }

    @Override
    public final void toAttributes( Attributes attributes ) {
        for ( Asset asset : assets.values() ) {
            ComponentBuilderHelper.toAttributes( attributes, asset.indexedObjectType(), asset );
        }
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
        eventDispatcher.notify( new AssetEvent( deleted, AssetEvent.Type.ASSET_DELETED ) );
    }

    private void dispose( Asset asset ) {
        eventDispatcher.notify( new AssetEvent( asset, AssetEvent.Type.ASSET_DISPOSED ) );
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
        eventDispatcher.notify( new AssetEvent( asset, AssetEvent.Type.ASSET_LOADED ) );
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
    
    private void checkGroup( String group ) {
        if ( !hasGroup( group ) ) {
            throw new IllegalArgumentException( "No group found: " + group );
        }
    }
    
    public final class AssetBuilder<A extends Asset> extends BaseComponentBuilder<A> {
        
        private final Class<A> assetType;
        private final boolean autoLoad;
        
        private AssetBuilder( AssetSystem system, Class<A> assetType, boolean autoLoad ) {
            super( system );
            this.assetType = assetType;
            this.autoLoad = autoLoad;
        }

        @Override
        public A build( int componentId ) {
            attributes.put( Component.INSTANCE_TYPE_NAME, assetType.getName() );
            A asset = getInstance( componentId );
            
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
            
            eventDispatcher.notify( new AssetEvent( asset, AssetEvent.Type.ASSET_CREATED ) );
            
            if ( autoLoad ) {
                load( asset );
            }
            
            return asset;
        }
    }

}
