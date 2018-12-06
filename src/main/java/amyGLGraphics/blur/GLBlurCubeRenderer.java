package amyGLGraphics.blur;

import amyGLGraphics.base.GLGraphicsHandler;
import amyGLGraphics.base.GLProgram;
import amyGLGraphics.debug.GLFrameBufferDisplay;

public class GLBlurCubeRenderer extends GLBlurRenderer {
	static final private float blur = 1.0f;

	static final protected int defWidth = GLGraphicsHandler.shadowWidth * 6;
	static final private int defHeight = GLGraphicsHandler.shadowHeight;

	public GLBlurCubeRenderer() {
		super();
		width = defWidth;
		height= defHeight;
	}

	public GLBlurCubeRenderer(int width, int height) {
		super(width, height);
	}

	@Override
	protected void createProgram() {
		displayObject = new GLFrameBufferDisplay();
		buffer = new GLBlurBuffer(defWidth, defHeight);
		program = new GLBlurCubeProgram();
	}

	@Override
	protected GLProgram getProgram() {
		return program;
	}

	@Override
	public void resetState() {
		super.resetState();
	}

	@Override
	public void unbindGL() {
		if (getProgram() != null && getProgram().isGLBound()) {
			getProgram().unbindProgram();
		}
		displayObject.unbindObject();
		buffer.unbindBuffer();
	}
}
