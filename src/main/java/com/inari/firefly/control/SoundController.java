package com.inari.firefly.control;

import com.inari.firefly.component.NamedIndexedComponent;

public abstract class SoundController extends NamedIndexedComponent {

    SoundController( int id ) {
        super( id );
    }
    
    @Override
    public final Class<SoundController> getComponentType() {
        return SoundController.class;
    }

    @Override
    public final Class<SoundController> getIndexedObjectType() {
        return SoundController.class;
    }
    
    public abstract void update( long time, int soundId );

}
