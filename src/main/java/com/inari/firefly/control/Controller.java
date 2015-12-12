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
package com.inari.firefly.control;

import java.util.Arrays;
import java.util.Set;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.Disposable;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFTimer;
import com.inari.firefly.system.FFTimer.UpdateScheduler;
import com.inari.firefly.system.component.SystemComponent;

public abstract class Controller extends SystemComponent implements Disposable {
    
    public static final SystemComponentKey TYPE_KEY = SystemComponentKey.create( Controller.class );
    
    public static final AttributeKey<Float> UPDATE_RESOLUTION = new AttributeKey<Float>( "updateResolution", Float.class, Controller.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        UPDATE_RESOLUTION
    };
    
    private float updateResolution;
    
    private UpdateScheduler updateScheduler;
    protected final IntBag componentIds;
    
    protected Controller( int id ) {
        super( id );
        // TODO check if this is a proper init
        componentIds = new IntBag( 10, -1 );
        updateResolution = -1;
        updateScheduler = null;
    }
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

    public final float getUpdateResolution() {
        return updateResolution;
    }

    public final void setUpdateResolution( float updateResolution ) {
        this.updateResolution = updateResolution;
    }

    @Override
    public final Class<Controller> indexedObjectType() {
        return Controller.class;
    }
    
    @Override
    public final Class<Controller> componentType() {
        return Controller.class;
    }
    
    public final void addComponentId( int componentId ) {
        componentIds.add( componentId );
    }
    
    public final void removeComponentId( int componentId ) {
        componentIds.remove( componentId );
    }
    
    @Override
    public Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( Arrays.asList( ATTRIBUTE_KEYS ) );
        return attributeKeys;
    }

    @Override
    public void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        updateResolution = attributes.getValue( UPDATE_RESOLUTION, updateResolution );
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( UPDATE_RESOLUTION, updateResolution );
    }
    
    final void processUpdate( final FFTimer timer ) {
        if ( updateResolution >= 0 ) {
            if ( updateScheduler == null ) {
                updateScheduler = timer.createUpdateScheduler( updateResolution );
            }
            if ( updateScheduler.needsUpdate() ) {
                update( timer );
            }
            return;
        }
        
        update( timer );
    }
    
    public abstract void update( final FFTimer timer );

}
