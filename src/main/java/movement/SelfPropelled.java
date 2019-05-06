package movement;

import movement.mathDS.Vector;

public abstract class SelfPropelled extends Movable {
	private double baseMoveForce;

	public SelfPropelled() {
	}	
	protected SelfPropelled(SelfPropelled selfPropelled) {
		super(selfPropelled);
		setBaseMoveForce(selfPropelled.getBaseMoveForce());
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
	public void locomote(double[] direction) {
		applyForce(new Vector(baseMoveForce, direction));
	}
	@Override
	public abstract SelfPropelled clone();
}