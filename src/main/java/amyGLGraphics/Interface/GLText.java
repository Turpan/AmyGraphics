package amyGLGraphics.Interface;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;

import amyGLGraphics.GLTexture2D;
import amyGLGraphics.GLTextureCache;
import amyGLGraphics.IO.GraphicsUtils;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLVertex;
import amyGraphics.Component;
import amyGraphics.Label;
import amyGraphics.Texture;

public class GLText extends GLObject {
	
	private Label label;
	
	int lastX;
	int lastY;
	int lastRight;
	int lastBottom;
	
	public GLText(Label label) {
		this.label = label;
		createVertices();
		super.setDrawOrder();
		calculateVertices();
		bindBuffer();
		createTexture();
		
		lastX = label.getX();
		lastY = label.getY();
		lastRight = label.getRight();
		lastBottom = label.getBottom();
	}

	private void createTexture() {
		GLTexture2D texture = GLTextureCache.getFontTexture(label.getFont().getSprite());
		
		addFontTexture(texture);
	}

	@Override
	protected int[] createDrawOrder() {
		List<Integer> drawList = new ArrayList<Integer>();
		
		for (int i=0; i<vertices.size(); i++) {
			int index0 = (i * 4);
			int index1 = ((i * 4) + 1);
			int index2 = ((i * 4) + 2);
			int index3 = ((i * 4) + 3);
			
			drawList.add(index0);
			drawList.add(index1);
			drawList.add(index2);
			drawList.add(index3);
			drawList.add(index2);
			drawList.add(index1);
		}
		
		int[] drawOrder = new int[drawList.size()];
		
		for (int i=0; i<drawList.size(); i++) {
			drawOrder[i] = drawList.get(i);
		}
		
		return drawOrder;
	}

	@Override
	public void update() {
		boolean shouldUpdate = false;
		
		if (hasMoved()) {
			calculateVertices();
			shouldUpdate = true;
		}
		
		if (shouldUpdate) {
			updateBuffer();
		}
	}

	@Override
	protected List<Integer> createAttributePointers() {
		int stride = GLVertex.positionBytesCount + GLVertex.textureByteCount;
		
		glVertexAttribPointer(VERTEXPOSITION, GLVertex.positionElementCount, GL_FLOAT, 
                false, stride, GLVertex.positionByteOffset);
        glVertexAttribPointer(VERTEXPOSITION+1, GLVertex.textureElementCount, GL_FLOAT, 
                false, stride, GLVertex.normalByteOffset);
        
        List<Integer> attribs = new ArrayList<Integer>();
        attribs.add(VERTEXPOSITION);
        attribs.add(VERTEXPOSITION+1);
        return attribs;
	}

	@Override
	protected FloatBuffer createVertexBuffer() {
		int elementCount = GLVertex.positionElementCount + GLVertex.textureElementCount;
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(getVertices().size() * elementCount);
		for (var vertex : getVertices()) {
			buffer.put(vertex.xyzw);
			buffer.put(vertex.st);
		}
		buffer.flip();
		return buffer;
	}

	@Override
	protected void createVertices() {
		int index=0;
		
		for (Component letter : label.getLetters()) {
			for (int i=0; i<4; i++) {
				var vertex = new GLVertex();
				
				vertices.add(vertex);
			}
			
			calculateTexture(letter, index);
			
			index+=4;
		}
	}

	private void calculateTexture(Component letter, int index) {
		if (shouldSnip(letter)) {
			snipTexture(letter, index);
			
			return;
		}
		
		Texture target = letter.getActiveTexture().getRenderTarget();
		
		float s = (float) calculatePercentage(target.getX(), target.getSprite().getWidth());
		float t = (float) calculatePercentage(target.getY(), target.getSprite().getHeight());
		float right = (float) calculatePercentage(target.getX() + target.getWidth(), target.getSprite().getWidth());
		float bottom = (float) calculatePercentage(target.getY() + target.getHeight(), target.getSprite().getHeight());
		
		vertices.get(index).setST(s, t);
		vertices.get(index+1).setST(right, t);
		vertices.get(index+2).setST(s, bottom);
		vertices.get(index+3).setST(right, bottom);
	}
	
