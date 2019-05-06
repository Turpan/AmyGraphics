package movement;

import java.util.ArrayList;
import java.util.List;

import amyGraphics.Texture;
import amyGraphics.TexturePosition;
import movement.mathDS.Vector;

public abstract class Entity {
	private double[] position;
	private double[] dimensions;
	protected double[] rotationAxis = {1,0,0};		//axis/angle bitchhhhh
	protected double[] centreOfRotation = {0,0,0};
	protected double angle = 0;							//just a set of default values. Not a big deal because angle is 0.
		
	private Texture activeTexture;
	private List<Texture> textures = new ArrayList<Texture>();
	private TexturePosition texturePosition = TexturePosition.FRONT;
	private boolean visible = true;

	public Entity() {
	}
	protected Entity(Entity entity) {
		setPosition(entity.getPosition().clone());
		setDimensions(entity.getDimensions().clone());
		setRotationAxis(entity.getRotationAxis().clone());
		setAngle(entity.getAngle());
		setCentreOfRotation(entity.getCentreOfRotation().clone());
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
		if (texture == null) {
			return;
		}
		
		textures.add(texture);
	}
	public void removeTexture(Texture texture) {
		textures.remove(texture);
	}
	public Texture getActiveTexture() {
		return activeTexture;
	}
	public TexturePosition getTexturePosition() {
		return texturePosition;
	}
	public void setTexturePosition(TexturePosition texturePosition) {
		this.texturePosition = texturePosition;
	}
	public void setActiveTexture(Texture activeTexture) {
		if (!textures.contains(activeTexture)) {
			activeTexture = null;
		}
		this.activeTexture = activeTexture;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
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
	public void setRotationAxis(double[] rotationAxis) {
		var tmp = rotationAxis.clone();		//don't want to be destructive of direction...
		if (tmp.length < 3) {	//don't need to check for tmp>DIM, as, due to the implementation, this doesn't actually matter.
			var tmp2 = new double[3];
			for(int i = 0; i< tmp.length; i++) {
				tmp2[i] = tmp[i];
			}
			tmp = tmp2;
		}
		double check = 0;
		for (int i = 0; i<3;i++) {
			check += tmp[i]*tmp[i];
		}
		if (check<0.999||check>1.001) {//technically, should equal 1, but slight rounding errors, working with irrational numbers converted to decimal.
			for (int i = 0; i<3;i++) {
				tmp[i] = tmp[i]/check;
			}
		}
		this.rotationAxis = tmp;
	}
	public double[] getRotationAxis() {
		return rotationAxis;
	}
	public double getAngle() {
		return angle;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}
	public double[] getCentreOfRotation() {
		return centreOfRotation;
	}
	public void setCentreOfRotation(double[] centreOfRotation) {
		this.centreOfRotation = centreOfRotation;
	}
	
	public abstract Entity clone();
	
	public void move(Vector movement) {
		double[] newPosition = new double[Vector.DIMENSIONS];
		double[] currentPosition = getPosition();
		double[] moveCmpnts = movement.getComponents();
		for (int i=0;i<Vector.DIMENSIONS;i++) {
			newPosition[i] = (currentPosition[i] + moveCmpnts[i]);
		}
		setPosition(newPosition);
	}
}
