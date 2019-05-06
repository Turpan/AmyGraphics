package movement;

import movement.Shapes.OutlineShape;

public abstract class Obstacle extends Collidable{
	//Collidable objects that don't move. They don't need a number of the things that Movables
	//have. Generally used for things like floors and walls. Can have bigShapes as their outlines
	// because I'm not super fussed about exactly where on the wall a movable hits, as it's a 
	//homogeneous object.
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
	public  abstract void collision(Movable m);
	
	@Override
	public abstract Obstacle clone();
}
