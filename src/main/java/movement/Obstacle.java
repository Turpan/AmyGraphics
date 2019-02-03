package movement;

import movement.Shapes.OutlineShape;

public abstract class Obstacle extends Collidable{
	//Collidable objects that don't move
	private double CoR;
	private OutlineShape outlineShape;
	
	public Obstacle() {
	}
	protected Obstacle(Obstacle obstacle) {
		super(obstacle);
		setCoR(obstacle.getCoR());
	}

	public double getCoR() {
		return CoR;
	}
	public void setCoR(double CoR) {
		this.CoR = CoR;
	}
	public OutlineShape getOutline() {
		return outlineShape;
		
	}
	public void setOutline(OutlineShape outline) {
		this.outlineShape = outline;
	}
	
	@Override
	public abstract Obstacle clone();
}
