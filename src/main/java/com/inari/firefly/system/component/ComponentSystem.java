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
package com.inari.firefly.system.component;

import java.util.Set;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.FFInitException;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.utils.Disposable;

public abstract class ComponentSystem<T extends ComponentSystem<T>> implements FFSystem {
    
    public enum BuildType {
        CLEAR_OLD,
        OVERWRITE,
        MERGE_ATTRIBUTES
    }
    
    protected final FFSystemTypeKey<T> systemKey;
    protected FFContext context;
    
    protected ComponentSystem( FFSystemTypeKey<T> systemKey ) {
        this.systemKey = systemKey;
    }
    
    @Override
    public final FFSystemTypeKey<T> systemTypeKey() {
        return systemKey;
    }

    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return systemKey;
    }
    
    @Override
    public void init( FFContext context ) throws FFInitException {
        this.context = context;
    }
    
    protected final void disposeSystemComponent( SystemComponent component ) {
        if ( component == null ) {
            return;
        }
        
        if ( component instanceof Disposable ) {
            ( (Disposable) component ).dispose( context );
        }

        component.dispose();
    }

    public abstract Set<SystemComponentKey<?>> supportedComponentTypes();
   
    public abstract Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter();
    
    public abstract void clearSystem();

}
