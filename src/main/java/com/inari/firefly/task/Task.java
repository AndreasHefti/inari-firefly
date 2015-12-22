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
package com.inari.firefly.task;

import java.util.Arrays;
import java.util.Set;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.SystemComponent;

public abstract class Task extends SystemComponent {
    
    public static final SystemComponentKey<Task> TYPE_KEY = SystemComponentKey.create( Task.class );

    public static final AttributeKey<Boolean> REMOVE_AFTER_RUN = new AttributeKey<Boolean>( "removeAfterRun", Boolean.class, Task.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        REMOVE_AFTER_RUN
    };
    
    private boolean removeAfterRun;
    
    protected Task( int id ) {
        super( id );
    }
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

    public final boolean removeAfterRun() {
        return removeAfterRun;
    }

    public final void setRemoveAfterRun( boolean removeAfterRun ) {
        this.removeAfterRun = removeAfterRun;
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
        
        removeAfterRun = attributes.getValue( REMOVE_AFTER_RUN, removeAfterRun );
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( REMOVE_AFTER_RUN, removeAfterRun );
    }

    public abstract void run( FFContext context );

}
