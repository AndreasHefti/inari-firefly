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
package com.inari.firefly.audio;

import com.inari.firefly.control.Controller;

public abstract class SoundController extends Controller {

    protected AudioSystem soundSystem;
    
    protected SoundController( int id ) {
        super( id );
    }

    @Override
    public final void update() {
        for ( int i = 0; i < componentIds.length(); i++ ) {
            if ( componentIds.isEmpty( i ) ) {
                continue;
            }
            
            update( soundSystem.sounds.get( componentIds.get( i ) ) );
        }
    }


    public abstract void update( Sound sound );

}