	protected boolean shouldSnip(Component letter) {
		Component parent = label.getParent();
		
		if (parent == null) {
			return false;
		}
		
		return (letter.getX() < parent.getX() 
				|| letter.getY() < parent.getY()
				|| letter.getRight() > parent.getRight()
				|| letter.getBottom() > parent.getBottom());
	}
	
	protected void snipTexture(Component letter, int index) {
		Component parent = label.getParent();
		
		int offscreenx = parent.getX() - letter.getX();
		int offscreeny = parent.getY() - letter.getY();
		
		offscreenx = Math.max(0, offscreenx);
		offscreeny = Math.max(0, offscreeny);
		
		int offscreenwidth = letter.getRight() - parent.getRight();
		int offscreenheight = letter.getBottom() - parent.getBottom();
		
		offscreenwidth = Math.max(0, offscreenwidth);
		offscreenheight = Math.max(0, offscreenheight);
		
		double xfactor = calculatePercentage(letter.getX() - offscreenx, letter.getX());
		double yfactor = calculatePercentage(letter.getY() - offscreeny, letter.getY());
		
		double widthfactor = calculatePercentage(letter.getWidth() - offscreenwidth, letter.getWidth());
		double heightfactor = calculatePercentage(letter.getHeight() - offscreenheight, letter.getHeight());
		
		Texture target = letter.getActiveTexture().getRenderTarget();
		
		float s = (float) calculatePercentage(target.getX(), target.getSprite().getWidth());
		float t = (float) calculatePercentage(target.getY(), target.getSprite().getHeight());
		float right = (float) calculatePercentage(target.getX() + target.getWidth(), target.getSprite().getWidth());
		float bottom = (float) calculatePercentage(target.getY() + target.getHeight(), target.getSprite().getHeight());
		
		float s1 = (float) (((s - right) * xfactor) + right);
		float t1 = (float) (((t - bottom) * yfactor) + bottom);
		float right1 = (float) (((right - s) * widthfactor) + s);
		float bottom1 = (float) (((bottom - t) * heightfactor) + t);
		
		vertices.get(index).setST(s1, t1);
		vertices.get(index+1).setST(right1, t1);
		vertices.get(index+2).setST(s1, bottom1);
		vertices.get(index+3).setST(right1, bottom1);
	}
	
	protected boolean hasMoved() {
		boolean moved;
		
		moved = (lastX != label.getX()
				|| lastY != label.getY()
				|| lastRight != label.getRight()
				|| lastBottom != label.getBottom());
		
		lastX = label.getX();
		lastY = label.getY();
		lastRight = label.getRight();
		lastBottom = label.getBottom();
		
		return moved;
	}

	@Override
	protected void calculateVertices() {
		int index = 0;
		
		for (Component letter : label.getLetters()) {
			int x = letter.getX();
			int y = letter.getY();
			int right = letter.getRight();
			int bottom = letter.getBottom();
			
			if (label.getParent() != null) {
				x = Math.min(Math.max(x , label.getParent().getX()), label.getParent().getRight());
				y = Math.min(Math.max(y, label.getParent().getY()), label.getParent().getBottom());
				right = Math.max(Math.min(right, label.getParent().getRight()), label.getParent().getX());
				bottom = Math.max(Math.min(bottom, label.getParent().getBottom()), label.getParent().getY());
			}
			
			float fx = calculateRelativePosition(x, viewWidth);
			float fy = calculateRelativePosition(y, viewHeight);
			float fr = calculateRelativePosition(right, viewWidth);
			float fb = calculateRelativePosition(bottom, viewHeight);
			vertices.get(index).setXY(fx, flip(fy));
			vertices.get(index+1).setXY(fr, flip(fy));
			vertices.get(index+2).setXY(fx, flip(fb));
			vertices.get(index+3).setXY(fr, flip(fb));
			
			index+=4;
		}
	}
	
	public void addFontTexture(GLTexture2D texture) {
		this.addTexture(texture, GL13.GL_TEXTURE0);
	}
	
	public Vector4f getTextColour() {
		return GraphicsUtils.colourToVec4(label.getFontColour());
	}

}
