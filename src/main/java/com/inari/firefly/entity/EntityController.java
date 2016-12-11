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
package com.inari.firefly.entity;

import com.inari.firefly.control.Controller;

public abstract class EntityController extends Controller {

    protected EntityController( int id ) {
        super( id );
    }
    
    public void entityActivated( int entityId ) {
        if ( hasControllerId( entityId ) ) {
            componentIds.add( entityId );
        }
    }
    
    public void entityDeactivated( int entityId ) {
        componentIds.remove( entityId );
    }
    
    public void init( int entityId ) {
        
    }
    
    @Override
    public void update() {
        for ( int i = 0; i < componentIds.length(); i++ ) {
            int entityId = componentIds.get( i );
            if ( entityId >= 0 ) {
                update( entityId );
            }
        }
    }

    protected abstract void update( int entityId );
    
    private final boolean hasControllerId( int entityId ) {
        EEntity controllerComponent = context.getEntityComponent( entityId, EEntity.TYPE_KEY );
        if ( controllerComponent == null ) {
            return false;
        }
        
        return controllerComponent.controlledBy( index );
    }

    public void initEntity( EntityAttributeMap attributes ) {
        // NOOP for default
    }
}
