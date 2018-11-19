package movement;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import amyGraphics.Texture;
import movement.Entity.MalformedEntityException;
import movement.mathDS.Vector;

public abstract class Entity {
	private float[] position;
	private double[]dimensions;
	private Texture activeTexture;
	private List<Texture> textures = new ArrayList<Texture>();

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
	public List<Texture> getTextures() {
		return textures;
	}
	public void addTexture(Texture texture) {
		textures.add(texture);
	}
	public void removeTexture(Texture texture) {
		textures.remove(texture);
	}
	public Texture getActiveTexture() {
		return activeTexture;
	}
	public void setActiveTexture(Texture activeTexture) {
		if (!textures.contains(activeTexture)) {
			activeTexture = null;
		}
		this.activeTexture = activeTexture;
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
