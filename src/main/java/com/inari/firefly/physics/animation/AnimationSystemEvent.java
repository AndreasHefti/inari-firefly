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
package com.inari.firefly.physics.animation;

import com.inari.commons.event.Event;

/** Event to operate Animation's
 *  Use this to operate Animation's of all types. Animations can be started, stopped and finished. 
 */
public final class AnimationSystemEvent extends Event<AnimationSystem> {
    
    /** The type key for AnimationEvent event type */
    public static final EventTypeKey TYPE_KEY = createTypeKey( AnimationSystemEvent.class );
    
    /** The available and supported types of AnimationEvent */
    public enum Type {
        /** Starts an Animation ( if not already started ) */
        START_ANIMATION,
        /** Stops a running Animation (keeping the current state) */
        STOP_ANIMATION,
        /** Resumes a previously stopped Animation (continuing with the retained state) */
        RESUME_ANIMATION,
        /** Finishes a running or pausing animation. The finished Animation will be disposed on next update call */
        FINISH_ANIMATION
    }
    
    /** The id of the Animation to do the action event */
    public final int animationId;
    /** The type of AnimationEvent */ 
    public final Type type;

    /** Create a new AnimationEvent with specifed type and animationId.
     *  @param type the type of AnimationEvent
     *  @param animationId the id of Animation instance to operate on
     */
    public AnimationSystemEvent( Type type, int animationId ) {
        super( TYPE_KEY );
        this.animationId = animationId;
        this.type = type;
    }

    @Override
    protected final void notify( AnimationSystem listener ) {
        listener.onAnimationEvent( this );
    }

}
