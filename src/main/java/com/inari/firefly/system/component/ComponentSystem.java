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
package com.inari.firefly.system.component;

import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public abstract class ComponentSystem implements FFSystem {
    
    public enum BuildType {
        CLEAR_OLD,
        OVERWRITE,
        MERGE_ATTRIBUTES
    }
    
    protected FFContext context;
    
    @Override
    public void init( FFContext context ) throws FFInitException {
        this.context = context;
    }
    
    public ComponentBuilder getComponentBuilder( SystemComponentKey componentTypeKey ) {
        return context.getComponentBuilder( componentTypeKey );
    }

    public abstract SystemComponentKey[] supportedComponentTypes();
   
    public abstract SystemBuilderAdapter<?>[] getSupportedBuilderAdapter();
    
//    void fromAttributes( Attributes attributes );
//    
//    void fromAttributes( Attributes attributes, BuildType buildType );
//    
//    void toAttributes( Attributes attributes );
    
    public abstract void clear();

}
