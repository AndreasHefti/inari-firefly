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
package com.inari.firefly.control.state;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;
import com.inari.firefly.system.component.Activation;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentMap;
import com.inari.firefly.system.external.FFTimer;
import com.inari.firefly.system.utils.Condition;

public class StateSystem
    extends
        ComponentSystem<StateSystem>
    implements 
        UpdateEventListener {
    
    public static final FFSystemTypeKey<StateSystem> SYSTEM_KEY = FFSystemTypeKey.create( StateSystem.class );
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        Workflow.TYPE_KEY
    );

    private final SystemComponentMap<Workflow> workflows;


    public StateSystem() {
        super( SYSTEM_KEY );
        workflows = SystemComponentMap.create( 
            this, Workflow.TYPE_KEY,
            new Activation() {
                public final void activate( int id ) { activateWorkflow( id ); }
                public final void deactivate( int id ) {}
            },
            20, 10 
        ); 
    }
    
    @Override
    public void init( FFContext context ) {
        super.init( context );
        
        context.registerListener( UpdateEvent.TYPE_KEY, this );
        context.registerListener( StateSystemEvent.TYPE_KEY, this );
    }
    
    public final SystemComponentMap<Workflow> workflowMap() {
        return workflows;
    }
    
    public final String getCurrentState( int workflowId ) {
        return workflows.get( workflowId ).getCurrentState();
    }
    
    public final String getCurrentState( String workflowName ) {
        return getCurrentState( workflows.getId( workflowName ) );
    }
    
    public final void activateWorkflow( int workflowId ) {
        final Workflow workflow = workflows.get( workflowId );
        if ( workflow == null || workflow.isActive() ) {
            return;
        }
        
        workflow.activate();
        context.notify( WorkflowEvent.createWorkflowStartedEvent( workflow.index(), workflow.getName(), workflow.getCurrentState() ) );
    }

    public final void update( final FFTimer timer ) {
        
        for ( int i = 0; i < workflows.map.capacity(); i++ ) {
            Workflow workflow = workflows.get( i );
            if ( workflow == null || !workflow.isActive() ) {
                continue;
            }
            
            DynArray<StateChange> stateChangesOfCurrentState = workflow.getStateChangesOfCurrentState();
            for ( int s = 0; s < stateChangesOfCurrentState.size(); s++ ) {
                StateChange stateChange = stateChangesOfCurrentState.get( s );
                Condition condition = stateChange.getCondition();
                if ( condition == null ) {
                    continue;
                }
                
                if ( condition.check( context ) ) {
                    doStateChange( workflow, stateChange );
                    break;
                }
            }
        }
    }
    
    final void doStateChange( final int workflowId, final String stateChangeName ) {
        final Workflow workflow = workflows.get( workflowId );
        doStateChange( workflow, workflow.getStateChangeForCurrentState( stateChangeName ) );
    }
    
    final void changeState( final int workflowId, final String targetStateName ) {
        final Workflow workflow = workflows.get( workflowId );
        doStateChange( workflow, workflow.getStateChangeForTargetState( targetStateName ) );
    }

    private final void doStateChange( final Workflow workflow, final StateChange stateChange ) {
        String toStateName = stateChange.getToStateName();
        workflow.changeState( toStateName );

        if ( toStateName != null ) {
            context.notify( WorkflowEvent.createStateChangedEvent( workflow.index(), workflow.getName(), stateChange ) );
        } else {
            context.notify( WorkflowEvent.createWorkflowFinishedEvent( workflow.index(), workflow.getName(), stateChange ) );
        }
    }

    public final void dispose( FFContext context ) {
        context.disposeListener( UpdateEvent.TYPE_KEY, this );
        context.disposeListener( StateSystemEvent.TYPE_KEY, this );
        
        clearSystem();
    }

    public final void clearSystem() {
        workflows.clear();
    }

    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            workflows.getBuilderAdapter()
        );
    }

}
