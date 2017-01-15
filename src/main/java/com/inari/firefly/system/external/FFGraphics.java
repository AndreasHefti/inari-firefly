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

import java.util.Iterator;

import com.inari.commons.geom.Rectangle;
import com.inari.firefly.graphics.ShaderAsset;
import com.inari.firefly.graphics.SpriteRenderable;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.graphics.view.ViewEventListener;
import com.inari.firefly.system.utils.FFContextInitiable;

public interface FFGraphics extends FFContextInitiable, ViewEventListener {

    int createTexture( TextureData data );
    
    void disposeTexture( int textureId );
    
    int createSprite( SpriteData data );
    
    void disposeSprite( int spriteId );
    
    int createShader( ShaderAsset shaderAsset );
    
    void disposeShader( int shaderAssetId );
    
    int getScreenWidth();

    int getScreenHeight();
    
    void startRendering( View view, boolean clear );

    void renderSprite( SpriteRenderable renderableSprite, float xpos, float ypos );
    
    void renderSprite( SpriteRenderable renderableSprite, float xpos, float ypos, float scale );
    
    void renderSprite( SpriteRenderable renderableSprite, TransformData tranform );
    
    void renderShape( ShapeData data );
    
    void renderShape( ShapeData data, TransformData tranform );

    void endRendering( View view );
    
    void flush( Iterator<View> virtualViews );
    
    byte[] getScreenshotPixels( Rectangle area );

}
