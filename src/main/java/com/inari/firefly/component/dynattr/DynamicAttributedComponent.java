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
package com.inari.firefly.component.dynattr;

import com.inari.firefly.component.Component;
import com.inari.firefly.component.attr.AttributeKey;

public interface DynamicAttributedComponent extends Component {
    
    <A> void setDynamicAttribute( AttributeKey<A> key, A value );
    
    <A> A getDynamicAttribute( AttributeKey<A> key );
    
    public boolean hasDynamicAttributes();
    
    @Override
    public Class<? extends DynamicAttributedComponent> componentType();
}
