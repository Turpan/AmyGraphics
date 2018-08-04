package amyGLGraphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

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
	private GLShadowMapCube shadowBuffer;
	
	private Light light;
	private GLObject lightObject;
	
	public GLPointDepthRenderer(Light light, GLObject lightObject) {
		this.light = light;
		this.lightObject = lightObject;
	}
	
	public GLShadowMapCube getShadowMap() {
		return shadowBuffer;
	}

	@Override
	protected void createProgram() {
		depthProgram = new GLPointDepthProgram();
		
		shadowBuffer = new GLShadowMapCube(GLGraphicsHandler.shadowWidth, GLGraphicsHandler.shadowHeight);
	}

	@Override
	protected void updateUniversalUniforms() {
		depthProgram.updateLightPosition(getLightPosition(lightObject));
		
		Matrix4f[] matrices = getLightMatrices(lightObject);
		
		for (int i=0; i<6; i++) {
			depthProgram.updateLightMatrix(matrices[i], i);
		}
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
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
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
		shadowBuffer.createFrameBuffer();
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
	
	public static Matrix4f[] getLightMatrices(GLObject lightObject) {
		Matrix4f[] matrices = new Matrix4f[6];
		
		if (lightObject != null) {
			
			for (int i=0; i<6; i++) {
				Vector3f centre = new Vector3f();
				
				Vector3f lightPosition = getLightPosition(lightObject);
				
				centre = lightPosition.add(directions[i], centre);
				
				Matrix4f cameraMatrix = new Matrix4f().lookAt
						(lightPosition, centre, up[i]);
				
				Matrix4f result = new Matrix4f();
				
				result = GLPointDepthProgram.perspective.mul(cameraMatrix, result);
				
				matrices[i] = result;
			}
		} else {
			for (int i=0; i<6; i++) {
				matrices[i] = new Matrix4f();
			}
		}
		
		return matrices;
	}

}
