package movement;

import java.awt.image.BufferedImage;

import amyGraphics.Texture;

//import movement.Shapes.OutlineShape;

public abstract class Entity {
	double[] position;
	double[] dimensions;
	Texture texture;
	//OutlineShape outline;	//determines collisions. Technically, doesn't at all map to the sprite

	public void setDimensions(double[] dimensions) {
		for (double dim : dimensions) {
			if (dim <0) {dim = 0;}
		}
		this.dimensions= dimensions;
	}
	public double[] getDimensions() {
		return dimensions;
	}
	public Texture getTexture() {
		return texture;
	}
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	public void setPosition(double[] position) {
		this.position = position;
	}
	public double[] getPosition() {
		return position;
	}
	/*public void setOutline(OutlineShape outline) {
		this.outline = outline;
	}
	public OutlineShape getOutline() {
		return outline;
	}*/
	
	
	
	public class MalformedEntityException extends Exception {
		private static final long serialVersionUID = 1L;

		public MalformedEntityException (String message) {
	        super (message);
	    }
	}
}
