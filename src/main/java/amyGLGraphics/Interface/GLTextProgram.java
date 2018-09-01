package amyGLGraphics.Interface;

import org.joml.Vector4f;

import amyGLGraphics.base.GLProgram;

public class GLTextProgram extends GLProgram {

	int colourLocation;

	public GLTextProgram() {
		
	}
	
	public void createProgram() {
		super.createProgram();
		queryVariables();
	}
	
	protected void queryVariables() {
		colourLocation = queryVariable("textColour");
	}
	
	public void setColour(Vector4f colour) {
		updateVec4(colour, colourLocation);
	}

	@Override
	protected void createShaders() {
		createVertexShader("shaders/textvertex.glsl");
		createFragmentShader("shaders/textfragment.glsl");
	}

}
