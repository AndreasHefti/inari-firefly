package com.inari.firefly.physics.animation.timeline;

import com.inari.firefly.graphics.sprite.SpriteSetAsset.Sprite;
import com.inari.firefly.physics.animation.Frame;

public final class SpriteIdFrame implements Frame.IntFrame {
    
    private final Sprite sprite;
    private final long time;

    public SpriteIdFrame( Sprite sprite, long time ) {
        this.sprite = sprite;
        this.time = time;
    }

    public final long intervalTime() {
        return time;
    }

    public final int value() {
        return sprite.getInstanceId();
    }
}
