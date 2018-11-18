package movement;

import movement.Shapes.OutlineShape;

public abstract class Obstacle extends Collidable{
	//Collidable objects that don't move
	private double CoR;
	private OutlineShape outline;
	
	public double getCoR() {
		return CoR;
	}
	public void setCoR(double CoR) {
		this.CoR = CoR;
	}
	public void setOutline(OutlineShape outline) {
		this.outline = outline;
	}
	public OutlineShape getOutline() {
		return outline;
	}
}
