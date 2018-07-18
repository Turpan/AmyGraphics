package amyGLInterface;

import java.awt.image.BufferedImage;

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
	@Override
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
}
