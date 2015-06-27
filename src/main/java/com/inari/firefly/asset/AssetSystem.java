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

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.inari.commons.StringUtils;
import com.inari.commons.event.IEventDispatcher;
import com.inari.firefly.FFContext;
import com.inari.firefly.asset.event.AssetEvent;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.ComponentBuilderHelper;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.component.event.ComponentSystemEvent;
import com.inari.firefly.component.event.ComponentSystemEvent.Type;
import com.inari.firefly.system.FFSystem;

public final class AssetSystem implements FFSystem, ComponentSystem, ComponentBuilderFactory {
    
    public static final String DEFAULT_GROUP_NAME = "FF_DEFAULT_ASSET_GROUP";
    
    private IEventDispatcher eventDispatcher;
    private final Map<String, Map<String, Asset>> groupNameMapping;
    
    
    public AssetSystem() {
        groupNameMapping = new LinkedHashMap<String, Map<String, Asset>>();
    }
    
    @Override
    public void init( FFContext context ) {
        eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
        
        eventDispatcher.notify( 
            new ComponentSystemEvent( Type.INITIALISED, this ) 
        );
    }

    @Override
    public final void dispose( FFContext context ) {
        eventDispatcher.notify( 
            new ComponentSystemEvent( Type.DISPOSED, this ) 
        );
        
        clear();
    }

    @Override
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public final <C> ComponentBuilder<C> getComponentBuilder( Class<C> type ) {
        if ( !Asset.class.isAssignableFrom( type ) ) {
            throw new IllegalArgumentException( "The IComponentType is not a subtype of Asset." + type );
        }
        
        return new AssetBuilder( this, type );
    }

    public final <A extends Asset> AssetBuilder<A> getAssetBuilder( Class<A> assetType ) {
        return new AssetBuilder<A>( this, assetType );
    }
    
    public final void loadAsset( String group, String name ) {
        Asset asset = getAsset( group, name );
        
        if ( asset.loaded ) {
            return;
        }
        
        loadAsset( asset );
    }
    
    public final void loadAssets( String group ) {
        Map<String, Asset> nameMapping = getNameMappings( group );
        
        for ( Asset asset : nameMapping.values() ) {
            if ( !asset.loaded ) {
                loadAsset( asset );
            }
        }
    }
    
    public final boolean isAssetLoaded( String group, String name ) {
        Asset asset = getAsset( group, name );
        return asset.loaded;
    }
    
    public final void disposeAsset( String group, String name ) {
        Asset asset = getAsset( group, name );
        
        if ( !asset.loaded ) {
            return;
        }
        
        disposeAsset( asset );
    }
    
    public final void disposeAssets( String group ) {
        Map<String, Asset> nameMapping = getNameMappings( group );
        
        for ( Asset asset : nameMapping.values() ) {
            if ( asset.loaded ) {
                disposeAsset( asset );
            }
        }
    }
    
    public final void deleteAsset( String group, String name ) {
        Asset asset = getAsset( group, name );
        if ( asset.loaded ) {
            Collection<Asset> alsoDisposedAssets = disposeAsset( asset );
            if ( alsoDisposedAssets != null ) {
                for ( Asset alsoDisposedAsset : alsoDisposedAssets ) {
                    delete( alsoDisposedAsset.group, alsoDisposedAsset.getName() );
                }
            }
        }
        
        delete( asset.group, asset.getName() );
    }

    public final void deleteAssets( String group ) {
        Map<String, Asset> nameMapping = getNameMappings( group );
        
        Collection<Asset> assetsToDeleteAlso = null;
        for ( Asset asset : nameMapping.values() ) {
            if ( asset.loaded ) {
                Collection<Asset> alsoDisposedAssets = disposeAsset( asset );
                if ( assetsToDeleteAlso == null ) {
                    assetsToDeleteAlso = alsoDisposedAssets;
                } else {
                    assetsToDeleteAlso.addAll( assetsToDeleteAlso );
                }
            } 
            delete( asset.group, asset.getName() );
        }
        
        for ( Asset alsoDisposedAsset : assetsToDeleteAlso ) {
            delete( alsoDisposedAsset.group, alsoDisposedAsset.getName() );
        }
    }
    
    public final boolean hasAsset( String group, String name ) {
        if ( !hasGroup( group ) ) {
            return false;
        }
        
        Map<String, Asset> assetOfGroup = groupNameMapping.get( group );
        return assetOfGroup.containsKey( name );
    }
    
    public final boolean hasGroup( String group ) {
        return groupNameMapping.containsKey( group );
    }
    
    private final void deleteAsset( int assetId ) {
        Asset asset = getAsset( assetId );
        if ( asset != null ) {
            deleteAsset( asset.group, asset.getName() );
        }
    }

    private final Asset getAsset( int componentId ) {
        for ( Map<String, Asset> assetsOfGroup : groupNameMapping.values() ) {
            for ( Asset asset : assetsOfGroup.values() ) {
                if ( componentId == asset.indexedId() ) {
                    return asset;
                }
            }
        }
        
        return null;
    }
    
