package movement;

import movement.Shapes.OutlineShape;

public abstract class Collidable extends Entity{
	public Collidable() {
	}
	protected Collidable(Collidable collidable) {
		super(collidable);
	}
	
	public abstract void collision(Movable m, double[] collisionLocationInThis);				//for extra collisional effects.
	public abstract OutlineShape getOutline();
	
	@Override
	public void setRotationAxis(double[] rotationAxis) {
		super.setRotationAxis(rotationAxis);
		getOutline().setRotationAxis(rotationAxis);
	}
	@Override
	public void setAngle(double angle) {
		super.setAngle(angle);
		getOutline().setAngle(angle);
	}
	@Override
	public void setCentreOfRotation(double[] centreOfRotation) {
		super.setCentreOfRotation(centreOfRotation);
		getOutline().setCentreOfRotation(centreOfRotation);
	}
	@Override
	public abstract Collidable clone();
}
