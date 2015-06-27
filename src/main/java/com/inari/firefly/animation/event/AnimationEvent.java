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
package com.inari.firefly.animation.event;

import com.inari.commons.event.Event;

public final class AnimationEvent extends Event<AnimationEventListener> {
    
    public enum Type {
        START_ANIMATION,
        STOP_ANIMATION
    }
    
    public final int animationId;
    public final Type type;

    public AnimationEvent( Type type, int animationId ) {
        this.animationId = animationId;
        this.type = type;
    }

    @Override
    public final void notify( AnimationEventListener listener ) {
        listener.onAnimationEvent( this );
    }

}
