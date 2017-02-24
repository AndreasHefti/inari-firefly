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
package com.inari.firefly.component.build;

import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.attr.Attribute;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public interface ComponentBuilder {

    ComponentBuilder clear();
    
    ComponentBuilder setAttributes( AttributeMap attributes );
    
    AttributeMap getAttributes();

    ComponentBuilder set( AttributeKey<Float> key, float value );
    ComponentBuilder set( AttributeKey<Integer> key, int value );
    ComponentBuilder set( AttributeKey<Long> key, long value );
    ComponentBuilder set( AttributeKey<Double> key, double value );
    <T> ComponentBuilder set( AttributeKey<T> key, T value );
    <T> ComponentBuilder set( AttributeKey<DynArray<T>> key, T value, int index );
    
    ComponentBuilder add( AttributeKey<IntBag> key, int value );
    <T> ComponentBuilder add( AttributeKey<DynArray<T>> key, T value );
    <T> ComponentBuilder add( AttributeKey<DynArray<T>> key, T value, Class<T> type );
    <T> ComponentBuilder add( AttributeKey<DynArray<T>> key, T[] values );
    <T> ComponentBuilder add( AttributeKey<DynArray<T>> key, T[] values, Class<T> type );
    <T> ComponentBuilder add( AttributeKey<DynArray<T>> key, DynArray<T> values );
    ComponentBuilder add( Attribute<?>... attributes );
    
    int build();
    void build( int componentId );
    
    int activate();
    void activate( int componentId );

    ComponentBuilder buildAndNext();
    ComponentBuilder buildAndNext( int componentId );

    ComponentBuilder activateAndNext();
    ComponentBuilder activateAndNext( int componentId );

}
