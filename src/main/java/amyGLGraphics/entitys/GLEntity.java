package amyGLGraphics.entitys;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import amyGLGraphics.GLTexture;
import amyGLGraphics.GLTextureColour;
import amyGLGraphics.GLTextureDepth;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLVertex;
import amyGraphics.Animation;
import amyGraphics.Texture;
import movement.Entity;

public class GLEntity extends GLObject{
	
	private Entity entity;
	private boolean is3d;
	
	public GLEntity(Entity entity) {
		super();
		this.entity = entity;
		determine3D();
		super.setDrawOrder();
		createVertices();
		calculateVertices();
		updateTexture();
		colourVertices();
		setNormals();
		bindBuffer();
	}
	/*@Override
	public BufferedImage getSprite() {
		return entity.getTexture().getSprite();
	}*/
	@Override
	protected void calculateVertices() {
		if (is3D()) {
			calculate3DVertices();
		} else {
			calculate2DVertices();
		}
		//TODO factor in camera position
	}
	
	private void calculate3DVertices() {
		int right = (int) (entity.getDimensions()[0]);
		int top = (int) (entity.getDimensions()[1]);
		int back = (int) (entity.getDimensions()[2]);
		float fx = calculateRelativePosition(0, viewWidth);
		float fy = calculateRelativePosition(0, viewHeight);
		float fz = flip(calculateRelativePosition(0, viewDepth));
		float fr = calculateRelativePosition(right, viewWidth);
		float ft = calculateRelativePosition(top, viewHeight);
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
		update();
	}
	
	private void colourVertices() {
		for (int i=0; i<6; i++) {
			int index = i * 4;
			vertices.get(index).setRGB(1, 0, 0);
			vertices.get(index + 1).setRGB(0, 1, 0);
			vertices.get(index + 2).setRGB(0, 0, 1);
			vertices.get(index + 3).setRGB(1, 1, 0);
		}
	}
	
	private void setNormals() {
		for (int i=0; i<vertices.size()/4; i++) {
			setNormal(i*4);
		}
	}
	
	private void setNormal(int pos) {
		Vector3f normal;
		
		Vector3f pointA = new Vector3f();
		Vector3f pointB = new Vector3f();
		Vector3f pointC = new Vector3f();
		
		float[] el1 = vertices.get(pos).getElements();
		float[] el2 = vertices.get(pos+1).getElements();
		float[] el3 = vertices.get(pos+2).getElements();
		
		pointA.set(el1[0], el1[1], el1[2]);
		pointB.set(el2[0], el2[1], el2[2]);
		pointC.set(el3[0], el3[1], el3[2]);
		
		normal = calculateSurfaceNormal(pointA, pointB, pointC);
		
		for (int i=pos; i<pos+4; i++) {
			vertices.get(i).setABC(normal.x, normal.y, normal.z);
		}
	}
	
	public void universalColour(Vector3f color) {
		for (var vertex : vertices) {
			vertex.setRGBA(color.x, color.y, color.z, 1.0f);
		}
		updateBuffer();
	}
	
	private void calculate2DVertices() {
		int right = (int) (entity.getDimensions()[0]);
		int bottom = (int) (entity.getDimensions()[1]);
		float fx = calculateRelativePosition(0, viewWidth);
		float fy = calculateRelativePosition(0, viewHeight);
		float fr = calculateRelativePosition(right, viewWidth);
		float fb = calculateRelativePosition(bottom, viewHeight);
		vertices.get(0).setXY(fx, flip(fy));
		vertices.get(1).setXY(fr, flip(fy));
		vertices.get(2).setXY(fr, flip(fb));
		vertices.get(3).setXY(fx, flip(fb));
	}
	
	@Override
	protected void createVertices() {
		for (int i=0; i<4; i++) {
			var vertex = new GLVertex();
			vertex.setST(texturecoords[i*2], texturecoords[(i*2) + 1]);
			vertices.add(vertex);
		}
		if (is3D()) {
			create3DVertices();
		}
	}
	
