package amyGLGraphics.entitys.deferred;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import amyGLGraphics.entitys.GLPointDepthProgram;
import amyGLGraphics.entitys.GLRoomHandler;
import amyGLGraphics.entitys.GLWorldProgram;

public class GLLPassProgram extends GLWorldProgram {

	static final int positionTextureUnit = 0;
	static final int normalTextureUnit = 1;
	static final int colourTextureUnit = 2;
	static final int dirDepthTextureUnit = 3;
	static final int pointDepthTextureUnit = 4;

	private int dirLightAmbientLocation;
	private int dirLightDiffuseLocation;
	private int dirLightSpecularLocation;
	private int dirLightDirectionLocation;
	
	private int[] pointLightAmbientLocation = new int[GLRoomHandler.DEFPOINTLIGHTCOUNT];
	private int[] pointLightDiffuseLocation = new int[GLRoomHandler.DEFPOINTLIGHTCOUNT];
	private int[] pointLightSpecularLocation = new int[GLRoomHandler.DEFPOINTLIGHTCOUNT];
	private int[] pointLightPositionLocation = new int[GLRoomHandler.DEFPOINTLIGHTCOUNT];
	
	private int dirLightMatrixLocation;
	
	private int viewPositionLocation;
	
	private int softShadowLocation;
	
	private int farPlaneLocation;
	
	private int gammaLocation;
	
	private int positionTextureLocation;
	private int normalTextureLocation;
	private int albedoTextureLocation;
	
	private int dirShadowMapLocation;
	private int[] pointShadowMapLocation = new int[GLRoomHandler.POINTLIGHTCOUNT];

	public GLLPassProgram() {

	}

	@Override
	protected void createShaders() {
		createVertexShader("shaders/lightpassvertex.glsl");
		createFragmentShader("shaders/lightpassfragment.glsl");
	}

	@Override
	protected void setUpVars() {
		super.setUpVars();
		
		dirLightAmbientLocation = queryVariable("dirLight.ambient");
		dirLightDiffuseLocation = queryVariable("dirLight.diffuse");
		dirLightSpecularLocation = queryVariable("dirLight.specular");
		dirLightDirectionLocation = queryVariable("dirLight.direction");
		
		for (int i=0; i<GLRoomHandler.DEFPOINTLIGHTCOUNT; i++) {
			pointLightAmbientLocation[i] = queryVariable("pointLights[" + i + "].ambient");
			pointLightDiffuseLocation[i] = queryVariable("pointLights[" + i + "].diffuse");
			pointLightSpecularLocation[i] = queryVariable("pointLights[" + i + "].specular");
			pointLightPositionLocation[i] = queryVariable("pointLights[" + i + "].position");
		}
		
		dirLightMatrixLocation = queryVariable("dirLightMatrix");
		
		viewPositionLocation = queryVariable("viewPosition");
		
		positionTextureLocation = queryVariable("position_texture");
		normalTextureLocation = queryVariable("normal_texture");
		albedoTextureLocation = queryVariable("albedo_texture");
		
		dirShadowMapLocation = queryVariable("dirShadowMap");
		
		for (int i=0; i<GLRoomHandler.POINTLIGHTCOUNT; i++) {
			pointShadowMapLocation[i] = queryVariable("pointShadowMap[" + i + "]");
			updatePointShadowMapUnit(i + pointDepthTextureUnit, i);
		}
		
		softShadowLocation = queryVariable("softshadows");
		
		farPlaneLocation = queryVariable("farPlane");
		
		gammaLocation = queryVariable("gamma");
		
		updatePositionTextureUnit(positionTextureUnit);
		updateNormalTextureUnit(normalTextureUnit);
		updateAlbedoTextureUnit(colourTextureUnit);
		
		updateDirShadowMapUnit(dirDepthTextureUnit);
		
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

	public void updateViewPosition(Vector3f position) {
		updateVec3(position, viewPositionLocation);
	}
	
	public void updateSoftShadow(boolean softShadow) {
		updateBoolean(softShadow, softShadowLocation);
	}
	
	public void updateFarPlane(float farPlane) {
		updateFloat(farPlane, farPlaneLocation);
	}
	
	public void updateGamma(float gamma) {
		updateFloat(gamma, gammaLocation);
	}

	public void updatePositionTextureUnit(int unit) {
		updateInt(unit, positionTextureLocation);
	}
	
	public void updateNormalTextureUnit(int unit) {
		updateInt(unit, normalTextureLocation);
	}
	
	public void updateAlbedoTextureUnit(int unit) {
		updateInt(unit, albedoTextureLocation);
	}
	
	public void updateDirShadowMapUnit(int unit) {
		updateInt(unit, dirShadowMapLocation);
	}
	
	public void updatePointShadowMapUnit(int unit, int index) {
		updateInt(unit, pointShadowMapLocation[index]);
	}
}
