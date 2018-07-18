package amyGLGraphics;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

public class GLBackground extends GLObject {
	static final float[] coords = {
			-1.0f, 1.0f,
			1.0f, 1.0f,
			1.0f, -1.0f,
			-1.0f, -1.0f
	};
	private GLTexture texture;
	private int cameraPositionX = 0;
	private int cameraPositionY = 0;
	private int width = 0;
	private int height = 0;
	public GLBackground(BufferedImage sprite) {
		this();
		if (sprite == null) {
			throw new NullPointerException("Background cannot be null");
		}
		setSprite(sprite);
	}
	public GLBackground() {
		super.setDrawOrder();
		createVertices();
		calculateVertices();
	}
	
	public void setSprite(BufferedImage sprite) {
		texture = new GLTexture(sprite);
		width = sprite.getWidth();
		height = sprite.getHeight();
		calculateVertices();
		if (isGLBound()) {
			unbindObject();
		}
		bindBuffer();
	}
	public void setCameraPosition(int x, int y) {
		cameraPositionX = x;
		cameraPositionY = y;
		calculateVertices();
	}

	@Override
	protected void createVertices() {
		for (int i=0; i<4; i++) {
			var vertex = new GLVertex();
			vertices.add(vertex);
		}
		initialiseVertices();
	}
	
	private void initialiseVertices() {
		for (int i=0; i<vertices.size(); i++) {
			var vertex = vertices.get(i);
			vertex.setXY(coords[i*2], coords[(i*2) + 1]);
			vertex.setZ(-1f);
		}
	}

	public int getTextureID() {
		return texture.getTextureID();
	}
	@Override
	public BufferedImage getSprite() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected void calculateVertices() {
		float top;
		float bottom;
		float left;
		float right;
		if (width <= viewWidth) {
			left = 0.0f;
			right = 1.0f;
		} else {
			left = calculateRelativePosition(cameraPositionX, viewWidth);
			right = calculateRelativePosition(cameraPositionX + viewWidth, viewWidth);
		}
		if (height <= viewHeight) {
			top = 0.0f;
			bottom = 1.0f;
		} else {
			top = calculateRelativePosition(cameraPositionY, viewHeight);
			bottom = calculateRelativePosition(cameraPositionY + viewHeight, viewHeight);
		}
		setVertices(top, bottom, left, right);
	}
	
	private void setVertices(float top, float bottom, float left, float right) {
		vertices.get(0).setST(left, top);
		vertices.get(1).setST(right, top);
		vertices.get(2).setST(right, bottom);
		vertices.get(3).setST(left, bottom);
		vertices.get(0).setRGB(1, 0, 0);
		vertices.get(1).setRGB(0, 1, 0);
		vertices.get(2).setRGB(0, 0, 1);
		vertices.get(3).setRGB(1, 1, 0);
	}
	@Override
	protected byte[] createDrawOrder() {
		return new byte[] {
				0, 1, 2,
	            2, 3, 0
		};
	}
	@Override
	public void update() {
		updateBuffer();
	}
}
