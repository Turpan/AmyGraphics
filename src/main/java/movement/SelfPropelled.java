package movement;

import movement.Vectors.Force;
import movement.Vectors.Vector.MalformedVectorException;

public abstract class SelfPropelled extends Moveable {
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
		applyForce(new Force(baseMoveForce, direction));
	}

}