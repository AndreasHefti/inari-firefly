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

import java.util.Iterator;

import com.inari.commons.lang.indexed.Indexer;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.utils.Condition;
import com.inari.firefly.system.component.SystemComponentBuilder;

public class StateSystem
    extends
        ComponentSystem<StateSystem>
    implements 
        UpdateEventListener {
    
    public static final FFSystemTypeKey<StateSystem> SYSTEM_KEY = FFSystemTypeKey.create( StateSystem.class );
    
    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        Workflow.TYPE_KEY,
    };

    private final DynArray<Workflow> workflows;


    public StateSystem() {
        super( SYSTEM_KEY );
        workflows = new DynArray<Workflow>( Indexer.getIndexedObjectSize( Workflow.class ) );
    }
    
    @Override
    public void init( FFContext context ) {
        super.init( context );
        
        context.registerListener( UpdateEvent.TYPE_KEY, this );
        context.registerListener( StateSystemEvent.TYPE_KEY, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( UpdateEvent.TYPE_KEY, this );
        context.disposeListener( StateSystemEvent.TYPE_KEY, this );
        
        clear();
    }

    public final void clear() {
        for ( Workflow workflow : workflows ) {
            deleteWorkflow( workflow.index() );
        }
        
        workflows.clear();
    }
    
    public void activateWorkflow( int workflowId ) {
        final Workflow workflow = workflows.get( workflowId );
        if ( workflow == null || workflow.isActive() ) {
            return;
        }
        
        workflow.activate();
        context.notify( WorkflowEvent.createWorkflowStartedEvent( workflow.index(), workflow.getName(), workflow.getCurrentState() ) );
    }

    @Override
    public final void update( UpdateEvent event ) {
        
        for ( Workflow workflow : workflows ) {
            if ( !workflow.isActive() ) {
                continue;
            }
            
            Iterator<StateChange> stateChanges = workflow.stateChangesForCurrentState();
            if ( stateChanges == null ) {
                continue;
            }
            
            while ( stateChanges.hasNext() ) {
                StateChange stateChange = stateChanges.next();
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
    
    public final void doStateChange( final int workflowId, final String stateChangeName ) {
        Workflow workflow = getWorkflow( workflowId );
        doStateChange( workflow, workflow.getStateChangeForCurrentState( stateChangeName ) );
    }
    
    public final void changeState( final int workflowId, final String targetStateName ) {
        Workflow workflow = getWorkflow( workflowId );
        doStateChange( workflow, workflow.getStateChangeForTargetState( targetStateName ) );
    }

    private final void doStateChange( final Workflow workflow, final StateChange stateChange ) {
        String toStateName = stateChange.getToStateName();
        workflow.setCurrentState( toStateName );
        
        if ( toStateName != null ) {
            context.notify( WorkflowEvent.createStateChangedEvent( workflow.index(), workflow.getName(), stateChange ) );
        } else {
            context.notify( WorkflowEvent.createWorkflowFinishedEvent( workflow.index(), workflow.getName(), stateChange ) );
        }
    }
    
    public final boolean deleteWorkflow( String name ) {
        Workflow workflow = getWorkflow( name );
        if ( workflow == null ) {
            return false;
        }
        
        deleteWorkflow( workflow.index() );
        return true;
    }
    
    public final Workflow getWorkflow( int workflowId ) {
        return workflows.get( workflowId );
    }

    public final Workflow getWorkflow( String name ) {
        if ( name == null ) {
            return null;
        }
        for ( Workflow workflow : workflows ) {
            if ( name.equals( workflow.getName() ) ) {
                return workflow;
            }
        }
        
        return null;
    }
    
    public final int getWorkflowId( String name ) {
        for ( Workflow workflow : workflows ) {
            if ( workflow.getName().equals( name ) ) {
                return workflow.index();
            }
        }
        
        return -1;
    }

    public final void deleteWorkflow( int indexedId ) {
        Workflow workflow = workflows.remove( indexedId );
        if ( workflow == null ) {
            return;
        }
        
        workflow.dispose();
    }
    
    public final String getCurrentState( int workflowId ) {
        if ( !workflows.contains( workflowId ) ) {
            return null;
        }
        
        return workflows.get( workflowId ).getCurrentState();
    }
    
    public final String getCurrentState( String workflowName ) {
        return getCurrentState( getWorkflowId( workflowName ) );
    }
    
    public final boolean hasWorkflow( int workflowId ) {
        return workflows.contains( workflowId );
    }

    public final WorkflowBuilder getWorkflowBuilder() {
        return new WorkflowBuilder();
    }

    @Override
    public final SystemComponentKey<?>[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
                new WorkflowBuilderAdapter( this ),
            };
    }

    private final class WorkflowBuilderAdapter extends SystemBuilderAdapter<Workflow> {
        public WorkflowBuilderAdapter( StateSystem system ) {
            super( system, new WorkflowBuilder() );
        }
        @Override
        public final SystemComponentKey<Workflow> componentTypeKey() {
            return Workflow.TYPE_KEY;
        }
        @Override
        public final Workflow getComponent( int id ) {
            return workflows.get( id );
        }
        @Override
        public final Iterator<Workflow> getAll() {
            return workflows.iterator();
        }
        @Override
        public final void deleteComponent( int id ) {
            deleteWorkflow( id );
        }
        @Override
        public final void deleteComponent( String name ) {
            deleteWorkflow( name );
        }
        @Override
        public final Workflow getComponent( String name ) {
            return getWorkflow( name );
        }
    }
    
    public final class WorkflowBuilder extends SystemComponentBuilder {
        
        protected WorkflowBuilder() {
            super( context );
        }
        
        @Override
        public final SystemComponentKey<Workflow> systemComponentKey() {
            return Workflow.TYPE_KEY;
        }
        
        @Override
        public int doBuild( int componentId, Class<?> subType, boolean activate ) {
            Workflow workflow = createSystemComponent( componentId, subType, context );
            workflows.set( workflow.index(), workflow );
            
            if ( activate ) {
                activateWorkflow( workflow.index() );
            }
            
            return workflow.index();
        }
    }

}
