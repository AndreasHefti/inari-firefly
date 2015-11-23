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
package com.inari.firefly.state;

import java.util.Arrays;
import java.util.Set;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.component.SystemComponent;

public final class Workflow extends SystemComponent {
    
    public static final SystemComponentKey TYPE_KEY = SystemComponentKey.create( Workflow.class );
    
    public static final AttributeKey<String> START_STATE_NAME = new AttributeKey<String>( "startStateName", String.class, Workflow.class );
    public static final AttributeKey<Integer> INIT_TASK_ID = new AttributeKey<Integer>( "initTaskId", Integer.class, Workflow.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        START_STATE_NAME,
        INIT_TASK_ID
    };
    
    private String startStateName;
    private int initTaskId;
    private int currentStateId;

    Workflow( int workflowId ) {
        super( workflowId );
        startStateName = null;
        initTaskId = -1;
        currentStateId = -1;
    }
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }
    
    public final boolean isActive() {
        return currentStateId >= 0;
    }
    
    final void activate( int startStateId ) {
        currentStateId = startStateId;
    }

    public final int getCurrentStateId() {
        return currentStateId;
    }

    final void setCurrentStateId( int currentStateId ) {
        this.currentStateId = currentStateId;
    }

    public final String getStartStateName() {
        return startStateName;
    }

    public final void setStartStateName( String startStateName ) {
        this.startStateName = startStateName;
    }

    public final int getInitTaskId() {
        return initTaskId;
    }

    public final void setInitTaskId( int initTaskId ) {
        this.initTaskId = initTaskId;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( Arrays.asList( ATTRIBUTE_KEYS ) );
        return attributeKeys;
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        startStateName = attributes.getValue( START_STATE_NAME, startStateName );
        initTaskId = attributes.getValue( INIT_TASK_ID, initTaskId );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        attributes.put( START_STATE_NAME, startStateName );
        attributes.put( INIT_TASK_ID, initTaskId );
    }

}
