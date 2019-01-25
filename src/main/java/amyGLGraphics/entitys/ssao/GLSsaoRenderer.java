package amyGLGraphics.entitys.ssao;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import amyGLGraphics.GLTextureColour;
import amyGLGraphics.base.GLFrameBuffer;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLProgram;
import amyGLGraphics.base.GLRenderer;
import amyGLGraphics.base.GLWindow;

public class GLSsaoRenderer extends GLRenderer {
	
	public static final int SAMPLECOUNT = 64;
	public static final int NOISESIZE = 4;
	
	private final Vector3f[] samples = new Vector3f[SAMPLECOUNT];
	private final Vector3f[] noise = new Vector3f[NOISESIZE*NOISESIZE];
	
	private GLTextureColour noiseTexture;
	
	private GLSsaoProgram program;
	private GLSsaoBlurProgram blurProgram;
	
	private GLSsaoBuffer buffer1;
	private GLSsaoBuffer buffer2;
	
	private boolean blurStage;
	
	public GLSsaoRenderer()  {
		super();
		
		Random random = new Random();
		for (int i=0; i<samples.length; i++) {
			float x = random.nextFloat() * 2.0f - 1.0f;
			float y = random.nextFloat() * 2.0f - 1.0f;
			float z = random.nextFloat();
			
			Vector3f sample = new Vector3f(x, y, z);
			sample.normalize();
			sample.mul(random.nextFloat());
			float scale = (float) i / 64.0f;
			scale = lerp(0.1f, 1.0f, scale * scale);
			sample.mul(scale);
			
			samples[i] = sample;
		}
		
		for (int i=0; i<noise.length; i++) {
			float x = random.nextFloat() * 2.0f - 1.0f;
			float y = random.nextFloat() * 2.0f - 1.0f;
			float z = 0.0f;
			
			Vector3f rotation = new Vector3f(x, y, z);
			
			noise[i] = rotation;
		}
		
		createNoiseTexture();
	}
	
	private void createNoiseTexture() {
		ByteBuffer buffer = BufferUtils.createByteBuffer(NOISESIZE * NOISESIZE * 3);
		int i = 0;
		for (Vector3f noise : this.noise) {
			noise.get(i*3, buffer);
			i++;
		}
		
		noiseTexture = new GLSsaoNoiseTexture(buffer);
	}
	
	public GLTextureColour getNoiseTexture() {
		return noiseTexture;
	}
	
	public GLFrameBuffer getSSAOBuffer() {
		return buffer2;
	}
	
	private float lerp(float a, float b, float f) {
		return a + f * (b - a);
	}

	@Override
	protected void createProgram() {
		program = new GLSsaoProgram();
		blurProgram = new GLSsaoBlurProgram();
		blurProgram.createProgram();
		
		buffer1 = new GLSsaoBuffer();
		buffer2 = new GLSsaoBuffer();
	}

	@Override
	protected void updateUniversalUniforms() {
		
	}
	
	@Override
	public void resetState() {
		super.resetState();
		
		blurProgram.createProgram();
		
		buffer1.createFrameBuffer();
		buffer2.createFrameBuffer();
		
		createNoiseTexture();
	}
	
	@Override
	public void unbindGL() {
		super.unbindGL();
		
		blurProgram.unbindProgram();
		
		buffer1.unbindBuffer();
		buffer2.unbindBuffer();
		
		noiseTexture.unbindTexture();
	}
	
	@Override
	public void render(GLObject object, GLFrameBuffer geomBuffer) {
		object.getTextures().clear();
		
		object.getTextures().put(geomBuffer.getColourTextures().get(0), GL13.GL_TEXTURE0 + GLSsaoProgram.positionTextureUnit);
		object.getTextures().put(geomBuffer.getColourTextures().get(1), GL13.GL_TEXTURE0 + GLSsaoProgram.normalTextureUnit);
		object.getTextures().put(noiseTexture, GL13.GL_TEXTURE0 + GLSsaoProgram.noiseTextureUnit);
		
		float scaleX = (float) GLWindow.getWindowWidth() / (float) NOISESIZE;
		float scaleY = (float) GLWindow.getWindowHeight() / (float) NOISESIZE;
		
		Vector2f noiseScale = new Vector2f(scaleX, scaleY);
		
		program.updateNoiseScale(noiseScale);
		
		if (camera != null) {
			program.updateViewMatrix(camera.getCameraMatrix());
		}
		
		glDisable(GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, buffer1.getBufferID());
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		super.render(object);
		
		object.getTextures().clear();
		
		object.getTextures().put(buffer1.getColourTextures().get(0), GL13.GL_TEXTURE0);
		
		blurStage = true;
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, buffer2.getBufferID());
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		super.render(object);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		glEnable(GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		
		blurStage = false;
	}

	@Override
	protected void updateUniforms(GLObject object) {
		// TODO Auto-generated method stub

	}

	@Override
	protected GLProgram getProgram() {
		return blurStage ? blurProgram : program;
	}

	@Override
	protected void globalSetup() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void resetGlobal() {
		// TODO Auto-generated method stub

	}

}
