/*******************************************************************************
 * Copyright (c) 2015 - 2016, Andreas Hefti, inarisoft@yahoo.de 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/ 
package com.inari.firefly.system.external;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.graphics.ShaderAsset;
import com.inari.firefly.graphics.SpriteRenderable;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.graphics.view.ViewEventListener;
import com.inari.firefly.system.utils.FFContextInitiable;

/** This defines the low level API interface for all graphical functions used by the firefly API. <p>
 * 
 *  The {@link FFContextInitiable} implementation has gets the {@link FFContext} on initialization phase.<p>
 * 
 *  The {@link ViewEventListener} implementation has to deal with {@link View}(s) on lower level API. <p>
 *  There are two kind of Views; The BaseView that always is the Window-View. No matter if there are more Views available, the BaseView is always existing.
 *  And any number of VirtualViews. A VirtualView is a rectangular region within the BaseView that defines its own 2D space (camera/view).
 *  Usually a VirtualView can be implemented within Viewports or FBO's within rendering to textures on GPU level.
 *  For more Information about Views see {@link ViewSystem}
 *   
 */
public interface FFGraphics extends FFContextInitiable, ViewEventListener {

    /** This is called from the firefly API when a texture is created/loaded and should be loaded into the GPU
     * 
     * @param data The texture DAO
     * @return the texture identifier to identify the texture on lower level API.
     */
    int createTexture( TextureData data );
    
    /** This is called from the firefly API when a texture is disposed and should be deleted from GPU.
     *  and must release and delete the texture on GPU level
     *  
     * @param the identifier of the texture to dispose.
     */
    void disposeTexture( int textureId );
    
    /** This is called from the firefly API when a sprite is created/loaded and gives an identifier for that sprite.
     * 
     * @param data the sprite DAO
     * @return the sprite identifier to identify the sprite on lower level API.
     */
    int createSprite( SpriteData data );
    
    /** This is called from the firefly API when a sprite is disposed
     *  and must release and delete the sprite on lower level 
     *  
     * @param the sprite identifier of the texture to dispose.
     */
    void disposeSprite( int spriteId );
    
    /** This is called from the firefly API when a shader script is created/loaded and gives an identifier for that shader script.
     * 
     * @param shaderAsset the shader DAO
     * @return the shader identifier to identify the shader on lower level API.
     */
    int createShader( ShaderAsset shaderAsset );
    
    /** This is called from the firefly API when a shader script is disposed 
     *  and must release and delete the shader script on GPU level
     * 
     * @param the identifier of the shader to dispose.
     */
    void disposeShader( int shaderAssetId );
    
    /** Use this to get the actual screen width
     * @return the actual screen width
     */
    int getScreenWidth();

    /** Use this to get the actual screen height
     * @return the actual screen height
     */
    int getScreenHeight();
    
    /** This is called form the firefly API before rendering to a given {@link View} and must 
     *  prepare all the stuff needed to render the that {@link View} on following renderXXX calls.
     *  
     * @param view the {@link View} that is starting to be rendered
     * @param clear indicates whether the {@link View} should be cleared with the vies clear-color before rendering or not
     */
    void startRendering( View view, boolean clear );

    /** This is called form the firefly API to render a created sprite on specified position to the actual {@link View}
     * 
     * @param renderableSprite the sprite DAO
     * @param xpos the x-axis position in the 2D world of the actual {@link View}
     * @param ypos the y-axis position in the 2D world of the actual {@link View}
     */
    void renderSprite( SpriteRenderable renderableSprite, float xpos, float ypos );
    
    /** This is called form the firefly API to render a created sprite on specified position and scale to the actual {@link View}
     * 
     * @param renderableSprite the sprite DAO
     * @param xpos the x-axis position in the 2D world of the actual {@link View}
     * @param ypos the y-axis position in the 2D world of the actual {@link View}
     * @param scale the x-axis and y-axis scale for the sprite to render
     */
    void renderSprite( SpriteRenderable renderableSprite, float xpos, float ypos, float scale );
    
    /** This is called form the firefly API to render a created sprite with specified {@link TransformData} to the actual {@link View}
     * 
     * @param renderableSprite the sprite DAO
     * @param tranform {@link TransformData} DAO containing all transform data to render the sprite like: position-offset, scale, pivot, rotation
     */
    void renderSprite( SpriteRenderable renderableSprite, TransformData transform );
    
    /** This is called form the firefly API to render a shape. See {@link ShapeData} for more information about the data structure of shapes.
     * 
     * @param data {@link ShapeData} DAO
     */
    void renderShape( ShapeData data );
    
    /** This is called form the firefly API to render a shape with given {@link TransformData}. 
     *  See {@link ShapeData} for more information about the data structure of shapes.
     * 
     * @param data {@link ShapeData} DAO
     * @param transform {@link TransformData} DAO
     */
    void renderShape( ShapeData data, TransformData transform );

    /** This is called form the firefly API to notify the end of rendering for a specified {@link View}.
     * @param view {@link View} that is ending to be rendered
     */
    void endRendering( View view );
    
    void flush( DynArray<View> virtualViews );
    
    byte[] getScreenshotPixels( Rectangle area );

}
