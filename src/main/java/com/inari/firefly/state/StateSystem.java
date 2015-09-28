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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.ComponentBuilderHelper;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;
import com.inari.firefly.task.event.TaskEvent;

public class StateSystem implements FFContextInitiable, ComponentSystem, ComponentBuilderFactory, UpdateEventListener {
    
    public static final TypedKey<StateSystem> CONTEXT_KEY = TypedKey.create( "FF_STATE_SYSTEM", StateSystem.class );
    
    private IEventDispatcher eventDispatcher;
    private FFContext context;
    
    private final DynArray<Workflow> workflows;
    private final DynArray<State> states;
    private final DynArray<Collection<StateChange>> stateChangesForState;
    
    private int updateStep = 1;

    public StateSystem() {
        workflows = new DynArray<Workflow>( Indexer.getIndexedObjectSize( Workflow.class ) );
        states = new DynArray<State>( Indexer.getIndexedObjectSize( State.class ) );
        stateChangesForState = new DynArray<Collection<StateChange>>( Indexer.getIndexedObjectSize( State.class ) );
    }
    
    @Override
    public void init( FFContext context ) {
        if ( this.context != null ) {
            return;
        }
        this.context = context;
        
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        eventDispatcher.register( UpdateEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        eventDispatcher.unregister( UpdateEvent.class, this );
        
        clear();
        context = null;
        eventDispatcher = null;
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
        
        int initTaskId = workflow.getInitTaskId();
        if ( initTaskId >= 0 ) {
            eventDispatcher.notify( new TaskEvent( TaskEvent.Type.RUN_TASK, initTaskId ) );
        }
        
        workflow.activate();
    }
    
    @Override
    public final void update( UpdateEvent event ) {
        long update = event.timer.getUpdate();
        if ( update % updateStep != 0 ) {
            return;
        }
        
        for ( Workflow workflow : workflows ) {
            if ( !workflow.isActive() ) {
                continue;
            }
            
            int currentStateId = workflow.getCurrentStateId();
            Collection<StateChange> stateChanges = stateChangesForState.get( currentStateId );
            
            for ( StateChange stateChange : stateChanges ) {
                StateChangeCondition condition = stateChange.getCondition();
                if ( condition == null ) {
                    continue;
                }
                
                if ( condition.check( context, workflow, event.timer ) ) {
                    int taskId = stateChange.getTaskId();
                    if ( taskId >= 0 ) {
                        eventDispatcher.notify( new TaskEvent( TaskEvent.Type.RUN_TASK, taskId ) );
                    }
                    
                    workflow.setCurrentStateId( stateChange.getToStateId() );
                    break;
                }
            }
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

    public final Workflow getWorkflow( String name ) {
        for ( Workflow workflow : workflows ) {
            if ( workflow.getName().equals( name ) ) {
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

    @Override
    @SuppressWarnings( { "unchecked" } )
    public final <C> ComponentBuilder<C> getComponentBuilder( Class<C> type ) {
        if ( type == Workflow.class ) {
            return (ComponentBuilder<C>) getWorkflowBuilder();
        }
        
        if ( type == State.class ) {
            return (ComponentBuilder<C>) getStateBuilder();
        }
        
        if ( type == StateChange.class ) {
            return (ComponentBuilder<C>) getStateChangeBuilder();
        }
        
        throw new IllegalArgumentException( "Unsupported IComponent type for StateSystem. Type: " + type );
    }
    
    public final ComponentBuilder<Workflow> getWorkflowBuilder() {
        return new BaseComponentBuilder<Workflow>( this ) {
            @Override
            protected Workflow createInstance( Constructor<Workflow> constructor, Object... paramValues ) throws Exception {
                return constructor.newInstance( paramValues );
            }
            @Override
            public Workflow build( int componentId ) {
                Workflow workflow = new Workflow( componentId );
                workflow.fromAttributes( attributes );
                workflows.set( workflow.index(), workflow );
                return workflow;
            }
        };
    }
    
    public final ComponentBuilder<State> getStateBuilder() {
        return new BaseComponentBuilder<State>( this ) {
            @Override
            protected State createInstance( Constructor<State> constructor, Object... paramValues ) throws Exception {
                return constructor.newInstance( paramValues );
            }
            @Override
            public State build( int componentId ) {
                State state = new State( componentId );
                state.fromAttributes( attributes );
                states.set( state.index(), state );
                return state;
            }
        };
    }

    public final ComponentBuilder<StateChange> getStateChangeBuilder() {
        return new StateChangeBuilder( this );
    }

    private static final Set<Class<?>> SUPPORTED_COMPONENT_TYPES = new HashSet<Class<?>>();
    @Override
    public final Set<Class<?>> supportedComponentTypes() {
        if ( SUPPORTED_COMPONENT_TYPES.isEmpty() ) {
            SUPPORTED_COMPONENT_TYPES.add( State.class );
            SUPPORTED_COMPONENT_TYPES.add( StateChange.class );
            SUPPORTED_COMPONENT_TYPES.add( Workflow.class );
        }
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final void fromAttributes( Attributes attributes ) {
        fromAttributes( attributes, BuildType.CLEAR_OLD );
    }

    @Override
    public final void fromAttributes( Attributes attributes, BuildType buildType ) {
        if ( buildType == BuildType.CLEAR_OLD ) {
            clear();
        }
        
        new ComponentBuilderHelper<Workflow>() {
            @Override
            public Workflow get( int id ) {
                return workflows.get( id );
            }
            @Override
            public void delete( int id ) {
                deleteWorkflow( id );
            }
        }.buildComponents( Workflow.class, buildType, getWorkflowBuilder(), attributes );
        
        new ComponentBuilderHelper<State>() {
            @Override
            public State get( int id ) {
                return states.get( id );
            }
            @Override
            public void delete( int id ) {
                deleteState( id );
            }
        }.buildComponents( State.class, buildType, getStateBuilder(), attributes );
        
        new ComponentBuilderHelper<StateChange>() {
            @Override
            public StateChange get( int id ) {
                return getStateChange( id );
            }
            @Override
            public void delete( int id ) {
                deleteStateChange( id );
            }
        }.buildComponents( StateChange.class, buildType, getStateChangeBuilder(), attributes );
    }

    

    @Override
    public final void toAttributes( Attributes attributes ) {
        ComponentBuilderHelper.toAttributes( attributes, Workflow.class, workflows );
        ComponentBuilderHelper.toAttributes( attributes, State.class, states );
        for ( Collection<StateChange> stateChanges : stateChangesForState ) {
            ComponentBuilderHelper.toAttributes( attributes, StateChange.class, stateChanges );
        }
    }

    private final class StateChangeBuilder extends BaseComponentBuilder<StateChange> {
        
        protected StateChangeBuilder( StateSystem system ) {
            super( system );
        }
        
        @Override
        protected StateChange createInstance( Constructor<StateChange> constructor, Object... paramValues ) throws Exception {
            return constructor.newInstance( paramValues );
        }

        @Override
        public StateChange build( int componentId ) {
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
            
            return stateChange;
        }
    }


}
