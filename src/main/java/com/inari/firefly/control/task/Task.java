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
package com.inari.firefly.control.task;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.SystemComponent;
import com.inari.firefly.system.utils.Trigger;
import com.inari.firefly.system.utils.Triggerer;

public abstract class Task extends SystemComponent {
    
    public static final SystemComponentKey<Task> TYPE_KEY = SystemComponentKey.create( Task.class );

    public static final AttributeKey<Boolean> REMOVE_AFTER_RUN = AttributeKey.createBoolean( "removeAfterRun", Task.class );
    public static final AttributeKey<DynArray<Trigger>> TRIGGER = AttributeKey.createDynArray( "trigger", Task.class, Trigger.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        REMOVE_AFTER_RUN,
        TRIGGER
    );
    
    public static final Triggerer TASK_TRIGGERER = new Triggerer() {
        @Override
        public final void trigger( FFContext context, int componentId ) {
            context.getSystem( TaskSystem.SYSTEM_KEY ).runTask( componentId );
        }
    };
    
    
    private boolean removeAfterRun;
    private final DynArray<Trigger> trigger;
    
    protected Task( int id ) {
        super( id );
        trigger = DynArray.create( Trigger.class, 1, 2 );
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

    public final void addTrigger( Trigger trigger ) {
        this.trigger.add( trigger );
        trigger.register( context, index(), TASK_TRIGGERER );
    }
    
    public final void removeTrigger( int index ) {
        Trigger remove = trigger.remove( index );
        if ( remove != null ) {
            remove.dispose( context );
        }
    }
    
    public final void clearTrigger() {
        for ( int i = 0; i < trigger.capacity(); i++ ) {
            if ( trigger.contains( i ) ) {
                removeTrigger( i );
            }
        }
    }

    

    @Override
    public Set<AttributeKey<?>> attributeKeys() {
        return JavaUtils.unmodifiableSet( super.attributeKeys(), ATTRIBUTE_KEYS );
    }

    @Override
    public void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        removeAfterRun = attributes.getValue( REMOVE_AFTER_RUN, removeAfterRun );
        if ( attributes.contains( TRIGGER ) ) {
            clearTrigger();
            DynArray<Trigger> triggers = attributes.getValue( TRIGGER );
            for ( Trigger trigger : triggers ) {
                addTrigger( trigger );
            }
        }
    }

    

    @Override
    public void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( REMOVE_AFTER_RUN, removeAfterRun );
        attributes.put( TRIGGER, trigger );
    }

    public abstract void runTask();

}
