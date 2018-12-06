package amyGLGraphics.entitys.deferred;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import amyGLGraphics.base.GLFrameBuffer;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLProgram;
import amyGLGraphics.base.GLRenderer;
import amyGLGraphics.base.GLWindow;

public class GLGPassRenderer extends GLRenderer {

	private GLGPassBuffer buffer;
	private GLGPassProgram program;
	
	public GLFrameBuffer getPassBuffer() {
		return buffer;
	}
	
	@Override
	protected void createProgram() {
		program = new GLGPassProgram();
		buffer = new GLGPassBuffer(GLWindow.getWindowWidth(), GLWindow.getWindowHeight());
	}

	@Override
	protected void updateUniversalUniforms() {
		if (camera != null) {
			program.updateViewMatrix(camera.getCameraMatrix());
		}
	}

	@Override
	protected void updateUniforms(GLObject object) {
		program.updateModelMatrix(object.getModelMatrix());
	}

	@Override
	protected GLProgram getProgram() {
		return program;
	}

	@Override
	protected void globalSetup() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, buffer.getBufferID());
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
	}

	@Override
	protected void resetGlobal() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	@Override
	public void unbindGL() {
		super.unbindGL();
		buffer.unbindBuffer();
	}

}
