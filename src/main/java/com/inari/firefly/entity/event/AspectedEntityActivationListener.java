package com.inari.firefly.entity.event;

import com.inari.commons.event.IAspectedEventListener;

public interface AspectedEntityActivationListener extends EntityActivationListener, IAspectedEventListener {
    // Just to bring the Aspect into the EntityActivationListener
}
