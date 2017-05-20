package com.inari.firefly.system.utils;

import com.inari.firefly.system.FFContext;

/** Defines an interface for an object/component that can be loaded or loads/dispose some things
 *  
 *  Implement this in your Component if the component can be loaded/disposed or can load/dispose objects/components
 *  and needs the FFContext to be injected on load and dispose.
 */ 
public interface Loadable {
    
    /** Indicates whether the object/component is loaded or not
     * @return true if the object/component is loaded
     */
    boolean isLoaded();
    
    /** Implements the load of either this or some other Components or Objects.
     * 
     * @param context FFContext instance 
     * @return Disposable the dispose what was loaded within this method 
     */
    Disposable load( FFContext context );

}
