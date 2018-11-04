package amyGLGraphics.entitys;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import amyGLGraphics.base.GLGraphicsHandler;

public class GLPointDepthProgram extends GLWorldProgram{
	
	public static final float fov = (float) Math.toRadians(90.0);
	
	public static final float aspect = GLGraphicsHandler.shadowHeight / GLGraphicsHandler.shadowWidth;
	
	public static final float near = 0.1f;
	
	public static final float far = 15.0f;
	
	public static final Matrix4f perspective
	= new Matrix4f().perspective(fov, aspect, near, far);
	
	private int lightPositionLocation;
	
	private int farPlaneLocation;
	
	private int lightMatrixLocation;
	
	private int softShadowLocation;

	@Override
	protected void createShaders() {
		createVertexShader("shaders/pointlightdepthvertex.glsl");
		createFragmentShader("shaders/pointlightdepthfragment.glsl");
	}
	
	@Override
	protected void setUpVars() {
		super.setUpVars();
		
		lightPositionLocation = queryVariable("lightPosition");
		
		farPlaneLocation = queryVariable("farPlane");
		
		lightMatrixLocation = queryVariable("lightMatrix");
		
		softShadowLocation = queryVariable("softshadow");
		
		updateFarPlane(far);
		updatePersMatrix(perspective);
	}

	public void updateLightMatrix(Matrix4f pointLightMatrix) {
		updateMat4(pointLightMatrix, lightMatrixLocation);
	}
	
	public void updateLightPosition(Vector3f lightLocation) {
		updateVec3(lightLocation, lightPositionLocation);
	}
	
	public void updateFarPlane(float farPlane) {
		updateFloat(farPlane, farPlaneLocation);
	}
	
	public void updateSoftShadow(boolean softShadow) {
		updateBoolean(softShadow, softShadowLocation);
	}

}
