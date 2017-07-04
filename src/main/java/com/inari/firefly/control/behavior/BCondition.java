package com.inari.firefly.control.behavior;

import com.inari.firefly.control.behavior.EBehavoir.ActionState;
import com.inari.firefly.system.FFContext;

public interface BCondition {

    EBehavoir.ActionState check( int entityId, final EBehavoir behavior, final FFContext context );
    
}
