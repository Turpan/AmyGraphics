package amyGLGraphics;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
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
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import amyGLGraphics.IO.DecodedPNG;

public class GLTextureCube extends GLTexture{

	private DecodedPNG[] texturesData;

	public GLTextureCube(BufferedImage[] faces) {

		texturesData = new DecodedPNG[6];

		for (int i=0; i<faces.length; i++) {
			texturesData[i] = new DecodedPNG(faces[i]);
		}

		createTexture();
	}

	@Override
	protected void createTexture() {
		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);

		for (int i=0; i<6; i++) {
			DecodedPNG textureData = texturesData[i];
			int width = textureData.getWidth();
			int height = textureData.getHeight();
			ByteBuffer byteBuffer = BufferUtils.createByteBuffer(textureData.getData().length);
			byteBuffer.put(textureData.getData());
			byteBuffer.flip();
			//TODO remember to update this later
			/*glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i
					, 0, GL_SRGB_ALPHA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);*/
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i
					, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);
		}

		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP,GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	}

	@Override
	public boolean isTransparent() {
		// skybox should never be transparent
		return false;
	}

	@Override
	public int getTextureType() {
		return GL_TEXTURE_CUBE_MAP;
	}
}
