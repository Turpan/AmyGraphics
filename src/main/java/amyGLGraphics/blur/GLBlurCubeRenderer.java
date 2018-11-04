package amyGLGraphics.blur;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.Map;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;

import amyGLGraphics.GLTexture;
import amyGLGraphics.base.GLFrameBuffer;
import amyGLGraphics.base.GLGraphicsHandler;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLProgram;
import amyGLGraphics.base.GLRenderer;
import amyGLGraphics.base.GLWindow;
import amyGLGraphics.depthDebug.GLFrameBufferDisplay;

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
