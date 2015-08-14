/*******************************************************************************
 * Copyright (c) 2015, Andreas Hefti, inarisoft@yahoo.de 
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
package com.inari.firefly.system;

import java.util.Iterator;

import com.inari.firefly.asset.event.AssetEventListener;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.renderer.sprite.SpriteRenderable;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.event.SoundEventListener;
import com.inari.firefly.system.view.View;
import com.inari.firefly.system.view.event.ViewEventListener;

public interface ILowerSystemFacade extends FFComponent, AssetEventListener, ViewEventListener, SoundEventListener {
    
    void startRendering( View view );
    
    void renderSprite( SpriteRenderable renderableSprite, ETransform transform );
    
    void renderSprite( SpriteRenderable renderableSprite, float xpos, float ypos );
    
    void endRendering( View view );
    
    void flush( Iterator<View> virtualViews );
    
    void soundAttributesChanged( Sound sound );
    
    Input getInput();

    int getScreenWidth();

    int getScreenHeight();

}
