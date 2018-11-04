package amyGLGraphics.entitys;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import amyGLGraphics.base.GLGraphicsHandler;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLProgram;
import amyGLGraphics.base.GLRenderer;
import amyGLGraphics.base.GLWindow;
import movement.Light;

public class GLDirDepthRenderer extends GLRenderer{
	
	private GLDirDepthProgram depthProgram;
	private GLShadowMap shadowBuffer;
	
	private Light directionalLight;
	private GLObject dirLightObject;
	
	private boolean softShadow;
	
	public void setDirectionalLight(Light directionalLight, GLObject dirLightObject) {
		this.directionalLight = directionalLight;
		this.dirLightObject = dirLightObject;
	}
	
	public GLShadowMap getShadowMap() {
		return shadowBuffer;
	}
	
	public void setSoftShadow(boolean softShadow) {
		this.softShadow = softShadow;
	}

	@Override
	protected void createProgram() {
		depthProgram = new GLDirDepthProgram();
		
		shadowBuffer = new GLShadowMap(GLGraphicsHandler.shadowWidth, GLGraphicsHandler.shadowHeight);
	}

	@Override
	protected void updateUniversalUniforms() {
		depthProgram.updateSoftShadows(softShadow);
		depthProgram.updateLightMatrix(getDirLightMatrix(dirLightObject));
	}

	@Override
	protected void updateUniforms(GLObject object) {
		depthProgram.updateModelMatrix(object.getModelMatrix());
	}

	@Override
	protected GLProgram getProgram() {
		return depthProgram;
	}

	@Override
	protected void globalSetup() {
		GL11.glViewport(0, 0, GLGraphicsHandler.shadowWidth, GLGraphicsHandler.shadowHeight);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, shadowBuffer.getBufferID());
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
	}

	@Override
	protected void resetGlobal() {
		GL11.glViewport(0, 0, GLWindow.getWindowWidth(), GLWindow.getWindowHeight());
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	@Override
	public void unbindGL() {
		super.unbindGL();
		shadowBuffer.unbindBuffer();
	}
	
	@Override
	public void resetState() {
		super.resetState();
		shadowBuffer.resetState();
	}
	
	public static Matrix4f getDirLightMatrix(GLObject dirLightObject) {
		if (dirLightObject != null) {
			//Vector4f lightPosition = dirLightObject.getVertices().get(0).xyzwVector().mul(dirLightObject.getModelMatrix());
			Vector4f position = dirLightObject.getVertices().get(0).xyzwVector();
			Vector3f lightPosition = new Vector3f(position.x, position.y, position.z);
			
			lightPosition = dirLightObject.getModelMatrix().transformPosition(lightPosition);
			
			Matrix4f cameraMatrix = new Matrix4f().lookAt
					(lightPosition.x, lightPosition.y, lightPosition.z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
			
			Matrix4f result = new Matrix4f();
			
			result = GLDirDepthProgram.perspective.mul(cameraMatrix, result);
			
			return result;
		} else {
			return new Matrix4f();
		}
	}

}
