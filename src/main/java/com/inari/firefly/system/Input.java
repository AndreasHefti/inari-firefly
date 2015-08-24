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
package com.inari.firefly.system;

import com.inari.commons.geom.Position;
import com.inari.commons.geom.Vector2f;

public abstract class Input {
    
    public enum InputType {
        UP,
        RIGHT,
        DOWN,
        LEFT,
        BUTTON_A,
        BUTTON_B,
        BUTTON_C,
        BUTTON_D
    }
    
    public boolean up = false;
    public boolean right = false;
    public boolean down = false;
    public boolean left = false;
    
    public boolean buttonA = false;
    public boolean buttonB = false;
    public boolean buttonC = false;
    public boolean buttonD = false;
    
    public final Vector2f acceleration = new Vector2f( 0f, 0f );
    public final Position pointerPosition = new Position( 0 ,0 );
    
    public abstract void update();
    
    public abstract void setKeyInput( InputType type, int keyCode );

}
