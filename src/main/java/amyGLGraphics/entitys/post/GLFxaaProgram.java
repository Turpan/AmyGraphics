package amyGLGraphics.entitys.post;

import amyGLGraphics.base.GLProgram;

public class GLFxaaProgram extends GLProgram {

	private int invWidthLocation;
	private int invHeightLocation;
	
	@Override
	protected void createShaders() {
		createVertexShader("shaders/fxaavertex.glsl");
		createFragmentShader("shaders/fxaafragment.glsl");
	}
	
	private void setUpVars() {
		invWidthLocation = queryVariable("invwidth");
		invHeightLocation = queryVariable("invheight");
	}
	
	@Override
	protected void linkProgram() {
		super.linkProgram();
		setUpVars();
	}

	public void updateInvWidth(float invwidth) {
		updateFloat(invwidth, invWidthLocation);
	}
	
	public void updateInvHeight(float invheight) {
		updateFloat(invheight, invHeightLocation);
	}
}
