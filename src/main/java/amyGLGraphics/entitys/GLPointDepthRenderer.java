package amyGLGraphics.entitys;

import java.util.List;

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

public class GLPointDepthRenderer extends GLRenderer {

	public static final Vector3f[] directions = new Vector3f[] {
			new Vector3f(1.0f, 0.0f, 0.0f),
			new Vector3f(-1.0f, 0.0f, 0.0f),
			new Vector3f(0.0f, 1.0f, 0.0f),
			new Vector3f(0.0f, -1.0f, 0.0f),
			new Vector3f(0.0f, 0.0f, 1.0f),
			new Vector3f(0.0f, 0.0f, -1.0f)
	};

	public static final Vector3f[] up = new Vector3f[] {
			new Vector3f(0.0f, -1.0f, 0.0f),
			new Vector3f(0.0f, -1.0f, 0.0f),
			new Vector3f(0.0f, 0.0f, 1.0f),
			new Vector3f(0.0f, 0.0f, -1.0f),
			new Vector3f(0.0f, -1.0f, 0.0f),
			new Vector3f(0.0f, -1.0f, 0.0f)
	};

	private GLPointDepthProgram depthProgram;
	private GLShadowMapPoint shadowBuffer;

	private Light light;
	private GLObject lightObject;

	private int face;

	private boolean softShadow;

	public GLPointDepthRenderer(Light light, GLObject lightObject) {
		super();
		this.light = light;
		this.lightObject = lightObject;
		
		shadowBuffer.setSoftShadows(softShadow);
	}

	public GLShadowMapPoint getShadowMap() {
		return shadowBuffer;
	}

	public void setSoftShadow(boolean softShadow) {
		if (softShadow == this.softShadow) {
			return;
		}

		this.softShadow = softShadow;
		shadowBuffer.setSoftShadows(softShadow);
	}

	@Override
	public void render(List<GLObject> objects) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, shadowBuffer.getBufferID());
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
		for (int i=0; i<6; i++) {
			face = i;
			super.render(objects);
		}
	}

	@Override
	protected void createProgram() {
		depthProgram = new GLPointDepthProgram();

		shadowBuffer = new GLShadowMapPoint(GLGraphicsHandler.shadowWidth*6, GLGraphicsHandler.shadowHeight);
	}

	@Override
	protected void updateUniversalUniforms() {
		depthProgram.updateLightPosition(getLightPosition(lightObject));

		Matrix4f matrix = getLightMatrix(lightObject, face);

		depthProgram.updateLightMatrix(matrix);

		depthProgram.updateSoftShadow(softShadow);
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
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, shadowBuffer.getBufferID());
		GL11.glViewport(GLGraphicsHandler.shadowWidth * face, 0, GLGraphicsHandler.shadowWidth, GLGraphicsHandler.shadowHeight);
	}

	@Override
	protected void resetGlobal() {
		GL11.glEnable(GL11.GL_CULL_FACE);
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

	public static Vector3f getLightPosition(GLObject lightObject) {
		Vector3f lightPosition;

		if (lightObject != null) {
			Vector4f position = lightObject.getVertices().get(0).xyzwVector();
			lightPosition = new Vector3f(position.x, position.y, position.z);

			lightPosition = lightObject.getModelMatrix().transformPosition(lightPosition);
		} else {
			lightPosition = new Vector3f();
		}

		return lightPosition;
	}

	public static Matrix4f getLightMatrix(GLObject lightObject, int i) {
		if (lightObject != null) {
			Vector3f centre = new Vector3f();

			Vector3f lightPosition = getLightPosition(lightObject);

			centre = lightPosition.add(directions[i], centre);

			Matrix4f cameraMatrix = new Matrix4f().lookAt
					(lightPosition, centre, up[i]);

			Matrix4f result = new Matrix4f();

			result = GLPointDepthProgram.perspective.mul(cameraMatrix, result);

			return result;
		} else {
			return new Matrix4f();
		}
	}

}
