package movement;


public abstract class Obstacle extends Collidable{
	//Collidable objects that don't move
	private double CoR;
	
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
	@Override
	public abstract Obstacle clone();
}
