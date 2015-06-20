package com.inari.firefly.state;

import java.util.ArrayList;
import java.util.Collection;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFContext;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.state.event.StateChangeEvent;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.event.UpdateEvent;
import com.inari.firefly.system.event.UpdateEventListener;

public class StateSystem implements FFSystem, ComponentBuilderFactory, UpdateEventListener {
    
    private IEventDispatcher eventDispatcher;
    private FFContext context;
    
    private final DynArray<Workflow> workflows;
    private final DynArray<State> states;
    private final DynArray<Collection<StateChange>> stateChangesForState;
    private final DynArray<StateChangeCondition> conditions;
    
    private int updateStep = 10;

    public StateSystem(  ) {
        workflows = new DynArray<Workflow>( Indexer.getIndexedObjectSize( Workflow.class ) );
        states = new DynArray<State>( Indexer.getIndexedObjectSize( State.class ) );
        stateChangesForState = new DynArray<Collection<StateChange>>( Indexer.getIndexedObjectSize( State.class ) );
        conditions = new DynArray<StateChangeCondition>( Indexer.getIndexedObjectSize( StateChangeCondition.class ) );
    }
    
    @Override
    public void init( FFContext context ) {
        if ( this.context != null ) {
            return;
        }
        this.context = context;
        
        eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
        eventDispatcher.register( UpdateEvent.class, this );
    }

    public final int getUpdateStep() {
        return updateStep;
    }

    public final void setUpdateStep( int updateStep ) {
        this.updateStep = updateStep;
    }

    @Override
    public final void dispose( FFContext context ) {
        eventDispatcher.unregister( UpdateEvent.class, this );
        
        clear();
        context = null;
        eventDispatcher = null;
    }

    public final void clear() {
        for ( Workflow workflow : workflows ) {
            deleteWorkflow( workflow.indexedId() );
        }
        workflows.clear();
        states.clear();
        stateChangesForState.clear();
        conditions.clear();
    }
    
    @Override
    public final void update( UpdateEvent event ) {
        if ( event.getTimeElapsed() % updateStep != 0 ) {
            return;
        }
        
        for ( Workflow workflow : workflows ) {
            Collection<StateChange> stateChanges = stateChangesForState.get( workflow.getCurrentStateId() );
            if ( stateChanges == null || stateChanges.isEmpty() ) {
                continue;
            }
            
            for ( StateChange stateChange : stateChanges ) {
                int conditionId = stateChange.getConditionId();
                if ( conditionId < 0 ) {
                    continue;
                }
                
                StateChangeCondition condition = conditions.get( conditionId );
                if ( condition == null ) {
                    continue;
                }
                
                if ( condition.check( workflow, event.getTimeElapsed() ) ) {
                    workflow.setCurrentStateId( stateChange.getToStateId() );
                    eventDispatcher.notify( new StateChangeEvent( stateChange ) );
                }
            }
        }
    }
    
    public final boolean deleteWorkflow( String name ) {
        Workflow workflow = getWorkflow( name );
        if ( workflow == null ) {
            return false;
        }
        
        deleteWorkflow( workflow.indexedId() );
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
                return workflow.indexedId();
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
            deleteState( state.indexedId() );
        }
    }

    public final void deleteState( int indexedId ) {
        State state = states.remove( indexedId );
        if ( state == null ) {
            return;
        }
        
        Collection<StateChange> stateChanges = stateChangesForState.get( indexedId );
        if ( stateChanges != null ) {
            stateChanges.clear();
        }
    }
    
    public final boolean hasWorkflow( int workflowId ) {
        return workflows.contains( workflowId );
    }
    
    public final boolean hasState( int stateId ) {
        return states.contains( stateId );
    }
    
    public final boolean hasCondition( int conditionId ) {
        return conditions.contains( conditionId );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final <C> ComponentBuilder<C> getComponentBuilder( Class<C> type ) {
        if ( type == Workflow.class ) {
            return (ComponentBuilder<C>) getWorkflowBuilder();
        }
        
        if ( type == State.class ) {
            return (ComponentBuilder<C>) getStateBuilder();
        }
        
        if ( type == StateChangeCondition.class ) {
            return (ComponentBuilder<C>) getStateChangeConditionBuilder();
        }
        
        if ( type == StateChange.class ) {
            return (ComponentBuilder<C>) getStateChangeBuilder();
        }
        
        throw new IllegalArgumentException( "Unsupported IComponent type for StateSystem. Type: " + type );
    }
    
    public final ComponentBuilder<Workflow> getWorkflowBuilder() {
        return new BaseComponentBuilder<Workflow>( this ) {
            @Override
            public Workflow build( int componentId ) {
                Workflow workflow = new Workflow( componentId );
                workflow.fromAttributeMap( attributes );
                workflows.set( workflow.indexedId(), workflow );
                return workflow;
            }
        };
    }
    
    public final ComponentBuilder<State> getStateBuilder() {
        return new BaseComponentBuilder<State>( this ) {
            @Override
            public State build( int componentId ) {
                State state = new State( componentId );
                state.fromAttributeMap( attributes );
                states.set( state.indexedId(), state );
                return state;
            }
        };
    }
    
    public final StateChangeConditionBuilder getStateChangeConditionBuilder() {
        return new StateChangeConditionBuilder( this );
    }
    
    public final ComponentBuilder<StateChange> getStateChangeBuilder() {
        return new BaseComponentBuilder<StateChange>( this ) {
            @Override
            public StateChange build( int componentId ) {
                StateChange stateChange = new StateChange( componentId );
                stateChange.fromAttributeMap( attributes );
                
                int workflowId = stateChange.getWorkflowId();
                int fromStateId = stateChange.getFromStateId();
                if ( !hasWorkflow( workflowId ) ) {
                    throw new ComponentCreationException( "The Workflow with id: " + workflowId + " does not exists." );
                }
                
                if ( !hasState( fromStateId ) ) {
                    throw new ComponentCreationException( "The State with id: " + stateChange.getFromStateId() + " does not exists." );
                }
                
                if ( !hasState( stateChange.getToStateId() ) ) {
                    throw new ComponentCreationException( "The State with id: " + stateChange.getToStateId() + " does not exists." );
                }
                
                int conditionId = stateChange.getConditionId();
                if( conditionId >= 0 && !hasCondition( conditionId ) ) {
                    throw new ComponentCreationException( "The StateChangeCondition with id: " + conditionId + " does not exists." );
                }
                
                Collection<StateChange> stateChanges = stateChangesForState.get( fromStateId );
                if ( stateChanges == null ) {
                    stateChanges = new ArrayList<StateChange>();
                    stateChangesForState.set( fromStateId, stateChanges );
                }
                stateChanges.add( stateChange );
                
                return stateChange;
            }
        };
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "StateSystem [updateStep=" );
        builder.append( updateStep );
        builder.append( ", workflows=" );
        builder.append( workflows );
        builder.append( ", states=" );
        builder.append( states );
        builder.append( ", stateChangesForState=" );
        builder.append( stateChangesForState );
        builder.append( ", conditions=" );
        builder.append( conditions );
        builder.append( "]" );
        return builder.toString();
    }

    private final class StateChangeConditionBuilder extends BaseComponentBuilder<StateChangeCondition> {
        
        private StateChangeConditionBuilder( StateSystem system ) {
            super( system );
        }

        @Override
        public StateChangeCondition build( int componentId ) {
            StateChangeCondition result = getInstance( context, componentId );
            result.fromAttributeMap( attributes );
            return result;
        }
    }

}
