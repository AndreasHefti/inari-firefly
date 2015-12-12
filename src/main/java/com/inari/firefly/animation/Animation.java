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
package com.inari.firefly.animation;

import java.util.Arrays;
import java.util.Set;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFTimer;
import com.inari.firefly.system.component.SystemComponent;

public abstract class Animation extends SystemComponent {
    
    public static final SystemComponentKey TYPE_KEY = SystemComponentKey.create( Animation.class );
    
    public static final AttributeKey<Long> START_TIME = new AttributeKey<Long>( "startTime", Long.class, Animation.class );
    public static final AttributeKey<Boolean> LOOPING = new AttributeKey<Boolean>( "looping", Boolean.class, Animation.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        START_TIME,
        LOOPING,
    };
    
    protected long startTime;
    protected boolean looping;
    protected boolean active;
    protected boolean finished;

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

    @Override
    public final Class<Animation> componentType() {
        return Animation.class;
    }

    @Override
    public final Class<Animation> indexedObjectType() {
        return Animation.class;
    }

    public final long getStartTime() {
        return startTime;
    }

    public final void setStartTime( long startTime ) {
        this.startTime = startTime;
    }

    public final boolean isLooping() {
        return looping;
    }

    public final void setLooping( boolean looping ) {
        this.looping = looping;
    }

    public final boolean isActive() {
        return active;
    }
    
    public final boolean isFinished() {
        return finished;
    }
    
    protected void setActive() {
        active = true;
    }
    
    public void update( final FFTimer timer ) {
        if ( !active && timer.getTime() >= startTime ) {
            setActive();
        }
    }

    @Override
    public Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( Arrays.asList( ATTRIBUTE_KEYS ) );
        return attributeKeys;
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
