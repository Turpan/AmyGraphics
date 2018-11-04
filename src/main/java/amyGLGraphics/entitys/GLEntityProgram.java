package amyGLGraphics.entitys;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL13;

public class GLEntityProgram extends GLWorldProgram {
	
	static final int diffuseTextureUnit = 0;
	static final int dirDepthTextureUnit = 1;
	static final int pointDepthTextureUnit = 2;
	
	private int[] pointLightAmbientLocation = new int[GLRoomHandler.POINTLIGHTCOUNT];
	private int[] pointLightDiffuseLocation = new int[GLRoomHandler.POINTLIGHTCOUNT];
	private int[] pointLightSpecularLocation = new int[GLRoomHandler.POINTLIGHTCOUNT];
	private int[] pointLightPositionLocation = new int[GLRoomHandler.POINTLIGHTCOUNT];
	
	private int dirLightAmbientLocation;
	private int dirLightDiffuseLocation;
	private int dirLightSpecularLocation;
	private int dirLightDirectionLocation;
	
	private int dirLightMatrixLocation;
	
	private int softShadowLocation;
	
	private int viewPositionLocation;
	
	private int gammaLocation;
	
	private int diffuseTextureLocation;
	private int dirShadowMapLocation;
	
	private int[] pointShadowMapLocation = new int[GLRoomHandler.POINTLIGHTCOUNT];
	
	private int farPlaneLocation;
	
	public GLEntityProgram() {
		
	}
	
	@Override
	protected void createShaders() {
		createVertexShader("shaders/entityvertex.glsl");
		createFragmentShader("shaders/entityfragment.glsl");
	}
	
	@Override
	protected void setUpVars() {
		super.setUpVars();
		
		for (int i=0; i<GLRoomHandler.POINTLIGHTCOUNT; i++) {
			pointLightAmbientLocation[i] = queryVariable("pointLights[" + i + "].ambient");
			pointLightDiffuseLocation[i] = queryVariable("pointLights[" + i + "].diffuse");
			pointLightSpecularLocation[i] = queryVariable("pointLights[" + i + "].specular");
			pointLightPositionLocation[i] = queryVariable("pointLights[" + i + "].position");
		}
		
		dirLightAmbientLocation = queryVariable("dirLight.ambient");
		dirLightDiffuseLocation = queryVariable("dirLight.diffuse");
		dirLightSpecularLocation = queryVariable("dirLight.specular");
		dirLightDirectionLocation = queryVariable("dirLight.direction");
		
		dirLightMatrixLocation = queryVariable("dirLightMatrix");
		
		softShadowLocation = queryVariable("softshadows");
		
		viewPositionLocation = queryVariable("viewPosition");
		
		gammaLocation = queryVariable("gamma");
		
		diffuseTextureLocation = queryVariable("texture_diffuse");
		
		dirShadowMapLocation = queryVariable("dirShadowMap");
		
		for (int i=0; i<GLRoomHandler.POINTLIGHTCOUNT; i++) {
			//pointShadowMapLocation[i] = queryVariable("pointShadowMap[" + i + "]");
			
			pointShadowMapLocation[i] = queryVariable("pointShadowMap");
			
			//updatePointShadowMapUnit(i + pointDepthTextureUnit, i);
		}
		
		farPlaneLocation = queryVariable("farPlane");
		
		updateDiffuseTextureUnit(diffuseTextureUnit);
		updateDirShadowMapUnit(dirDepthTextureUnit);
		
		updatePointShadowMapUnit(0 + pointDepthTextureUnit, 0);
		
		updateFarPlane(GLPointDepthProgram.far);
	}
	
	public void updatePointLightAmbient(Vector3f colour, int count) {
		updateVec3(colour, pointLightAmbientLocation[count]);
	}
	
	public void updatePointLightDiffuse(Vector3f colour, int count) {
		updateVec3(colour, pointLightDiffuseLocation[count]);
	}
	
	public void updatePointLightSpecular(Vector3f colour, int count) {
		updateVec3(colour, pointLightSpecularLocation[count]);
	}
	
	public void updatePointLightPosition(Vector3f position, int count) {
		updateVec3(position, pointLightPositionLocation[count]);
	}
	
	public void updateDirLightAmbient(Vector3f colour) {
		updateVec3(colour, dirLightAmbientLocation);
	}
	
	public void updateDirLightDiffuse(Vector3f colour) {
		updateVec3(colour, dirLightDiffuseLocation);
	}
	
	public void updateDirLightSpecular(Vector3f colour) {
		updateVec3(colour, dirLightSpecularLocation);
	}
	
	public void updateDirLightDirection(Vector3f direction) {
		updateVec3(direction, dirLightDirectionLocation);
	}
	
	public void updateDirLightMatrix(Matrix4f matrix) {
		updateMat4(matrix, dirLightMatrixLocation);
	}
	
	public void updateSoftShadow(boolean softShadow) {
		updateBoolean(softShadow, softShadowLocation);
	}
	
	public void updateViewPosition(Vector3f position) {
		updateVec3(position, viewPositionLocation);
	}
	
	public void updateGamma(float gamma) {
		updateFloat(gamma, gammaLocation);
	}
	
	public void updateDiffuseTextureUnit(int unit) {
		updateInt(unit, diffuseTextureLocation);
	}
	
	public void updateDirShadowMapUnit(int unit) {
		updateInt(unit, dirShadowMapLocation);
	}
	
	public void updatePointShadowMapUnit(int unit, int index) {
		updateInt(unit, pointShadowMapLocation[index]);
	}
	
	public void updateFarPlane(float farPlane) {
		updateFloat(farPlane, farPlaneLocation);
	}
}
