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

import java.util.Iterator;

import com.inari.commons.lang.indexed.Indexer;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.state.event.WorkflowEvent;
import com.inari.firefly.state.event.WorkflowEventListener;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;
import com.inari.firefly.task.event.TaskEvent;

public class StateSystem
    extends
        ComponentSystem<StateSystem>
    implements 
        UpdateEventListener,
        WorkflowEventListener {
    
    public static final FFSystemTypeKey<StateSystem> SYSTEM_KEY = FFSystemTypeKey.create( StateSystem.class );
    
    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        Workflow.TYPE_KEY,
    };

    private final DynArray<Workflow> workflows;
    
    private int updateStep = 1;

    public StateSystem() {
        super( SYSTEM_KEY );
        workflows = new DynArray<Workflow>( Indexer.getIndexedObjectSize( Workflow.class ) );
    }
    
    @Override
    public void init( FFContext context ) {
        super.init( context );
        
        context.registerListener( UpdateEvent.class, this );
        context.registerListener( WorkflowEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( UpdateEvent.class, this );
        context.disposeListener( WorkflowEvent.class, this );
        
        clear();
    }

    public final int getUpdateStep() {
        return updateStep;
    }

    public final void setUpdateStep( int updateStep ) {
        this.updateStep = updateStep;
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

        String initTaskName = workflow.getInitTaskName();
        if ( initTaskName != null ) {
            context.notify( new TaskEvent( TaskEvent.Type.RUN_TASK, initTaskName ) );
        }
        
        workflow.activate();
        context.notify( WorkflowEvent.createWorkflowStartedEvent( workflow.getId(), workflow.getName(), initTaskName ) );
    }
    
    @Override
    public void onEvent( WorkflowEvent event ) {
        switch ( event.type ) {
            case DO_STATE_CHANGE: {
                Workflow workflow = ( event.workflowName != null )? getWorkflow( event.workflowName ) : getWorkflow( event.workflowId );
                if ( workflow == null ) {
                    throw new IllegalArgumentException( "No Workflow found for state change event: " + event );
                }
                
                StateChange stateChange = null;
                if ( event.stateChangeName != null ) {
                    stateChange = workflow.getStateChangeForCurrentState( event.stateChangeName );
                } else if ( event.targetStateName != null ){
                    stateChange = workflow.getStateChangeForTargetState( event.targetStateName );
                } 
                
                if ( stateChange == null ) {
                    throw new IllegalArgumentException( "No StateChange found for state change event: " + event + " on workflow" + workflow );
                }
                doStateChange( workflow, stateChange );
                break;
            }
            default: {}
        }
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
                StateChangeCondition condition = stateChange.getCondition();
                if ( condition == null ) {
                    continue;
                }
                
                if ( condition.check( context, workflow, event.timer ) ) {
                    doStateChange( workflow, stateChange );
                    break;
                }
            }
        }
    }

    private void doStateChange( Workflow workflow, StateChange stateChange ) {
        String taskName = stateChange.getTaskName();
        if ( taskName != null ) {
            context.notify( new TaskEvent( TaskEvent.Type.RUN_TASK, taskName ) );
        }
        
        String toStateName = stateChange.getToStateName();
        workflow.setCurrentState( toStateName );
        
        if ( toStateName != null ) {
            context.notify( WorkflowEvent.createStateChangedEvent( workflow.getId(), workflow.getName(), stateChange ) );
        } else {
            context.notify( WorkflowEvent.createWorkflowFinishedEvent( workflow.getId(), workflow.getName(), stateChange ) );
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

    
    public final ComponentBuilder getWorkflowBuilder() {
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
    
    private final class WorkflowBuilder extends SystemComponentBuilder {
        
        @Override
        public final SystemComponentKey<Workflow> systemComponentKey() {
            return Workflow.TYPE_KEY;
        }
        
        @Override
        public int doBuild( int componentId, Class<?> subType, boolean activate ) {
            Workflow workflow = new Workflow( componentId );
            workflow.fromAttributes( attributes );
            workflows.set( workflow.index(), workflow );
            
            if ( activate ) {
                activateWorkflow( workflow.getId() );
            }
            
            return workflow.getId();
        }
    }

}
