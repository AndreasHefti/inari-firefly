package com.inari.firefly.entity.event;

import com.inari.commons.event.AspectedEventListener;

public interface AspectedEntityActivationListener extends EntityActivationListener, AspectedEventListener {
    // Just to bring the Aspect into the EntityActivationListener
}
