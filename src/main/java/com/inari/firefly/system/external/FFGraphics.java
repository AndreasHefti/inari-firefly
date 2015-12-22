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

import com.inari.firefly.asset.event.AssetEventListener;
import com.inari.firefly.renderer.SpriteRenderable;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.view.View;
import com.inari.firefly.system.view.event.ViewEventListener;

public interface FFGraphics extends FFContextInitiable, AssetEventListener, ViewEventListener {
    
    int getScreenWidth();

    int getScreenHeight();
    
    void startRendering( View view );
    
    void renderSprite( SpriteRenderable renderableSprite, float xpos, float ypos );
    
    void renderSprite( SpriteRenderable spriteRenderable, float x, float y, float pivotx, float pivoty, float scalex, float scaley, float rotation );

    void endRendering( View view );
    
    void flush( Iterator<View> virtualViews );

}