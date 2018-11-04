package amyGLGraphics.Interface;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;

import amyGLGraphics.GLTexture;
import amyGLGraphics.GLTexture2D;
import amyGLGraphics.GLTextureCache;
import amyGLGraphics.IO.GraphicsUtils;
import amyGLGraphics.base.GLGraphicsHandler;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLVertex;
import amyGraphics.Animation;
import amyGraphics.Texture;
import amyInterface.Component;

public class GLComponent extends GLObject{
	
	public static final int viewWidth = GLGraphicsHandler.interfaceWidth;
	public static final int viewHeight = GLGraphicsHandler.interfaceHeight;
	
	public static final int positionByteOffset = 0;
    public static final int colorByteOffset = positionByteOffset + GLVertex.positionBytesCount;
    public static final int textureByteOffset = colorByteOffset + GLVertex.colorByteCount;
	
	Component component;
	
	int lastX;
	int lastY;
	int lastRight;
	int lastBottom;
	
	public GLComponent(Component component) {
		this.component = component;
		super.setDrawOrder();
		createVertices();
		calculateVertices();
		updateTexture();
		colourVertices();
		bindBuffer();
		createTexture();
		
		lastX = component.getX();
		lastY = component.getY();
		lastRight = component.getRight();
		lastBottom = component.getBottom();
	}

	@Override
	protected void calculateVertices() {
		int x = component.getX();
		int y = component.getY();
		int right = component.getRight();
		int bottom = component.getBottom();
		
		if (component.getParent() != null) {
			x = Math.min(Math.max(x , component.getParent().getX()), component.getParent().getRight());
			y = Math.min(Math.max(y, component.getParent().getY()), component.getParent().getBottom());
			right = Math.max(Math.min(right, component.getParent().getRight()), component.getParent().getX());
			bottom = Math.max(Math.min(bottom, component.getParent().getBottom()), component.getParent().getY());
		}
		
		float fx = calculateRelativePosition(x, viewWidth);
		float fy = calculateRelativePosition(y, viewHeight);
		float fr = calculateRelativePosition(right, viewWidth);
		float fb = calculateRelativePosition(bottom, viewHeight);
		vertices.get(0).setXY(fx, flip(fy));
		vertices.get(1).setXY(fr, flip(fy));
		vertices.get(2).setXY(fx, flip(fb));
		vertices.get(3).setXY(fr, flip(fb));
	}
	
	public Component getComponent() {
		return component;
	}
	@Override
	protected int[] createDrawOrder() {
		return new int[] {
				0,1,2,
				3,2,1,
		};
	}
	@Override
	protected void createVertices() {
		for (int i=0; i<4; i++) {
			var vertex = new GLVertex();
			vertex.setST(texturecoords[i*2], texturecoords[(i*2) + 1]);
			
			vertices.add(vertex);
		}
		
		if (shouldSnip()) {
			snipTexture();
		}
	}
	@Override
	public void update() {
		boolean shouldUpdate = false;
		
		if (hasMoved()) {
			calculateVertices();
			shouldUpdate = true;
		}
		
		if (textureChanged()) {
			changeTexture();
			shouldUpdate = true;
		}
		
		if (isAnimated()) {
			updateTexture();
			shouldUpdate = true;
		}
		
		if (colourChanged()) {
			updateColour();
			shouldUpdate = true;
		}
		
		if (shouldUpdate) {
			updateBuffer();
		}
	}
	
	protected boolean isAnimated() {
		return (component.getActiveTexture() instanceof Animation);
	}
	
	protected void updateTexture() {
		if (!hasTexture()) {
			return;
		}
		
		if (shouldSnip()) {
			snipTexture();
			
			return;
		}
		
		Texture target = component.getActiveTexture().getRenderTarget();
		
		float s = (float) calculatePercentage(target.getX(), target.getSprite().getWidth());
		float t = (float) calculatePercentage(target.getY(), target.getSprite().getHeight());
		float right = (float) calculatePercentage(target.getX() + target.getWidth(), target.getSprite().getWidth());
		float bottom = (float) calculatePercentage(target.getY() + target.getHeight(), target.getSprite().getHeight());
		
		vertices.get(0).setST(s, t);
		vertices.get(1).setST(right, t);
		vertices.get(2).setST(s, bottom);
		vertices.get(3).setST(right, bottom);
	}
	
	protected boolean colourChanged() {
		Vector4f currentColour = vertices.get(0).rgbaVector();
		
		Vector4f targetColour = GraphicsUtils.colourToVec4(component.getColour());
		
		return !currentColour.equals(targetColour);
	}
	
	protected void updateColour() {
		Vector4f targetColour = GraphicsUtils.colourToVec4(component.getColour());
		
		for (GLVertex vertex : vertices) {
			vertex.setRGBA(targetColour.x, targetColour.y, targetColour.z, targetColour.w);
		}
	}
	
	protected boolean textureChanged() {
		if (!hasTexture()) {
			return false;
		}
		
		GLTexture2D gltexture = GLTextureCache.getInterfaceTexture(component.getActiveTexture().getSprite());
		
		return !getTextures().containsKey(gltexture);
	}
	