	protected void updateTexture() {
		if (!hasTexture()) {
			return;
		}
		
		Texture target = entity.getTexture().getRenderTarget();
		
		float s = (float) calculatePercentage(target.getX(), target.getSprite().getWidth());
		float t = (float) calculatePercentage(target.getY(), target.getSprite().getHeight());
		float right = (float) calculatePercentage(target.getX() + target.getWidth(), target.getSprite().getWidth());
		float bottom = (float) calculatePercentage(target.getY() + target.getHeight(), target.getSprite().getHeight());
		
		vertices.get(0).setST(s, t);
		vertices.get(1).setST(right, t);
		vertices.get(2).setST(s, bottom);
		vertices.get(3).setST(right, bottom);
		
		updateBuffer();
	}
	
	private void create3DVertices() {
		/*for (int i=0; i<20; i++) {
			var vertex = new GLVertex();
			vertex.setST(-2f, -2f);
			vertices.add(vertex);
		}*/
		for (int j=0; j<5; j++) {
			for (int i=0; i<4; i++) {
				var vertex = new GLVertex();
				//vertex.setST(texturecoords[i*2], texturecoords[(i*2) + 1]);
				vertices.add(vertex);
			}
		}
	}
	
	private void determine3D() {
		set3D(entity.getDimensions().length == 3);
	}
	
	@Override
	protected int[] createDrawOrder() {
		if (is3D()) {
			return create3dDrawOrder();
		} else {
			return create2dDrawOrder();
		}
	}
	
	private int[] create2dDrawOrder() {
		return new int[] {
				0, 1, 2,
	            3, 2, 1
		};
	}
	
	private int[] create3dDrawOrder() {
		return new int[] {
				0,1,2, 3,2,1,           //Face front
                4,5,6, 7,6,5,           //Face right
                8,9,10, 11,10,9,        //Face left
                12,13,14, 15,14,13,     //Face bottom
                16,17,18, 19,18,17,     //Face top
                20,21,22, 23,22,21,	    //Face back
		};
	}
	
	protected boolean is3D() {
		return is3d;
	}
	
	private void set3D(boolean is3d) {
		this.is3d = is3d;
	}
	@Override
	public void update() {
		updateModelMatrix();
		if (isAnimated()) {
			updateTexture();
		}
	}
	
	private void updateModelMatrix() {
		if (is3D()) {
			updateModelMatrix3D();
		} else {
			updateModelMatrix2D();
		}
	}
	
	private void updateModelMatrix3D() {
		int x = (int) entity.getPosition()[0];
		int y = (int) entity.getPosition()[1];
		int z = (int) entity.getPosition()[2];
		float fx = calculateTranslation(x, viewWidth);
		float fy = calculateTranslation(y, viewHeight);
		float fz = flip(calculateTranslation(z, viewDepth));
		modelMatrix = new Matrix4f().translate(fx, fy, fz);
	}
	
	private void updateModelMatrix2D() {
		int x = (int) entity.getPosition()[0];
		int y = (int) entity.getPosition()[1];
		float fx = calculateTranslation(x, viewWidth);
		float fy = calculateTranslation(y, viewHeight);
		modelMatrix = new Matrix4f().translate(fx, fy, 0);
	}
	
	protected boolean isAnimated() {
		return (entity.getTexture() instanceof Animation);
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
	public void setDiffuseTexture(GLTexture texture) {
		super.setDiffuseTexture(texture);
		addTexture(texture, GL_TEXTURE0 + GLEntityProgram.diffuseTextureUnit);
	}
	
	public void setDirShadowMap(GLTextureColour glTextureColour) {
		addTexture(glTextureColour, GL_TEXTURE0 + GLEntityProgram.dirDepthTextureUnit);
	}
	@Override
	public boolean hasTexture() {
		return (entity.getTexture() != null);
	}
}
