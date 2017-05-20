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

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.system.component.SystemComponent;
import com.inari.firefly.system.utils.Disposable;
import com.inari.firefly.system.utils.Loadable;

/** The base definition for all Asset implementation.
 * 
 *  A definition for a resource that can be loaded and disposed within the Asset instance. 
 *  On load an Asset gets an instance-id (or a list of instance ids) that can be used in Components or Entities to refer to an Asset resource instance to use.
 *  
 *  For Example a TextureAsset can be defined within a resource path to load an image for the texture 
 *  and some texture based attributes. A load call then loads the image with the specified attributes 
 *  into the GPU and propagate the texture id form GPU within the instanceId of the TextureAsset. 
 *  A call on dispose deletes the texture form GPU and frees the memory and sets the instanceId if the Asset back to undefined (negative integer).
 *  
 *  This is in general a SystemComponent and works with AssetSystem.
 *  An Asset can be built by using the SystemComponent builder within FFContext ( or AssetSystem )
 *  by using the specified TYPE_KEY and also specify an concrete implementation class.
 *  
 *  <code>
 *  
 *      int assetId = context.getComponentBuilder( Asset.TYPE_KEY, TextureAsset.class )
 *           .set( TextureAsset.RESOURCE_NAME, "someResourceName" )
 *           .build();
 *       
 *      // get
 *      TextureAsset asset = context.getSystemComponent( Asset.TYPE_KEY, assetId, TextureAsset.class );
 *      // load
 *      context.activateSystemComponent( Asset.TYPE_KEY, assetId ); 
 *       // dispose
 *       context.deactivateSystemComponent( Asset.TYPE_KEY, assetId ); 
 *       // delete
 *       context.deleteSystemComponent( Asset.TYPE_KEY, assetId );
 *       
 *  </code>
 *
 */
public abstract class Asset extends SystemComponent implements Loadable, Disposable {
    
    /** The TYPE_KEY that defines a SystemComponent of type Asset. All sub-types of Asset uses this same TYPE_KEY */
    public static final SystemComponentKey<Asset> TYPE_KEY = SystemComponentKey.create( Asset.class );
    
    protected boolean loaded = false;
    protected int dependsOn = -1;
    
    /** This should not be called directly. Instead the ComponentBuilder should be used. 
     *  See class documentation for example.
     *  
     *  @param assetIntId The component id is being injected by the builder on creation.
     */
    protected Asset( int assetIntId ) {
        super( assetIntId );
    }
    
    /** Use this to get the instance id of a loaded Asset.
     *  If the Asset implementation has more then one instance id when loaded this can be used anyway
     *  And always gives the first instance id from the list.
     *  
     *  Not every implementation of an Asset produces an instance id on load but normally
     *  there is one or more Component created on load and therefore also instance id(s) for this Component(s)
     *  If a Asset implementation do not produce an instance id on load this should be clarified on documentation.
     *  
     *  @return The instance id of the Asset when loaded and if there is one. 
     */
    public final int getInstanceId() {
        return getInstanceId( 0 );
    }
    
    /** Use this if a concrete Asset implementation uses a list of instance ids.
     * 
     * @param index The index of the instance id in the list to get.
     * @return the requested instance id of the Asset when loaded or undefined (negative integer) if not loaded.
     */
    public abstract int getInstanceId( int index );
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

    @Override
    public final boolean isLoaded() {
        return loaded;
    }

    /** Indicates if this Asset depends on an other asset. Gives the id of the Asset this depends on or 
     *  negative integer if this depends on no other Asset.
     *  
     *  If this Asset depends on another asset:
     *  1. The other asset has to be loaded first.
     *  2. This Asset has to be disposed or deleted first if the other Asset gets disposed or deleted.
     *  The Asset system will take care of that.
     *  
     * @return positive integer that is the id of the Asset this depends on. Or negative integer if there is no dependency
     */
    public final int dependsOn() {
        return dependsOn;
    }

    /** Used to set dependsOn from implemeting Classes. */
    protected final void dependsOn( int dependsOn ) {
        this.dependsOn = dependsOn;
    }

    /** Use this to check and stop if the Asset is not already loaded.
     *  @throws IllegalStateException if the Asset is not loaded.
     */
    protected void checkNotAlreadyLoaded() {
        if ( loaded ) {
            throw new IllegalStateException( "Asset: " + componentId() + " is already loaded and can not be modified" );
        } 
    }

}
