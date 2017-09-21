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

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.component.Activatable;
import com.inari.firefly.system.component.SystemComponent;

/** General and abstract implementation of the Animation SystemComponent.
 * 
 *  An animation means always and only an manipulation of a single value during time.
 * 
 *  This component defines in general an animation with a start time and a looping flag 
 *  and implements a general update mechanism for all kind of Animations to update the state of the 
 *  animation. This update is called by the AnimationSystem that listens to the global update events
 *  of the Firefly engine. 
 * 
 *  This is implemented by concrete value type implementation such as IntAnimation, FloatAnimation or a 
 *  general generic ValueAnimation.
 */
public abstract class Animation extends SystemComponent implements Activatable {
    
    /** The SystemComponent type key for Animation components */
    public static final SystemComponentKey<Animation> TYPE_KEY = SystemComponentKey.create( Animation.class );
    
    /** The AttributeKey for startTime attribute that defines a time where an Animation 
     *  is automatically started when living within the AnimationSystem.
     * 
     *  Use this if there is a concrete time where to start an Animation without use of AnimationEvent.
     */
    public static final AttributeKey<Long> START_TIME = AttributeKey.createLong( "startTime", Animation.class );
    /** The AttributeKey for the looping attributes that indicates whether the Animation is looping or not */
    public static final AttributeKey<Boolean> LOOPING = AttributeKey.createBoolean( "looping", Animation.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        START_TIME,
        LOOPING
    );
    
    protected long startTime;
    protected boolean looping;
    
    boolean active;
    boolean finished;
    
    protected long startedTime;
    protected long runningTime;

    Animation( int id ) {
        super( id );
        startTime = -1;
        looping = false;
        active = false;
        finished = false;
    }

    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

    /** Use this to get the startTime configured for this Animation */
    public final long getStartTime() {
        return startTime;
    }

    /** Use this to set the start time for this Animation */
    public final void setStartTime( long startTime ) {
        this.startTime = startTime;
    }

    /** Use this to indicate whether this Animation is looping or not */
    public final boolean isLooping() {
        return looping;
    }

    /** Use this to set the looping indication for this Animation */
    public final void setLooping( boolean looping ) {
        this.looping = looping;
    }

    /** Use this to indicates wether this Animation is active or not */
    public final boolean isActive() {
        return active;
    }
    
    public final void setActive( boolean active ) {
        if ( active ) {
            activate();
        } else {
            reset();
        }
    }
    
    public final boolean isFinished() {
        return finished;
    }
    
    public final void finish() {
        reset();
        finished = true;
    }

    public void activate() {
        if ( active ) {
            return;
        }
        
        startedTime = context.getTime();
        active = true;
    }
    
    public final void stop() {
        active = false;
    } 

    final void systemUpdate() {
        runningTime += context.getTimeElapsed();
        update();
    }
    
    /** This is called on every update by the AnimationSystem with the current FFTimer.
     *  
     *  This implementation deals with the start time of this Animation. If this Animation is not active
     *  and the current time is after the start time the Animation is atomatically activated by this update.
     * 
     *  Override this do to other update stuff if needed for a concrete implementation of Animation.
     */
    public abstract void update();
    
    public void reset() {
        active = false;
        finished = false;
        startedTime = -1;
        runningTime = 0;
    }

    @Override
    public Set<AttributeKey<?>> attributeKeys() {
        return JavaUtils.unmodifiableSet( super.attributeKeys(), ATTRIBUTE_KEYS );
    }

    @Override
    public void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        startTime = attributes.getValue( START_TIME, startTime );
        looping = attributes.getValue( LOOPING, looping );
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        attributes.put( START_TIME, startTime );
        attributes.put( LOOPING, looping );
    }

}
