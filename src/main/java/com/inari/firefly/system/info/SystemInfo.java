package com.inari.firefly.system.info;

import com.inari.firefly.system.FFContext;

public interface SystemInfo {
    
    String name();
    
    int getLength();
    
    void update( FFContext context, StringBuffer buffer, int bufferStartPointer );
    

}
