package com.inari.firefly;

import com.inari.commons.event.EventDispatcher;
import com.inari.commons.event.IEventDispatcher;
import com.inari.firefly.system.FireFly;

public class FireFlyMock extends FireFly {

    public FireFlyMock() {
        super( new EventDispatcher(), new FFGraphicsMock(), new FFAudioMock(), new TestTimer(), new InputMock() );
    }
    
    public FireFlyMock( EventDispatcherTestLog eventLog ) {
        super( new EventDispatcher( eventLog ), new FFGraphicsMock(), new FFAudioMock(), new TestTimer(), new InputMock() );
    }
    
    public FireFlyMock( IEventDispatcher eventDispatcher ) {
        super( eventDispatcher, new FFGraphicsMock(), new FFAudioMock(), new TestTimer(), new InputMock() );
    }

}
