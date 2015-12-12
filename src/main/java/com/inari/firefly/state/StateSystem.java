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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.inari.commons.lang.indexed.Indexer;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.state.event.WorkflowEvent;
import com.inari.firefly.state.event.WorkflowEvent.Type;
import com.inari.firefly.state.event.WorkflowEventListener;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;
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
    
    private static final SystemComponentKey[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        Workflow.TYPE_KEY,
        State.TYPE_KEY,
        StateChange.TYPE_KEY
    };

    private final DynArray<Workflow> workflows;
    private final DynArray<State> states;
    private final DynArray<Collection<StateChange>> stateChangesForState;
    
    private int updateStep = 1;

    public StateSystem() {
        super( SYSTEM_KEY );
        workflows = new DynArray<Workflow>( Indexer.getIndexedObjectSize( Workflow.class ) );
        states = new DynArray<State>( Indexer.getIndexedObjectSize( State.class ) );
        stateChangesForState = new DynArray<Collection<StateChange>>( Indexer.getIndexedObjectSize( State.class ) );
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
        states.clear();
        stateChangesForState.clear();
    }
    
    public void activateWorkflow( int workflowId ) {
        final Workflow workflow = workflows.get( workflowId );
        if ( workflow == null || workflow.isActive() ) {
            return;
        }
        
        int startStateId = -1;
        String startStateName = workflow.getStartStateName();
        for ( State state : states ) {
            if ( startStateName.equals( state.getName() ) && workflow.index() == state.getWorkflowId() ) {
                startStateId = state.getId();
            }
        }
        
        if ( startStateId < 0 ) {
            throw new FFInitException( "Failed to find startState: " + startStateId + " for workflow: " + workflow.getName() );
        }
        
        int initTaskId = workflow.getInitTaskId();
        if ( initTaskId >= 0 ) {
            context.notify( new TaskEvent( TaskEvent.Type.RUN_TASK, initTaskId ) );
        }
        
        workflow.activate( startStateId );
        context.notify( new WorkflowEvent( workflow.getId(), -1, Type.WORKFLOW_STARTED ) );
    }
    
    @Override
    public void onEvent( WorkflowEvent event ) {
        switch ( event.type ) {
            case DO_STATE_CHANGE: {
                Workflow workflow = ( event.workflowName != null )? getWorkflow( event.workflowName ): getWorkflow( event.workflowId );
                StateChange stateChange = ( event.stateChangeName != null )? getStateChange( event.stateChangeName ): getStateChange( event.stateChangeId );
                if ( workflow != null && stateChange != null ) {
                    doStateChange( workflow, stateChange );
                }
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
            
            int currentStateId = workflow.getCurrentStateId();
            if ( !stateChangesForState.contains( currentStateId ) ) {
                return;
            }
            Collection<StateChange> stateChanges = stateChangesForState.get( currentStateId );
            
            for ( StateChange stateChange : stateChanges ) {
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
        int taskId = stateChange.getTaskId();
        if ( taskId >= 0 ) {
            context.notify( new TaskEvent( TaskEvent.Type.RUN_TASK, taskId ) );
        }
        
        int toStateId = stateChange.getToStateId();
        workflow.setCurrentStateId( toStateId );
        
        if ( toStateId >= 0 ) {
            context.notify( new WorkflowEvent( workflow.getId(), stateChange.getId(), Type.STATE_CHANGED ) );
        } else {
            context.notify( new WorkflowEvent( workflow.getId(), stateChange.getId(), Type.WORKFLOW_FINISHED ) );
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
        
        deleteAllStates( indexedId );
        workflow.dispose();
    }
    
    public final State getState( int stateId ) {
        return states.get( stateId );
    }
    
    public final int getStateId( String workflowName, String stateName ) {
        int workflowId = getWorkflowId( workflowName );
        if ( workflowId < 0 ) {
            return -1;
        }
        
        for ( int i = 0; i < states.capacity(); i++ ) {
            if ( !states.contains( i ) ) {
                continue;
            }
            
            State state = states.get( i );
            if ( state.getWorkflowId() == workflowId && stateName.equals( state.getName() ) ) {
                return i;
            }
        }
        
        return -1;
    }
    
    public final int getStateId( String stateName ) {
        for ( int i = 0; i < states.capacity(); i++ ) {
            if ( !states.contains( i ) ) {
                continue;
            }
            
            State state = states.get( i );
            if ( stateName.equals( state.getName() ) ) {
                return i;
            }
        }
        
        return -1;
    }
    
    public final State getCurrentState( int workflowId ) {
        int currentStateId = getCurrentStateId( workflowId );
        if ( currentStateId < 0 ) {
            return null;
        }
        
        return states.get( currentStateId );
    }
    
    public final int getCurrentStateId( int workflowId ) {
        Workflow workflow = workflows.get( workflowId );
        if ( workflow == null ) {
            return -1;
        }
        
        return workflow.getCurrentStateId();
    }

    public final void deleteAllStates( int workflowId ) {
        for ( State state : states ) {
            deleteState( state.index() );
        }
    }

    public final void deleteState( int indexedId ) {
        State state = states.remove( indexedId );
        if ( state == null ) {
            return;
        }
        
        if ( !stateChangesForState.contains( indexedId ) ) {
            return;
        }
        
        Collection<StateChange> stateChanges = stateChangesForState.get( indexedId );
        if ( stateChanges != null ) {
            for ( StateChange stateChange : stateChanges ) {
                stateChange.dispose();
            }
            stateChanges.clear();
        }
        state.dispose();
    }
    
    public final StateChange getStateChange( int workflowId, String stateChangeName ) {
        if ( stateChangeName == null ) {
            return null;
        }
        for ( Collection<StateChange> stateChanges : stateChangesForState ) {
            for ( StateChange stateChange : stateChanges ) {
                if ( stateChange.getWorkflowId() == workflowId && stateChangeName.equals( stateChange.getName() ) ) {
                    return stateChange;
                }
            }
        }
        return null;
    }
    
    public final StateChange getStateChange( String stateChangeName ) {
        if ( stateChangeName == null ) {
            return null;
        }
        for ( Collection<StateChange> stateChanges : stateChangesForState ) {
            for ( StateChange stateChange : stateChanges ) {
                if ( stateChangeName.equals( stateChange.getName() ) ) {
                    return stateChange;
                }
            }
        }
        return null;
    }
    
    public final StateChange getStateChange( int id ) {
        for ( Collection<StateChange> stateChanges : stateChangesForState ) {
            for ( StateChange stateChange : stateChanges ) {
                if ( stateChange.index() == id ) {
                    return stateChange;
                }
            }
        }
        return null;
    }
    
    public final void deleteStateChange( int id ) {
        for ( Collection<StateChange> stateChanges : stateChangesForState ) {
            Iterator<StateChange> it = stateChanges.iterator();
            while ( it.hasNext() ) {
                StateChange stateChange = it.next();
                if ( stateChange.index() == id ) {
                    it.remove();
                    stateChange.dispose();
                    return;
                }
            }
        }
    }
    
    public final boolean hasWorkflow( int workflowId ) {
        return workflows.contains( workflowId );
    }
    
    public final boolean hasState( int workflowId, int stateId ) {
        if ( !states.contains( stateId ) ) {
            return false;
        }
        
        State state = states.get( stateId );
        return workflowId == state.getWorkflowId();
    }

    
    public final ComponentBuilder getWorkflowBuilder() {
        return new WorkflowBuilder();
    }

    public final ComponentBuilder getStateBuilder() {
        return new StateBuilder();
    }

    public final ComponentBuilder getStateChangeBuilder() {
        return new StateChangeBuilder();
    }

    @Override
    public final SystemComponentKey[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
                new WorkflowBuilderAdapter( this ),
                new StateBuilderAdapter( this ),
                new StateChangeBuilderAdapter( this )
            };
    }

  

    private final class WorkflowBuilderAdapter extends SystemBuilderAdapter<Workflow> {
        public WorkflowBuilderAdapter( StateSystem system ) {
            super( system, new WorkflowBuilder() );
        }
        @Override
        public final SystemComponentKey componentTypeKey() {
            return Workflow.TYPE_KEY;
        }
        @Override
        public final Workflow get( int id, Class<? extends Workflow> subtype ) {
            return workflows.get( id );
        }
        @Override
        public final Iterator<Workflow> getAll() {
            return workflows.iterator();
        }
        @Override
        public final void delete( int id, Class<? extends Workflow> subtype ) {
            deleteWorkflow( id );
        }
    }
    
    private final class StateBuilderAdapter extends SystemBuilderAdapter<State> {
        public StateBuilderAdapter( StateSystem system ) {
            super( system, new StateBuilder() );
        }
        @Override
        public final SystemComponentKey componentTypeKey() {
            return State.TYPE_KEY;
        }
        @Override
        public final State get( int id, Class<? extends State> subtype ) {
            return states.get( id );
        }
        @Override
        public final Iterator<State> getAll() {
            return states.iterator();
        }
        @Override
        public final void delete( int id, Class<? extends State> subtype ) {
            deleteState( id );
        }
    }
    
    private final class StateChangeBuilderAdapter extends SystemBuilderAdapter<StateChange> {
        public StateChangeBuilderAdapter( StateSystem system ) {
            super( system, new StateChangeBuilder() );
        }
        @Override
        public final SystemComponentKey componentTypeKey() {
            return StateChange.TYPE_KEY;
        }
        @Override
        public final StateChange get( int id, Class<? extends StateChange> subtype ) {
            return getStateChange( id );
        }
        @Override
        public final Iterator<StateChange> getAll() {
            return new Iterator<StateChange>() {
                private Iterator<Collection<StateChange>> it1 = stateChangesForState.iterator();
                private Iterator<StateChange> it2 = null;
                @Override
                public boolean hasNext() {
                    return it1.hasNext() || it2.hasNext();
                }
                @Override
                public StateChange next() {
                    if ( it2 == null || !it2.hasNext() ) {
                        it2 = it1.next().iterator();
                    }
                    return it2.next();
                }
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        @Override
        public final void delete( int id, Class<? extends StateChange> subtype ) {
            deleteStateChange( id );
        }
    }
    
    private final class WorkflowBuilder extends SystemComponentBuilder {
        
        @Override
        public final SystemComponentKey systemComponentKey() {
            return Workflow.TYPE_KEY;
        }
        
        @Override
        public int doBuild( int componentId, Class<?> subType, boolean activate ) {
            Workflow workflow = new Workflow( componentId );
            workflow.fromAttributes( attributes );
            workflows.set( workflow.index(), workflow );
            
            return workflow.getId();
        }
    }
    
    private final class StateBuilder extends SystemComponentBuilder {
        
        @Override
        public final SystemComponentKey systemComponentKey() {
            return State.TYPE_KEY;
        }
        
        @Override
        public int doBuild( int componentId, Class<?> subType, boolean activate ) {
            State state = new State( componentId );
            state.fromAttributes( attributes );
            states.set( state.index(), state );
            return state.getId();
        }
    }

    private final class StateChangeBuilder extends SystemComponentBuilder {
        
        @Override
        public final SystemComponentKey systemComponentKey() {
            return StateChange.TYPE_KEY;
        }
        
        @Override
        public int doBuild( int componentId, Class<?> subType, boolean activate ) {
            StateChange stateChange = new StateChange( componentId );
            stateChange.fromAttributes( attributes );
            
            int workflowId = stateChange.getWorkflowId();
            int fromStateId = stateChange.getFromStateId();
            if ( !hasWorkflow( workflowId ) ) {
                throw new ComponentCreationException( "The Workflow with id: " + workflowId + " does not exists." );
            }
            
            if ( !hasState( workflowId, fromStateId ) ) {
                throw new ComponentCreationException( "The State with id: " + stateChange.getFromStateId() + " does not exists." );
            }
            
            int toStateId = stateChange.getToStateId();
            if ( toStateId >= 0 && !hasState( workflowId, toStateId ) ) {
                throw new ComponentCreationException( "The State with id: " + stateChange.getToStateId() + " does not exists." );
            }
            
            Collection<StateChange> stateChanges;
            if ( !stateChangesForState.contains( fromStateId ) ) {
                stateChanges = new ArrayList<StateChange>();
                stateChangesForState.set( fromStateId, stateChanges );
            } else {
                stateChanges = stateChangesForState.get( fromStateId );
            }
            stateChanges.add( stateChange );
            return stateChange.getId();
        }
    }

}
