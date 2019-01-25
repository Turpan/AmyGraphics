package amyGLGraphics.entitys.ssao;

import org.joml.Vector2f;
import org.joml.Vector3f;

import amyGLGraphics.entitys.GLWorldProgram;

public class GLSsaoProgram extends GLWorldProgram {
	
	public static final int positionTextureUnit = 0;
	public static final int normalTextureUnit = 1;
	public static final int noiseTextureUnit = 2;
	
	private int noiseScaleLocation;
	private int[] sampleLocation = new int[GLSsaoRenderer.SAMPLECOUNT];
	
	private int positionTextureLocation;
	private int normalTextureLocation;
	private int noiseTextureLocation;
	
	@Override
	protected void createShaders() {
		createVertexShader("shaders/ssaovertex.glsl");
		createFragmentShader("shaders/ssaofragment.glsl");
	}
	
	@Override
	protected void setUpVars() {
		super.setUpVars();
		
		noiseScaleLocation = queryVariable("noise_scale");
		
		for (int i=0; i<GLSsaoRenderer.SAMPLECOUNT; i++) {
			sampleLocation[i] = queryVariable("samples[" + i + "]");
		}
		
		positionTextureLocation = queryVariable("position_texture");
		normalTextureLocation = queryVariable("normal_texture");
		noiseTextureLocation = queryVariable("noise_texture");
		
		updatePositionTextureUnit(positionTextureUnit);
		updateNormalTextureUnit(normalTextureUnit);
		updateNoiseTextureUnit(noiseTextureUnit);
	}
	
	public void updateNoiseScale(Vector2f noiseScale) {
		updateVec2(noiseScale, noiseScaleLocation);
	}
	
	public void updateSample(Vector3f sample, int index) {
		updateVec3(sample, sampleLocation[index]);
	}
	
	public void updatePositionTextureUnit(int unit) {
		updateInt(unit, positionTextureLocation);
	}
	
	public void updateNormalTextureUnit(int unit) {
		updateInt(unit, normalTextureLocation);
	}
	
	public void updateNoiseTextureUnit(int unit) {
		updateInt(unit, noiseTextureLocation);
	}
}
