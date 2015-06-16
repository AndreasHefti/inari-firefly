package com.inari.firefly.entity.event;

import com.inari.commons.event.AspectedEventListener;

@Deprecated // check if this is really useful
public interface AspectedEntityChangeListener extends EntityChangeListener, AspectedEventListener {
    // Just to bring Aspects into EntityChangeListener
}
