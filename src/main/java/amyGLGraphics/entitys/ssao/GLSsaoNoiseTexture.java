package amyGLGraphics.entitys.ssao;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGB;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import amyGLGraphics.GLTextureColour;

public class GLSsaoNoiseTexture extends GLTextureColour {

	public GLSsaoNoiseTexture(ByteBuffer buffer) {
		super(GLSsaoRenderer.NOISESIZE, GLSsaoRenderer.NOISESIZE, GL30.GL_RGB16F, GL_RGB, GL_FLOAT, GL_NEAREST);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTextureID());
		GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, GLSsaoRenderer.NOISESIZE,
				GLSsaoRenderer.NOISESIZE, GL_RGB, GL_FLOAT, buffer);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);  
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

}
