package amyGLGraphics;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;

import java.nio.FloatBuffer;

public class GLTextureColourCube extends GLTextureColour {

	public GLTextureColourCube(int width, int height) {
		super(width, height);
	}

	public GLTextureColourCube(int width, int height, int internalformat, int format, int data, int filtering) {
		super(width, height, internalformat, format, data, filtering);
	}

	@Override
	protected void createTexture() {
		createTexture(GL_RGB, GL_RGB, GL_UNSIGNED_BYTE, GL_LINEAR);
	}

	@Override
	protected void createTexture(int internalformat, int format, int data, int filtering) {
		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);

		for (int i=0; i<6; i++) {
			FloatBuffer buffer = null;

			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i
					, 0, internalformat, width, height, 0, format, data, buffer);
		}

		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP,GL_TEXTURE_MAG_FILTER, filtering);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, filtering);
	}

	@Override
	public int getTextureType() {
		return GL_TEXTURE_CUBE_MAP;
	}

}
