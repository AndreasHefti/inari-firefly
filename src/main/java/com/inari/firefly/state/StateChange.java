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

import com.inari.commons.config.StringConfigurable;

public final class StateChange implements StringConfigurable {

    String name;
    String fromStateName;
    String toStateName;
    StateChangeCondition condition;
    
    public StateChange( String name, String fromStateName, String toStateName ) {
        this( name, fromStateName, toStateName, null );
    }

    public StateChange( String name, String fromStateName, String toStateName, StateChangeCondition condition ) {
        this.name = name;
        this.fromStateName = fromStateName;
        this.toStateName = toStateName;
        this.condition = condition;
    }
    
    public final String getName() {
        return name;
    }

    public final String getFromStateName() {
        return fromStateName;
    }

    public final String getToStateName() {
        return toStateName;
    }

    public final StateChangeCondition getCondition() {
        return condition;
    }

    @Override
    public final void fromConfigString( String stringValue ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public final String toConfigString() {
        // TODO Auto-generated method stub
        return null;
    }

}
