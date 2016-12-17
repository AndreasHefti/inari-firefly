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

import java.util.Set;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.component.SystemComponent;
import com.inari.firefly.system.external.FFTimer.UpdateScheduler;

public abstract class Controller extends SystemComponent {
    
    public static final SystemComponentKey<Controller> TYPE_KEY = SystemComponentKey.create( Controller.class );
    public static final AttributeKey<Float> UPDATE_RESOLUTION = new AttributeKey<Float>( "updateResolution", Float.class, Controller.class );

    private float updateResolution;
    
    private boolean active = true;
    private UpdateScheduler updateScheduler;
    protected final IntBag componentIds;
    
    
    protected Controller( int id ) {
        super( id );
        componentIds = new IntBag( 10, -1 );
        updateResolution = -1;
        updateScheduler = null;
    }
    
    public final boolean isActive() {
        return active;
    }
    
    public final void setActive( boolean active ) {
        this.active = active;
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
    
    public final void addComponentId( int componentId ) {
        componentIds.add( componentId );
    }
    
    public final void removeComponentId( int componentId ) {
        componentIds.remove( componentId );
    }
    
    @Override
    public Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.add( UPDATE_RESOLUTION );
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
    
    final void processUpdate() {
        if ( !active ) {
            return;
        }
        
        if ( updateResolution >= 0 ) {
            if ( updateScheduler == null ) {
                updateScheduler = context.getTimer().createUpdateScheduler( updateResolution );
            }
            if ( updateScheduler.needsUpdate() ) {
                update();
            }
            return;
        }
        
        update();
    }
    
    public abstract void update();

}
