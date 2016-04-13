package com.inari.firefly.system.info;

public interface SystemInfoDisplay {
    
    boolean isActive();

    SystemInfoDisplay setActive( boolean active );
    
    SystemInfoDisplay addSystemInfo( SystemInfo systemInfo );

}
