package amyGLGraphics.depthDebug;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

import amyGLGraphics.GLObject;
import amyGLGraphics.GLVertex;

public class GLFrameBufferDisplay extends GLObject {
	
	public GLFrameBufferDisplay() {
		super();
		super.setDrawOrder();
		createVertices();
		calculateVertices();
		bindBuffer();
	}

	@Override
	protected byte[] createDrawOrder() {
		return new byte[] {
				0,1,2, 3,2,1
		};
	}

	@Override
	public void update() {
		
	}

	@Override
	protected List<Integer> createAttributePointers() {
		glVertexAttribPointer(VERTEXPOSITION, GLVertex.positionElementCount, GL_FLOAT, 
                false, GLVertex.positionBytesCount, GLVertex.positionByteOffset);
		
		List<Integer> attribPointers = new ArrayList<Integer>();
		attribPointers.add(VERTEXPOSITION);
		return attribPointers;
	}

	@Override
	protected FloatBuffer createVertexBuffer() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(getVertices().size() * GLVertex.positionElementCount);
		for (GLVertex vertex : getVertices()) {
			buffer.put(vertex.xyzw);
		}
		buffer.flip();
		return buffer;
	}

	@Override
	protected void createVertices() {
		for (int i=0; i<4; i++) {
			GLVertex vertex = new GLVertex();
			vertices.add(vertex);
		}
	}

	@Override
	protected void calculateVertices() {
		vertices.get(0).setXY(-1.0f, 1.0f);
		vertices.get(1).setXY(1.0f, 1.0f);
		vertices.get(2).setXY(-1.0f, -1.0f);
		vertices.get(3).setXY(1.0f, -1.0f);
	}

}
