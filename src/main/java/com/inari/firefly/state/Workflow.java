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
package com.inari.firefly.state;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.component.SystemComponent;

public final class Workflow extends SystemComponent {
    
    public static final SystemComponentKey<Workflow> TYPE_KEY = SystemComponentKey.create( Workflow.class );
    
    public static final AttributeKey<String> START_STATE_NAME = new AttributeKey<String>( "startStateName", String.class, Workflow.class );
    public static final AttributeKey<DynArray<String>> STATES = AttributeKey.createForDynArray( "states", Workflow.class );
    public static final AttributeKey<DynArray<StateChange>> STATE_CHANGES = AttributeKey.createForDynArray( "stateChanges", Workflow.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        START_STATE_NAME,
        STATES,
        STATE_CHANGES
    };
    
    private String startStateName;
    private DynArray<String> states;
    private DynArray<StateChange> stateChanges;
    
    private String currentStateName;

    Workflow( int workflowId ) {
        super( workflowId );
        startStateName = null;
        states = null;
        stateChanges = null;
        currentStateName = null;
    }
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }
    
    public final boolean isActive() {
        return currentStateName != null;
    }
    
    final void activate() {
        currentStateName = startStateName;
    }

    public final String getCurrentState() {
        return currentStateName;
    }
    
    final void setCurrentState( String stateName ) {
        currentStateName = stateName;
    }

    public final String getStartStateName() {
        return startStateName;
    }

    public final void setStartStateName( String startStateName ) {
        this.startStateName = startStateName;
    }

    public final DynArray<String> getStates() {
        return states;
    }

    public final void setStates( DynArray<String> states ) {
        this.states = states;
    }

    public final DynArray<StateChange> getStateChanges() {
        return stateChanges;
    }
    
    public final Iterator<StateChange> stateChangesForCurrentState() {
        return new CurrentStateChangeIterator();
    }

    public final void setStateChanges( DynArray<StateChange> stateChanges ) {
        this.stateChanges = stateChanges;
    }
    
    public final StateChange getStateChangeForTargetState( String targetStateName ) {
        for ( StateChange stateChange : stateChanges ) {
            if ( stateChange.getFromStateName().equals( currentStateName ) && targetStateName.equals( stateChange.toStateName ) ) {
                return stateChange;
            }
        }
        
        return null;
    }
    
    public final StateChange getStateChangeForCurrentState( String stateChangeName ) {
        for ( StateChange stateChange : stateChanges ) {
            if ( stateChangeName.equals( stateChange.getName() ) && stateChange.getFromStateName().equals( currentStateName ) ) {
                return stateChange;
            }
        }
        
        return null;
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
        states = attributes.getValue( STATES, states );
        stateChanges = attributes.getValue( STATE_CHANGES, stateChanges );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( START_STATE_NAME, startStateName );
        attributes.put( STATES, states );
        attributes.put( STATE_CHANGES, stateChanges );
    }
    
    private final class CurrentStateChangeIterator implements Iterator<StateChange> {
        
        private int nextIndex = -1;
        
        CurrentStateChangeIterator() {
            findNextIndex();
        }

        @Override
        public final boolean hasNext() {
            return stateChanges != null && nextIndex < stateChanges.capacity();
        }

        @Override
        public final StateChange next() {
            StateChange result = stateChanges.get( nextIndex );
            findNextIndex();
            return result;
        }

        @Override
        public final void remove() {
            throw new UnsupportedOperationException();
        }
        
        private final void findNextIndex() {
            nextIndex++;
            while( nextIndex < stateChanges.capacity() && !( stateChanges.contains( nextIndex ) && stateChanges.get( nextIndex ).fromStateName.equals( currentStateName ) ) ) {
                nextIndex++;
            }
        }
        
    }

}
