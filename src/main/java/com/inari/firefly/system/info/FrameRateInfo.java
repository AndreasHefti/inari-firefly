package com.inari.firefly.system.info;

import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.FFTimer;

public final class FrameRateInfo implements SystemInfo {
    
    private long lastSecondTime = -1;
    private int frames = 0;
    
    private char[] info = "FPS:.........00".toCharArray();

    @Override
    public final String name() {
        return "FrameRateInfo";
    }

    @Override
    public final int getLength() {
        return info.length;
    }

    @Override
    public final void update( FFContext context, StringBuffer buffer, int bufferStartPointer ) {
        FFTimer timer = context.getTimer();
        if ( lastSecondTime < 0 ) {
            lastSecondTime = timer.getTime();
            setText( buffer, bufferStartPointer );
            frames++;
            return;
        }
        
        frames++;
        long duration = timer.getTime() - lastSecondTime;
        if ( duration > 1000 ) {
            updateFPS( buffer, bufferStartPointer, String.valueOf( frames ) );
            frames = 0;
            lastSecondTime = timer.getTime();
        }
    }
    
    private void updateFPS( StringBuffer buffer, int bufferStartPointer, String fps ) {
        buffer.replace( bufferStartPointer + ( info.length - fps.length() ) ,  bufferStartPointer + info.length, fps );
    }

    private void setText( StringBuffer buffer, int bufferStartPointer ) {
        for ( int i = 0; i < info.length; i++ ) {
            buffer.setCharAt( i + bufferStartPointer, info[ i ] );
        }
    }

}
