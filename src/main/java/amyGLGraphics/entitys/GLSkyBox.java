package amyGLGraphics.entitys;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

import amyGLGraphics.GLTextureCube;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLVertex;

public class GLSkyBox extends GLObject {
	
	public GLSkyBox() {
		super();
		super.setDrawOrder();
		createVertices();
		calculateVertices();
		bindBuffer();
	}

	@Override
	protected int[] createDrawOrder() {
		return new int[] {
				0,1,2, 3,2,1,           //Face front
                4,5,6, 7,6,5,           //Face right
                8,9,10, 11,10,9,        //Face left
                12,13,14, 15,14,13,     //Face bottom
                16,17,18, 19,18,17,     //Face top
                20,21,22, 23,22,21,	    //Face back
		};
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
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
		int right = (int) (viewWidth);
		int bottom = (int) (viewHeight);
		int back = (int) (viewDepth);
		float fx = calculateRelativePosition(0, viewWidth);
		float fy = calculateRelativePosition(0, viewHeight);
		float fz = flip(calculateRelativePosition(0, viewDepth));
		float fr = calculateRelativePosition(right, viewWidth);
		float ft = calculateRelativePosition(bottom, viewHeight);
		float fba = flip(calculateRelativePosition(back, viewDepth));
		vertices.get(0).setXYZ(fx, ft, fz);
		vertices.get(1).setXYZ(fr, ft, fz);
		vertices.get(2).setXYZ(fx, fy, fz);
		vertices.get(3).setXYZ(fr, fy, fz);
		vertices.get(4).setXYZ(fr, ft, fz);
		vertices.get(5).setXYZ(fr, ft, fba);
		vertices.get(6).setXYZ(fr, fy, fz);
		vertices.get(7).setXYZ(fr, fy, fba);
		vertices.get(8).setXYZ(fx, ft, fba);
		vertices.get(9).setXYZ(fx, ft, fz);
		vertices.get(10).setXYZ(fx, fy, fba);
		vertices.get(11).setXYZ(fx, fy, fz);
		vertices.get(12).setXYZ(fx, fy, fz);
		vertices.get(13).setXYZ(fr, fy, fz);
		vertices.get(14).setXYZ(fx, fy, fba);
		vertices.get(15).setXYZ(fr, fy, fba);
		vertices.get(16).setXYZ(fr, ft, fz);
		vertices.get(17).setXYZ(fx, ft, fz);
		vertices.get(18).setXYZ(fr, ft, fba);
		vertices.get(19).setXYZ(fx, ft, fba);
		vertices.get(20).setXYZ(fr, ft, fba);
		vertices.get(21).setXYZ(fx, ft, fba);
		vertices.get(22).setXYZ(fr, fy, fba);
		vertices.get(23).setXYZ(fx, fy, fba);
	}
	
	public void setSkyBox(GLTextureCube texture) {
		addTexture(texture, GL_TEXTURE0);
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

}
