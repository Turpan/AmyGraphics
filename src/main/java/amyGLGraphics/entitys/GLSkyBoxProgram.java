package amyGLGraphics.entitys;

public class GLSkyBoxProgram extends GLWorldProgram {
	
	public static final int skybox1TextureUnit = 0;
	public static final int skybox2TextureUnit = 1;
	
	private int skybox1TextureLocation;
	private int skybox2TextureLocation;
	
	private int blendLocation;

	public GLSkyBoxProgram() {

	}

	@Override
	public void createProgram() {
		super.createProgram();
	}

	@Override
	public void setUpVars() {
		super.setUpVars();
		
		skybox1TextureLocation = queryVariable("skybox1");
		skybox2TextureLocation = queryVariable("skybox2");
		
		blendLocation = queryVariable("blend");
		
		updateSkybox1TextureUnit(skybox1TextureUnit);
		updateSkybox2TextureUnit(skybox2TextureUnit);
	}

	@Override
	protected void createShaders() {
		createVertexShader("shaders/skyvertex.glsl");
		createFragmentShader("shaders/skyfragment.glsl");

		//createFragmentShader("shaders/skyboxdepthfragment.glsl");
	}
	
	public void updateSkybox1TextureUnit(int unit) {
		updateInt(unit, skybox1TextureLocation);
	}
	
	public void updateSkybox2TextureUnit(int unit) {
		updateInt(unit, skybox2TextureLocation);
	}
	
	public void updateBlend(float blend) {
		updateFloat(blend, blendLocation);
	}

}
