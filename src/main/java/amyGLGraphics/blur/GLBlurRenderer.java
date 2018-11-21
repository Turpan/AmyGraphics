package amyGLGraphics.blur;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import amyGLGraphics.base.GLFrameBuffer;
import amyGLGraphics.base.GLGraphicsHandler;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLProgram;
import amyGLGraphics.base.GLRenderer;
import amyGLGraphics.base.GLWindow;
import amyGLGraphics.depthDebug.GLFrameBufferDisplay;

public class GLBlurRenderer extends GLRenderer {

	static final private float blur = 1.0f;

	static final private Vector2f dirVer = new Vector2f(0.0f, 1.0f);
	static final private Vector2f dirHor = new Vector2f(1.0f, 0.0f);

	static final private int defWidth = GLGraphicsHandler.shadowWidth;
	static final private int defHeight = GLGraphicsHandler.shadowHeight;

	protected GLFrameBufferDisplay displayObject;

	protected GLBlurBuffer buffer;

	protected GLBlurProgram program;

	protected int width = GLGraphicsHandler.shadowWidth;
	protected int height = GLGraphicsHandler.shadowHeight;

	public GLBlurRenderer() {
		super();
	}

	public GLBlurRenderer(int width, int height) {
		this();
		this.width = width;
		this.height = height;
	}

	public void setSize(int width, int height) {
		if (this.width == width && this.height == height) {
			return;
		}

		buffer.setSize(width, height);
		this.width = width;
		this.height = height;
	}

	public void blur(GLFrameBuffer buffer) {
		float blur = GLBlurRenderer.blur / width;
		blur += GLBlurRenderer.blur / height;
		blur /= 2;

		program.setBlur(blur);
		program.setDir(dirHor);

		displayObject.getTextures().clear();
		displayObject.addTexture(buffer.getColourTexture(), GL13.GL_TEXTURE0);

		GL11.glViewport(0, 0, width, height);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.buffer.getBufferID());
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		render(displayObject);

		program.setDir(dirVer);

		displayObject.getTextures().clear();
		displayObject.addTexture(this.buffer.getColourTexture(), GL13.GL_TEXTURE0);

		GL11.glViewport(0, 0, width, height);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, buffer.getBufferID());
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		render(displayObject);

		GL11.glViewport(0, 0, GLWindow.getWindowWidth(), GLWindow.getWindowHeight());
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	@Override
	protected void createProgram() {
		displayObject = new GLFrameBufferDisplay();
		buffer = new GLBlurBuffer(defWidth, defHeight);
		program = new GLBlurProgram();
	}

	@Override
	protected void updateUniversalUniforms() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateUniforms(GLObject object) {
		// TODO Auto-generated method stub

	}

	@Override
	protected GLProgram getProgram() {
		return program;
	}

	@Override
	protected void globalSetup() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void resetGlobal() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetState() {
		super.resetState();
		displayObject.bindBuffer();
		buffer.createFrameBuffer();
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
