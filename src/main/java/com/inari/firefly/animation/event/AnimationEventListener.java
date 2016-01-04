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
package com.inari.firefly.animation.event;

/** Implement this to receive AnimationEvent's when registerd to the EventDispatcher.
 *  For default the AnimationSystem is implementing this to operate the AnimationEvent's
 *  But there may be other use cases to listen to AnimationEvents to trigger some additional code
 */
public interface AnimationEventListener {
    
    /** This is called by the EventDispatcher on a AnimationEvent of any kind
     *  @param event the AnimationEvent instance
     */
    void onAnimationEvent( AnimationEvent event );
}
