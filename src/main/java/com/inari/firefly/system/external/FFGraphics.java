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
import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.graphics.BlendMode;
import com.inari.firefly.graphics.ShaderAsset;
import com.inari.firefly.graphics.SpriteRenderable;
import com.inari.firefly.graphics.TextureAsset;
import com.inari.firefly.graphics.shape.EShape;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.view.View;
import com.inari.firefly.system.view.ViewEventListener;

public interface FFGraphics extends FFContextInitiable, ViewEventListener {
    
    int createTexture( String resourceName );
    
    int createTexture( TextureAsset textureAsset );
    
    void disposeTexture( int textureId );
    
    int createSprite( int textureId, Rectangle textureRegion );
    
    int createSprite( SpriteAsset spriteAsset );
    
    void disposeSprite( int spriteId );
    
    int createShader( ShaderAsset shaderAsset );
    
    void disposeShader( int shaderAssetId );
    
    int getScreenWidth();

    int getScreenHeight();
    
    void startRendering( View view );

    void renderSprite( SpriteRenderable renderableSprite, float xpos, float ypos );
    
    void renderSprite( SpriteRenderable renderableSprite, TransformData tranform );
    
    void renderShape( EShape.Type type, float[] vertices, int segments, DynArray<RGBColor> colors, BlendMode blendMode, boolean fill, int shaderId );
    
    void renderShape( EShape.Type type, float[] vertices, int segments, DynArray<RGBColor> colors, BlendMode blendMode, boolean fill, int shaderId, TransformData tranform );

    void endRendering( View view );
    
    void flush( Iterator<View> virtualViews );

}
