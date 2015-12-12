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
package com.inari.firefly.component.attr;

import com.inari.firefly.component.Component.ComponentKey;

public interface AttributeMap {
    
    ComponentKey getComponentKey();
    
    void setComponentKey( ComponentKey typeKey );

    boolean isEmpty();

    <A> A getValue( AttributeKey<A> key );

    <A> AttributeMap put( AttributeKey<A> key, A value );
    
    AttributeMap putAll( AttributeMap attributes );
    
    AttributeMap putUntyped( AttributeKey<?> key, Object value );

    int getValue( AttributeKey<Integer> key, int defaultValue );

    float getValue( AttributeKey<Float> key, float defaultValue );

    boolean getValue( AttributeKey<Boolean> key, boolean defaultValue );

    long getValue( AttributeKey<Long> key, long defaultValue );

    <A> A getValue( AttributeKey<A> key, A defaultValue );

    Object getUntypedValue( AttributeKey<?> key, Object defaultValue );

    void clear();

    boolean contains( AttributeKey<?> key );

}