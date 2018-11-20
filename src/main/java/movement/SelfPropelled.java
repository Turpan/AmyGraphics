package movement;

import movement.mathDS.Vector;
import movement.mathDS.Vector.MalformedVectorException;

public abstract class SelfPropelled extends Movable {
	private double baseMoveForce;

	public SelfPropelled() throws MalformedVectorException {
		super();
	}
	public double getBaseMoveForce() {
		return baseMoveForce;
	}
	public void setBaseMoveForce(double baseMoveForce) {
		if (baseMoveForce < 0) {
			baseMoveForce = 0;
		}
		this.baseMoveForce = baseMoveForce;
	}
	public void locomote(double[] direction) throws MalformedVectorException {
		applyForce(new Vector(baseMoveForce, direction));
	}

}