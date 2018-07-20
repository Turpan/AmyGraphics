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
import java.util.Collections;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public abstract class GLObject {
	public static final int viewWidth = GLGraphicsHandler.viewWidth;
	public static final int viewHeight = GLGraphicsHandler.viewHeight;
	public static final int viewDepth = GLGraphicsHandler.viewDepth;
	public static final int VERTEXPOSITION = GLRoomRenderer.VERTEXPOSITION;
	public static final int NORMALPOSITION = GLRoomRenderer.NORMALPOSITION;
	public static final int COLOURPOSITION = GLRoomRenderer.COLOURPOSITION;
	public static final int TEXTUREPOSITION = GLRoomRenderer.TEXTUREPOSITION;
	static final float[] texturecoords = {
			0.0f, 0.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,
			0.0f, 1.0f
	};
	//static final byte[] draworder = GLRoomRenderer.draworder;
	protected byte[] draworder;
	private int objectID;
	private int objectBufferID;
	private int objectIndicesBufferID;
	private boolean GLBound;
	protected List<GLVertex> vertices = new ArrayList<GLVertex>();
	
	protected abstract byte[] createDrawOrder();
	
	protected void bindBuffer() {
		GLBound = true;
		objectID = glGenVertexArrays();
		glBindVertexArray(objectID);
		FloatBuffer buffer = createVertexBuffer();
		objectBufferID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, objectBufferID);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STREAM_DRAW);
		createAttributePointers();
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        objectIndicesBufferID = createOrderBuffer();
		glBindVertexArray(0);
	}
	protected int createOrderBuffer() {
		ByteBuffer buffer = BufferUtils.createByteBuffer(draworder.length);
		buffer.put(draworder);
		buffer.flip();
		int indicesBufferID = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		return indicesBufferID;
	}
	public void updateBuffer() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(getVertices().size() * GLVertex.elementCount);
		for (var vertex : getVertices()) {
			buffer.put(vertex.getElements());
		}
		buffer.flip();
		glBindVertexArray(objectID);
		glBindBuffer(GL_ARRAY_BUFFER, objectBufferID);
		glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	public abstract void update();
	
	protected abstract void createAttributePointers();
	
	protected abstract FloatBuffer createVertexBuffer();
	
	public void unbindObject() {
		GLBound = false;
		glBindVertexArray(objectID);
		glDeleteBuffers(objectBufferID);
		glDeleteBuffers(objectIndicesBufferID);
		glBindVertexArray(0);
		glDeleteVertexArrays(objectID);
	}
	protected abstract void createVertices();
	
	public List<GLVertex> getVertices() {
		return Collections.unmodifiableList(vertices);
	}
	
	public int getObjectID() {
		return objectID;
	}
	public int getObjectBufferID() {
		return objectBufferID;
	}
	public int getObjectIndicesBufferID() {
		return objectIndicesBufferID;
	}
	
	protected abstract void calculateVertices();
	
	protected float calculateRelativePosition(int coordinate, int totalsize) {
		double percentage = calculatePercentage(coordinate, totalsize); //maths stuff
		percentage = (percentage * 2) - 1; //graphics coordinates go from -1 to 1 so i need percentage that goes from
		return (float) percentage;
	}
	
	protected double calculatePercentage(int a, int b) {
		return (double) a / (double) b;
	}
	
	protected float calculateTranslation(int coordinate, int totalsize) {
		double percentage = calculatePercentage(coordinate, totalsize);
		return (float) (percentage * 2);
	}
	
	protected float flip(double Value) {
		if (Value <= 0) {
			return (float) Math.abs(Value);
		} else {
			return (float) (0-Value);
		}
	}
	
	public boolean isGLBound() {
		return GLBound;
	}

	public int getDrawLength() {
		return draworder.length;
	}
	
	protected void setDrawOrder() {
		draworder = createDrawOrder();
	}
	
	protected Vector3f calculateSurfaceNormal(Vector3f pointA, Vector3f pointB, Vector3f pointC) {
		// this formula https://www.khronos.org/opengl/wiki/Calculating_a_Surface_Normal
		Vector3f U = new Vector3f();
		Vector3f V = new Vector3f();
		
		pointB.sub(pointA, U);
		pointC.sub(pointA, V);
		
		/*float x = (U.y * V.z) - (U.z * V.y);
		float y = (U.z * V.x) - (U.x * V.z);
		float z = (U.x * V.y) - (U.y * V.x);
		
		Vector3f normal = new Vector3f();
		normal.x = x;
		normal.y = y;
		normal.z = z;
		normal = normal.normalize();*/
		
		Vector3f normal;
		//normal = U.cross(V);
		normal = V.cross(U);
		normal = normal.normalize();
		
		return normal;
	}
}
