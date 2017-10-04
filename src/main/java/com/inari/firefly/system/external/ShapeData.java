package com.inari.firefly.system.external;

import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.list.ReadOnlyDynArray;
import com.inari.firefly.graphics.BlendMode;

public interface ShapeData {
    
    public enum Type {
        POINT,
        LINE,
        POLI_LINE,
        POLIGON,
        RECTANGLE,
        CIRCLE,
        CONE,
        ARC,
        CURVE,
        TRIANGLE
    }
    
    Type getShapeType();
    
    float[] getVertices();
    
    int getSegments();
    
    ReadOnlyDynArray<RGBColor> getColors(); 
    
    BlendMode getBlendMode(); boolean isFill();
    
    int getShaderId();

}
