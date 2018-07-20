package amyGLInterface;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import amyGLGraphics.GLObject;
import amyGLGraphics.GLVertex;
import amyGraphics.Component;

public class GLComponent extends GLObject{
	Component component;
	
	public GLComponent(Component component) {
		this.component = component;
		super.setDrawOrder();
		createVertices();
		calculateVertices();
		bindBuffer();
	}
	public BufferedImage getSprite() {
		return component.getBackground();
	}
	@Override
	protected void calculateVertices() {
		int x = (int) component.getX();
		int y = (int) component.getY();
		int right = (int) component.getX() + component.getWidth();
		int bottom = (int) component.getY() + component.getHeight();
		float fx = calculateRelativePosition(x, viewWidth);
		float fy = calculateRelativePosition(y, viewHeight);
		float fr = calculateRelativePosition(right, viewWidth);
		float fb = calculateRelativePosition(bottom, viewHeight);
		vertices.get(0).setXY(fx, flip(fy));
		vertices.get(1).setXY(fr, flip(fy));
		vertices.get(2).setXY(fr, flip(fb));
		vertices.get(3).setXY(fx, flip(fb));
		//TODO factor in camera position
	}
	
	public Component getComponent() {
		return component;
	}
	@Override
	protected byte[] createDrawOrder() {
		return new byte[] {
				0, 1, 2,
	            2, 3, 0
		};
	}
	@Override
	protected void createVertices() {
		for (int i=0; i<4; i++) {
			var vertex = new GLVertex();
			vertices.add(vertex);
		}
	}
	@Override
	public void update() {
		updateBuffer();
	}
	@Override
	protected void createAttributePointers() {
		int stride = GLVertex.positionBytesCount + GLVertex.colorByteCount + GLVertex.textureByteCount;
		
		glVertexAttribPointer(VERTEXPOSITION, GLVertex.positionElementCount, GL_FLOAT, 
                false, stride, GLVertex.positionByteOffset);
        glVertexAttribPointer(COLOURPOSITION, GLVertex.colorElementCount, GL_FLOAT, 
                false, stride, GLVertex.colorByteOffset - GLVertex.normalByteOffset);
        glVertexAttribPointer(TEXTUREPOSITION, GLVertex.textureElementCount, GL_FLOAT, 
                false, stride, GLVertex.textureByteOffset - GLVertex.normalByteOffset);
	}
	@Override
	protected FloatBuffer createVertexBuffer() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(getVertices().size() * GLVertex.elementCount);
		for (var vertex : getVertices()) {
			buffer.put(vertex.xyzw);
			buffer.put(vertex.rgba);
			buffer.put(vertex.st);
		}
		buffer.flip();
		return buffer;
	}
}