	protected void changeTexture() {
		GLTexture2D gltexture = GLTextureCache.getInterfaceTexture(component.getActiveTexture().getSprite());
		
		getTextures().clear();
		
		addInterfaceTexture(gltexture);
		
		updateTexture();
	}
	
	@Override
	public boolean hasTexture() {
		return (component.getActiveTexture() != null);
	}
	
	public void addInterfaceTexture(GLTexture2D texture) {
		this.addTexture(texture, GL13.GL_TEXTURE0);
	}
	
	protected void createTexture() {
		if (!hasTexture()) {
			return;
		}
		
		GLTexture2D texture = GLTextureCache.getInterfaceTexture(component.getActiveTexture().getSprite());
		
		addInterfaceTexture(texture);
	}
	
	protected void colourVertices() {
		for (GLVertex vertex : vertices) {
			Vector4f colour = GraphicsUtils.colourToVec4(component.getColour());
			
			vertex.setRGBA(colour.x, colour.y, colour.z, colour.w);
		}
	}
	
	protected boolean hasMoved() {
		boolean moved;
		
		moved = (lastX != component.getX()
				|| lastY != component.getY()
				|| lastRight != component.getRight()
				|| lastBottom != component.getBottom());
		
		lastX = component.getX();
		lastY = component.getY();
		lastRight = component.getRight();
		lastBottom = component.getBottom();
		
		return moved;
	}
	
	protected boolean shouldSnip() {
		Component parent = component.getParent();
		
		if (parent == null) {
			return false;
		}
		
		if (component.getActiveTexture() == null) {
			return false;
		}
		
		return (component.getX() < parent.getX() 
				|| component.getY() < parent.getY()
				|| component.getRight() > parent.getRight()
				|| component.getBottom() > parent.getBottom());
	}
	
	protected void snipTexture() {
		Component parent = component.getParent();
		
		int offscreenx = parent.getX() - component.getX();
		int offscreeny = parent.getY() - component.getY();
		
		offscreenx = Math.max(0, offscreenx);
		offscreeny = Math.max(0, offscreeny);
		
		int offscreenwidth = component.getRight() - parent.getRight();
		int offscreenheight = component.getBottom() - parent.getBottom();
		
		offscreenwidth = Math.max(0, offscreenwidth);
		offscreenheight = Math.max(0, offscreenheight);
		
		double xfactor = calculatePercentage(component.getX() - offscreenx, component.getX());
		double yfactor = calculatePercentage(component.getY() - offscreeny, component.getY());
		
		double widthfactor = calculatePercentage(component.getWidth() - offscreenwidth, component.getWidth());
		double heightfactor = calculatePercentage(component.getHeight() - offscreenheight, component.getHeight());
		
		Texture target = component.getActiveTexture().getRenderTarget();
		
		float s = (float) calculatePercentage(target.getX(), target.getSprite().getWidth());
		float t = (float) calculatePercentage(target.getY(), target.getSprite().getHeight());
		float right = (float) calculatePercentage(target.getX() + target.getWidth(), target.getSprite().getWidth());
		float bottom = (float) calculatePercentage(target.getY() + target.getHeight(), target.getSprite().getHeight());
		
		float s1 = (float) (((s - right) * xfactor) + right);
		float t1 = (float) (((t - bottom) * yfactor) + bottom);
		float right1 = (float) (((right - s) * widthfactor) + s);
		float bottom1 = (float) (((bottom - t) * heightfactor) + t);
		
		vertices.get(0).setST(s1, t1);
		vertices.get(1).setST(right1, t1);
		vertices.get(2).setST(s1, bottom1);
		vertices.get(3).setST(right1, bottom1);
	}
	
	public boolean shouldRender() {
		if (!component.isVisible()) {
			return false;
		}
		
		Component parent = component.getParent();
		
		if (parent == null) {
			return true;
		}
		
		return (component.getX() < parent.getRight() && component.getY() < parent.getBottom());
	}
	
	@Override
	protected List<Integer> createAttributePointers() {
		int stride = GLVertex.positionBytesCount + GLVertex.colorByteCount + GLVertex.textureByteCount;
		
		glVertexAttribPointer(VERTEXPOSITION, GLVertex.positionElementCount, GL_FLOAT, 
                false, stride, positionByteOffset);
        glVertexAttribPointer(COLOURPOSITION-1, GLVertex.colorElementCount, GL_FLOAT, 
                false, stride, colorByteOffset);
        glVertexAttribPointer(TEXTUREPOSITION-1, GLVertex.textureElementCount, GL_FLOAT, 
                false, stride, textureByteOffset);
        
        List<Integer> attribs = new ArrayList<Integer>();
        attribs.add(VERTEXPOSITION);
        attribs.add(COLOURPOSITION-1);
        attribs.add(TEXTUREPOSITION-1);
        return attribs;
	}
	@Override
	protected FloatBuffer createVertexBuffer() {
		int elementCount = GLVertex.positionElementCount + GLVertex.colorElementCount + GLVertex.textureElementCount;
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(getVertices().size() * elementCount);
		for (var vertex : getVertices()) {
			buffer.put(vertex.xyzw);
			buffer.put(vertex.rgba);
			buffer.put(vertex.st);
		}
		buffer.flip();
		return buffer;
	}
}
