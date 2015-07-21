package com.inari.firefly;

import com.inari.firefly.system.FFContext;

public interface Disposable {
    
    /** Dispose the object/system.
     *  - remove all listeners
     *  - clear all internal data
     *  
     * @param context the IFFContext instance
     */
    void dispose( FFContext context );

}
