package amyGLGraphics;

public class GLVertex {
	float[] xyzw = new float[] {1f, 1f, 0f, 1f};
	float[] abc = new float[] {0.0f, 0.0f, 0.0f};
	float[] rgba = new float[] {1f, 0f, 1f, 1f};
	float[] st = new float[] {0f, 0f};
    public static final int elementBytes = 4;
    public static final int positionElementCount = 4;
    public static final int normalElementCount = 3;
    public static final int colorElementCount = 4;
    public static final int textureElementCount = 2;
    public static final int positionBytesCount = positionElementCount * elementBytes;
    public static final int normalByteCount = normalElementCount * elementBytes;
    public static final int colorByteCount = colorElementCount * elementBytes;
    public static final int textureByteCount = textureElementCount * elementBytes;
    public static final int positionByteOffset = 0;
    public static final int normalByteOffset = positionByteOffset + positionBytesCount;
    public static final int colorByteOffset = normalByteOffset + normalByteCount;
    public static final int textureByteOffset = colorByteOffset + colorByteCount;
    public static final int elementCount = positionElementCount + normalElementCount + 
            colorElementCount + textureElementCount;    
    public static final int stride = positionBytesCount + normalByteCount + colorByteCount + 
            textureByteCount;
    
    public void setX(float x) {
    	xyzw[0] = x;
    }
    
    public void setY(float y) {
    	xyzw[1] = y;
    }
    
    public void setXY(float x, float y) {
        setX(x);
        setY(y);
    }
    
    public void setXYZ(float x, float y, float z) {
    	setXY(x, y);
    	setZ(z);
    }
    
    public void setZ(float z) {
    	xyzw[2] = z;
    }
    
    public void setW(float w) {
    	xyzw[3] = w;
    }
    
    public void setZW(float z, float w) {
    	setZ(z);
    	setW(w);
    }
    
    public void setXYZW(float x, float y, float z, float w) {
    	setXY(x, y);
    	setZW(z, w);
    }
    
    public void setA(float a) {
    	abc[0] = a;
    }
    
    public void setB(float b) {
    	abc[1] = b;
    }
    
    public void setC(float c) {
    	abc[2] = c;
    }
    
    public void setABC(float a, float b, float c) {
    	setA(a);
    	setB(b);
    	setC(c);
    }
    
    public void setRGB(float r, float g, float b) {
        this.setRGBA(r, g, b, 1f);
    }
    public void setRGBA(float r, float g, float b, float a) {
        this.rgba = new float[] {r, g, b, 1f};
    }
    public void setST(float s, float t) {
        this.st = new float[] {s, t};
    }
    public float[] getElements() {
    	return new float[] {
    			xyzw[0],
    			xyzw[1],
    			xyzw[2],
    			xyzw[3],
    			abc[0],
    			abc[1],
    			abc[2],
    			rgba[0],
    			rgba[1],
    			rgba[2],
    			rgba[3],
    			st[0],
    			st[1]
    	};
    }
}
