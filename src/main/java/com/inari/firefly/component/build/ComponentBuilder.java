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
package com.inari.firefly.component.build;

import com.inari.firefly.component.attr.Attribute;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public interface ComponentBuilder<C> {
    
    ComponentBuilder<C> clear();
    
    ComponentBuilder<C> setAttributes( AttributeMap attributes );
    
    AttributeMap getAttributes();
    
    ComponentBuilder<C> set( AttributeKey<?> key, Object value );
    ComponentBuilder<C> set( AttributeKey<Float> key, float value );
    ComponentBuilder<C> set( AttributeKey<Integer> key, int value );
    ComponentBuilder<C> set( AttributeKey<Long> key, long value );
    ComponentBuilder<C> set( AttributeKey<Double> key, double value );
    ComponentBuilder<C> set( Attribute... attributes );
    
    C build();
    
    C build( int componentId );
    
    ComponentBuilder<C> buildAndNext();
    
    <CC> ComponentBuilder<CC> buildAndNext( Class<CC> componentType );
    
    ComponentBuilder<C> buildAndNext( int componentId );
    
    <CC> ComponentBuilder<CC> buildAndNext( int componentId, Class<CC> componentType );

}