    public final void clear() {
        for ( Map<String, Asset> assetMap : groupNameMapping.values() ) {
            for ( Asset asset : assetMap.values() ) {
                disposeAsset( asset );
            }
        }
        
        groupNameMapping.clear();
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
        
        for ( Class<? extends Asset> assetSubType : attributes.getAllSubTypes( Asset.class ) ) {
            new ComponentBuilderHelper<Asset>() {
                @Override
                public Asset get( int id ) {
                    return getAsset( id );
                }
                @Override
                public void delete( int id ) {
                    deleteAsset( id );
                }
            }.buildComponents( Asset.class, buildType, (AssetBuilder<Asset>) getAssetBuilder( assetSubType ), attributes );
        }
    }

    @Override
    public final void toAttributes( Attributes attributes ) {
        for ( Map<String, Asset> assetMap : groupNameMapping.values() ) {
            for ( Asset asset : assetMap.values() ) {
                ComponentBuilderHelper.toAttributes( attributes, asset.getIndexedObjectType(), asset );
            }
        }
    }
    
    private Asset getAsset( String group, String name ) {
        if ( !hasAsset( group, name ) ) {
            throw new IllegalArgumentException( "No Asset found for group: " + group + " name: " + name );
        }
        
        Map<String, Asset> nameMapping = groupNameMapping.get( group );
        return nameMapping.get( name );
    }
    
    private Collection<Asset> disposeAsset( Asset asset ) {
        Collection<Asset> toDisposeFirst = findeAssetsToDisposeFirst( asset.indexedId() );
        if ( !toDisposeFirst.isEmpty() ) {
            for ( Asset toDispose : toDisposeFirst ) {
                if ( toDispose.loaded ) {
                    dispose( toDispose );
                }
            }
        }
        
        dispose( asset );
        return toDisposeFirst;
    }
    
    private Collection<Asset> findeAssetsToDisposeFirst( int id ) {
        Set<Asset> result = new HashSet<Asset>();
        for ( Map<String, Asset> nameMapping : groupNameMapping.values() ) {
            for ( Asset asset : nameMapping.values() ) {
                int[] disposeFirst = asset.dependsOn();
                if ( disposeFirst != null ) {
                    for ( int i = 0; i < disposeFirst.length; i++ ) {
                        if ( id == disposeFirst[ i ] ) {
                            result.add( asset );
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }
    
    private void delete( String group, String name ) {
        Map<String, Asset> nameMapping = getNameMappings( group );
        Asset deleted = nameMapping.remove( name );
        eventDispatcher.notify( new AssetEvent( deleted, AssetEvent.Type.ASSET_DELETED ) );
        if ( nameMapping.isEmpty() ) {
            groupNameMapping.remove( group );
        }
    }

    private void dispose( Asset asset ) {
        eventDispatcher.notify( new AssetEvent( asset, AssetEvent.Type.ASSET_DISPOSED ) );
        asset.loaded = false;
    }

    private void loadAsset( Asset asset ) {
        int[] loadFirst = asset.dependsOn();
        if ( loadFirst != null ) {
            for ( int i = 0; i < loadFirst.length; i++ ) {
                Asset assetToLoadFirst = getAssetForId( asset.group, loadFirst[ i ] );
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
    
    private Asset getAssetForId( String mostPossibleGroup, int id ) {
        Map<String, Asset> forGroup = groupNameMapping.get( mostPossibleGroup );
        if ( forGroup != null ) {
            for ( Asset asset : forGroup.values() ) {
                if ( asset.indexedId() == id ) {
                    return asset;
                }
            }
        }
        
        for ( Map<String, Asset> nameMapping : groupNameMapping.values() ) {
            for ( Asset asset : nameMapping.values() ) {
                if ( asset.indexedId() == id ) {
                    return asset;
                }
            }
        }
        
        throw new IllegalArgumentException( "Unable to find Asset for id: " + id );
    }
    
    private Map<String, Asset> getNameMappings( String group ) {
        Map<String, Asset> nameMapping = groupNameMapping.get( group );
        if ( nameMapping == null ) {
            throw new IllegalArgumentException( "No group found: " + group  );
        }
        return nameMapping;
    }

    private Map<String, Asset> getOrCreateNameMapForGroup( String group ) {
        Map<String, Asset> result = groupNameMapping.get( group );
        if ( result == null ) {
            result = new LinkedHashMap<String, Asset>();
            groupNameMapping.put( group, result );
        }
        
        return result;
    }

    
    public final class AssetBuilder<A extends Asset> extends BaseComponentBuilder<A> {
        
        private final Class<A> assetType;
        
        private AssetBuilder( AssetSystem system, Class<A> assetType ) {
            super( system );
            this.assetType = assetType;
        }

        @Override
        protected A createInstance( Constructor<A> constructor, Object... paramValues ) throws Exception {
            return constructor.newInstance( paramValues );
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

            if ( hasAsset( asset.group, asset.getName() ) ) {
                throw new ComponentCreationException( "There is already an Asset with name: " + asset.getName() + " registered for this AssetSystem" );
            }

            Map<String, Asset> nameMapping = getOrCreateNameMapForGroup( asset.group );
            nameMapping.put( asset.getName(), asset );
            
            eventDispatcher.notify( new AssetEvent( asset, AssetEvent.Type.ASSET_CREATED ) );
            return asset;
        }
    }

}
