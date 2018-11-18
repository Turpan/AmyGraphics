package movement;

import java.awt.image.BufferedImage;

import amyGraphics.Texture;
import movement.Entity.MalformedEntityException;
import movement.mathDS.Vector;

public abstract class Entity {
	private float[] position;
	private double[]dimensions;
	private Texture texture;

	public void setDimensions(double[] dimensions) throws MalformedEntityException {
		if (dimensions.length != Vector.DIMENSIONS) {
			throw new MalformedEntityException("attempt to set dimensions to wrong number of dimensions");
		}
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
	public void setPosition(float[] position) throws MalformedEntityException {
		if (position.length != Vector.DIMENSIONS) {
			throw new MalformedEntityException("attempt to set position to wrong number of dimensions");
		}
		this.position = position;
	}
	public float[] getPosition() {
		return position;
	}
	public void move(Vector movement) {
		float[] newPosition = new float[Vector.DIMENSIONS];
		float[] currentPosition = getPosition();
		double[] moveCmpnts = movement.getComponents();
		for (int i=0;i<Vector.DIMENSIONS;i++) {
			newPosition[i] = (float) (currentPosition[i] + moveCmpnts[i]);
		}
		try {
			setPosition(newPosition);
		} catch (MalformedEntityException e) {
			e.printStackTrace();
		}
		
	}
	public class MalformedEntityException extends Exception {
		private static final long serialVersionUID = 1L;

		public MalformedEntityException (String message) {
	        super (message);
	    }
	}
}
