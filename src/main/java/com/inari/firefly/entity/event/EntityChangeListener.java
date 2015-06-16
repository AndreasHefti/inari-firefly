package com.inari.firefly.entity.event;

@Deprecated // check if this is really useful
public interface EntityChangeListener {
    
    public void onEntityChangeEvent( EntityChangeEvent event );

}
