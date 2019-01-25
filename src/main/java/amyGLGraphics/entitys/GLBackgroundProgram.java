package amyGLGraphics.entitys;

import amyGLGraphics.base.GLProgram;

public class GLBackgroundProgram extends GLProgram {
	
	public static final int background1TextureUnit = 0;
	public static final int background2TextureUnit = 1;
	
	private int background1TextureLocation;
	private int background2TextureLocation;
	
	private int blendLocation;

	public GLBackgroundProgram() {
		super();
	}

	@Override
	protected void linkProgram() {
		super.linkProgram();
		setUpVars();
	}

	public void setUpVars() {		
		background1TextureLocation = queryVariable("background1");
		background2TextureLocation = queryVariable("background2");
		
		blendLocation = queryVariable("blend");
		
		updateSkybox1TextureUnit(background1TextureUnit);
		updateSkybox2TextureUnit(background2TextureUnit);
	}

	@Override
	protected void createShaders() {
		createVertexShader("shaders/backgroundvertex.glsl");
		createFragmentShader("shaders/backgroundfragment.glsl");
	}
	
	public void updateSkybox1TextureUnit(int unit) {
		updateInt(unit, background1TextureLocation);
	}
	
	public void updateSkybox2TextureUnit(int unit) {
		updateInt(unit, background2TextureLocation);
	}
	
	public void updateBlend(float blend) {
		updateFloat(blend, blendLocation);
	}

}
