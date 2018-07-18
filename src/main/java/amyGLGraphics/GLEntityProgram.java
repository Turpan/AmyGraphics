package amyGLGraphics;

import org.joml.Vector3f;

public class GLEntityProgram extends GLWorldProgram {
	
	private int[] pointLightAmbientLocation = new int[GLRoomRenderer.POINTLIGHTCOUNT];
	private int[] pointLightDiffuseLocation = new int[GLRoomRenderer.POINTLIGHTCOUNT];
	private int[] pointLightSpecularLocation = new int[GLRoomRenderer.POINTLIGHTCOUNT];
	private int[] pointLightPositionLocation = new int[GLRoomRenderer.POINTLIGHTCOUNT];
	
	private int dirLightAmbientLocation;
	private int dirLightDiffuseLocation;
	private int dirLightSpecularLocation;
	private int dirLightDirectionLocation;
	
	private int viewPositionLocation;
	
	public GLEntityProgram() {
		
	}
	
	public void createProgram() {
		super.createProgram();
		setUpVars();
	}
	
	@Override
	protected void createShaders() {
		createVertexShader("shaders/entityvertex.glsl");
		createFragmentShader("shaders/entityfragment.glsl");
	}
	
	@Override
	protected void setUpVars() {
		super.setUpVars();
		
		for (int i=0; i<GLRoomRenderer.POINTLIGHTCOUNT; i++) {
			pointLightAmbientLocation[i] = queryVariable("pointLights[" + i + "].ambient");
			pointLightDiffuseLocation[i] = queryVariable("pointLights[" + i + "].diffuse");
			pointLightSpecularLocation[i] = queryVariable("pointLights[" + i + "].specular");
			pointLightPositionLocation[i] = queryVariable("pointLights[" + i + "].position");
		}
		
		dirLightAmbientLocation = queryVariable("dirLight.ambient");
		dirLightDiffuseLocation = queryVariable("dirLight.diffuse");
		dirLightSpecularLocation = queryVariable("dirLight.specular");
		dirLightDirectionLocation = queryVariable("dirLight.direction");
		
		viewPositionLocation = queryVariable("viewPosition");
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
	
	public void updateViewPosition(Vector3f position) {
		updateVec3(position, viewPositionLocation);
	}
}
