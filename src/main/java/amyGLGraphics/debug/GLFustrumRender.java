package amyGLGraphics.debug;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLVertex;

public class GLFustrumRender extends GLObject {
	
	public GLFustrumRender() {
		super();
		super.setDrawOrder();
		createVertices();
		calculateVertices();
		colourVertices();
		bindBuffer();
	}
	
	private void colourVertices() {
		/*for (GLVertex vertex : getVertices()) {
			vertex.setRGBA(1f, 0f, 0f, 1f);
		}*/
		
		for (int i=0; i<6; i++) {
			int index = i * 4;
			vertices.get(index).setRGB(1, 0, 0);
			vertices.get(index + 1).setRGB(0, 1, 0);
			vertices.get(index + 2).setRGB(0, 0, 1);
			vertices.get(index + 3).setRGB(1, 1, 0);
		}
	}

	@Override
	protected int[] createDrawOrder() {
		return new int[] {
				1,0,2, 1,2,3,           //Face front
				5,4,6, 5,6,7,           //Face right
				9,8,10, 9,10,11,        //Face left
				13,12,14, 13,14,15,     //Face bottom
				17,16,18, 17,18,19,     //Face top
				21,20,22, 21,22,23,	    //Face back
		};
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}
	
	public void update(List<Vector3f> nearPlane, List<Vector3f> farPlane, List<Vector3f> normals) {
		vertices.get(0).setXYZ(nearPlane.get(0).x, nearPlane.get(0).y, nearPlane.get(0).z);
		vertices.get(1).setXYZ(nearPlane.get(1).x, nearPlane.get(1).y, nearPlane.get(1).z);
		vertices.get(2).setXYZ(nearPlane.get(2).x, nearPlane.get(2).y, nearPlane.get(2).z);
		vertices.get(3).setXYZ(nearPlane.get(3).x, nearPlane.get(3).y, nearPlane.get(3).z);
		vertices.get(4).setXYZ(nearPlane.get(1).x, nearPlane.get(1).y, nearPlane.get(1).z);
		vertices.get(5).setXYZ(nearPlane.get(3).x, nearPlane.get(3).y, nearPlane.get(3).z);
		vertices.get(6).setXYZ(farPlane.get(1).x, farPlane.get(1).y, farPlane.get(1).z);
		vertices.get(7).setXYZ(farPlane.get(3).x, farPlane.get(3).y, farPlane.get(3).z);
		vertices.get(8).setXYZ(farPlane.get(0).x, farPlane.get(0).y, farPlane.get(0).z);
		vertices.get(9).setXYZ(nearPlane.get(0).x, nearPlane.get(0).y, nearPlane.get(0).z);
		vertices.get(10).setXYZ(farPlane.get(2).x, farPlane.get(2).y, farPlane.get(2).z);
		vertices.get(11).setXYZ(nearPlane.get(2).x, nearPlane.get(2).y, nearPlane.get(2).z);
		vertices.get(12).setXYZ(nearPlane.get(3).x, nearPlane.get(3).y, nearPlane.get(3).z);
		vertices.get(13).setXYZ(nearPlane.get(2).x, nearPlane.get(2).y, nearPlane.get(2).z);
		vertices.get(14).setXYZ(farPlane.get(3).x, farPlane.get(3).y, farPlane.get(3).z);
		vertices.get(15).setXYZ(farPlane.get(2).x, farPlane.get(2).y, farPlane.get(2).z);
		vertices.get(16).setXYZ(nearPlane.get(0).x, nearPlane.get(0).y, nearPlane.get(0).z);
		vertices.get(17).setXYZ(nearPlane.get(1).x, nearPlane.get(1).y, nearPlane.get(1).z);
		vertices.get(18).setXYZ(farPlane.get(0).x, farPlane.get(0).y, farPlane.get(0).z);
		vertices.get(19).setXYZ(farPlane.get(1).x, farPlane.get(1).y, farPlane.get(1).z);
		vertices.get(20).setXYZ(farPlane.get(1).x, farPlane.get(1).y, farPlane.get(1).z);
		vertices.get(21).setXYZ(farPlane.get(0).x, farPlane.get(0).y, farPlane.get(0).z);
		vertices.get(22).setXYZ(farPlane.get(3).x, farPlane.get(3).y, farPlane.get(3).z);
		vertices.get(23).setXYZ(farPlane.get(2).x, farPlane.get(2).y, farPlane.get(2).z);
		
		
		for (int i=0; i<6; i++) {
			for (int j=0; j<4; j++) {
				Vector3f normal = new Vector3f(normals.get(i)).negate();
				int index = i * 4;
				
				vertices.get(index).setABC(normal.x, normal.y, normal.z);
				vertices.get(index + 1).setABC(normal.x, normal.y, normal.z);
				vertices.get(index + 2).setABC(normal.x, normal.y, normal.z);
				vertices.get(index + 3).setABC(normal.x, normal.y, normal.z);
			}
		}
		
		updateBuffer();
	}

	@Override
	protected List<Integer> createAttributePointers() {
		glVertexAttribPointer(VERTEXPOSITION, GLVertex.positionElementCount, GL_FLOAT,
				false, GLVertex.stride, GLVertex.positionByteOffset);
		glVertexAttribPointer(NORMALPOSITION, GLVertex.normalElementCount, GL_FLOAT,
				false, GLVertex.stride, GLVertex.normalByteOffset);
		glVertexAttribPointer(COLOURPOSITION, GLVertex.colorElementCount, GL_FLOAT,
				false, GLVertex.stride, GLVertex.colorByteOffset);
		glVertexAttribPointer(TEXTUREPOSITION, GLVertex.textureElementCount, GL_FLOAT,
				false, GLVertex.stride, GLVertex.textureByteOffset);

		List<Integer> attribPointers = new ArrayList<Integer>();
		attribPointers.add(VERTEXPOSITION);
		attribPointers.add(NORMALPOSITION);
		attribPointers.add(COLOURPOSITION);
		attribPointers.add(TEXTUREPOSITION);
		return attribPointers;
	}

	@Override
	protected FloatBuffer createVertexBuffer() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(getVertices().size() * GLVertex.elementCount);
		for (var vertex : getVertices()) {
			buffer.put(vertex.getElements());
		}
		buffer.flip();
		return buffer;
	}

	@Override
	protected void createVertices() {
		for (int i=0; i<24; i++) {
			GLVertex vertex = new GLVertex();
			vertices.add(vertex);
		}
	}

	@Override
	protected void calculateVertices() {
		
	}

	@Override
	public boolean hasTexture() {
		return false;
	}

}
