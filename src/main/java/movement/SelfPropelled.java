package movement;

import movement.mathDS.Vector;

public abstract class SelfPropelled extends Movable {
	//SelfPropelleds can move themselves. They can generate a force in a direction, if they so
	//please. They require a baseMoveForce.
	
	//Examples of SelfPropelleds that don't inherit this classes children include:
	//simple creatures that move in complex ways, but aren't fully fleshed characters. Say, 
	//a wisp that follows you.
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