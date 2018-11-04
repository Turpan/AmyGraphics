package amyGLGraphics.blur;

import org.joml.Vector2f;

import amyGLGraphics.base.GLProgram;

public class GLBlurProgram extends GLProgram {
	
	private int blurLocation;
	private int dirLocation;

	@Override
	protected void createShaders() {
		createVertexShader("shaders/blurvertex.glsl");
		createFragmentShader("shaders/blurfragment.glsl");
	}
	
	protected void linkProgram() {
		super.linkProgram();
		setUpVars();
	}
	
	public void setUpVars() {
		blurLocation = queryVariable("blur");
		dirLocation = queryVariable("dir");
	}
	
	public void setBlur(float blur) {
		updateFloat(blur, blurLocation);
	}
	
	public void setDir(Vector2f dir) {
		updateVec2(dir, dirLocation);
	}

}
