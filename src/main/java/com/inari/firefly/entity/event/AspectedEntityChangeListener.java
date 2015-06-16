package com.inari.firefly.entity.event;

import com.inari.commons.event.IAspectedEventListener;

@Deprecated // check if this is really useful
public interface AspectedEntityChangeListener extends EntityChangeListener, IAspectedEventListener {
    // Just to bring Aspects into EntityChangeListener
}
