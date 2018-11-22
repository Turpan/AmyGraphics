package movement;

import java.util.ArrayList;
import java.util.List;

import amyGraphics.Texture;
import movement.mathDS.Vector;

public abstract class Entity {
	private double[] position;
	private double[]dimensions;
	private Texture activeTexture;
	private List<Texture> textures = new ArrayList<Texture>();

	public Entity() {
	}
	protected Entity(Entity entity) {
		setPosition(entity.getPosition().clone());
		setDimensions(entity.getDimensions().clone());
		for (Texture t : entity.getTextures()) {
			addTexture(t);
		}
		setActiveTexture(entity.getActiveTexture());
	}
	
	public void setDimensions(double[] dimensions) {
		for (double dim : dimensions) {
			if (dim <0) {dim = 0;}
		}
		if (dimensions.length != Vector.DIMENSIONS) {
			this.dimensions = new double[Vector.DIMENSIONS];
			for (int i = 0; i<dimensions.length && i<Vector.DIMENSIONS; i++) {
				this.dimensions[i] = dimensions[i];
			}
		}else {
		this.dimensions= dimensions;
		}
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
	public void setPosition(double[] position) {
		if (position.length != Vector.DIMENSIONS) {
			this.position = new double[Vector.DIMENSIONS];
			for (int i = 0; i<position.length && i<Vector.DIMENSIONS; i++) {
				this.position[i] = position[i];
			}
		}else {
			this.position = position;
		}
	}
	public double[] getPosition() {
		return position;
	}
	public abstract Entity clone();
	public void move(Vector movement) {
		double[] newPosition = new double[Vector.DIMENSIONS];
		double[] currentPosition = getPosition();
		double[] moveCmpnts = movement.getComponents();
		for (int i=0;i<Vector.DIMENSIONS;i++) {
			newPosition[i] = (currentPosition[i] + moveCmpnts[i]);
		}
	}
}
