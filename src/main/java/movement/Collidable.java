package movement;

import movement.Shapes.OutlineShape;

//Collidables are entities that can exist as objects in the world that can be 
//hysically interacted with. They require a functioning outlineShape.

public abstract class Collidable extends Entity{
	public Collidable() {
	}
	protected Collidable(Collidable collidable) {
		super(collidable);
	}
	
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
