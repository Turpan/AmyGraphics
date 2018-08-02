package amyGLGraphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class GLPointDepthProgram extends GLWorldProgram{
	
	public static final float fov = (float) Math.toRadians(90.0);
	
	public static final float aspect = GLGraphicsHandler.shadowHeight / GLGraphicsHandler.shadowWidth;
	
	public static final float near = 0.1f;
	
	public static final float far = 15.0f;
	
	public static final Matrix4f perspective
	= new Matrix4f().perspective(fov, aspect, near, far);
	
	private int lightPositionLocation;
	
	private int farPlaneLocation;
	
	private int[] lightMatrixLocation = new int[6];

	@Override
	protected void createShaders() {
		createVertexShader("shaders/pointlightdepthvertex.glsl");
		createGeometryShader("shaders/pointlightdepthgeom.glsl");
		createFragmentShader("shaders/pointlightdepthfragment.glsl");
	}
	
	@Override
	protected void setUpVars() {
		super.setUpVars();
		
		lightPositionLocation = queryVariable("lightPosition");
		
		farPlaneLocation = queryVariable("farPlane");
		
		for (int i=0; i<6; i++) {
			lightMatrixLocation[i] = queryVariable("lightMatrices[" + i + "]");
		}
		
		updateFarPlane(far);
		updatePersMatrix(perspective);
	}

	public void updateLightMatrix(Matrix4f pointLightMatrix, int count) {
		updateMat4(pointLightMatrix, lightMatrixLocation[count]);
	}
	
	public void updateLightPosition(Vector3f lightLocation) {
		updateVec3(lightLocation, lightPositionLocation);
	}
	
	public void updateFarPlane(float farPlane) {
		updateFloat(farPlane, farPlaneLocation);
	}

}
