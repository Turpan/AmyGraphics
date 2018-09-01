package amyGLGraphics.IO;

import java.awt.Color;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class GraphicsUtils {
	
	public static Vector4f colourToVec4(Color color) {
		float red = color.getRed();
		float green = color.getGreen();
		float blue = color.getBlue();
		float alpha = color.getAlpha();
		
		red = red / 255.0f;
		green = green / 255.0f;
		blue = blue / 255.0f;
		alpha = alpha / 255.0f;
		
		return new Vector4f(red, green, blue, alpha);
    }
	
	public static Vector3f colourToVec3(Color color) {
		float red = color.getRed();
		float green = color.getGreen();
		float blue = color.getBlue();
		
		red = red / 255.0f;
		green = green / 255.0f;
		blue = blue / 255.0f;
		
		return new Vector3f(red, green, blue);
	}
	
}
