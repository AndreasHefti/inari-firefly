package com.inari.firefly;

import org.junit.After;
import org.junit.Before;

import com.inari.firefly.system.FFContext;

public class FFTest {
    
    protected FireFlyMock firefly;
    protected FFContext ffContext;
    protected EventDispatcherTestLog eventLog;
    
    @Before
    public void init() {
        eventLog = new EventDispatcherTestLog();
        firefly = new FireFlyMock( eventLog );
        ffContext = firefly.getContext();
        eventLog.clearLog();
    }
    
    @After
    public void cleanup() {
        ffContext.dispose();
        ffContext = null;
    }

}
