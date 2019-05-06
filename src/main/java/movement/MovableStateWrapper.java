package movement;

public class MovableStateWrapper{
	//contains a boolean ghost, and a double[] relative position. Just makes it a little bit neater?
	boolean ghost;
	double[] relativePosition;
	boolean active;
	
	public MovableStateWrapper(double[] relativePosition, boolean ghost, boolean active) {
		this.ghost = ghost;
		this.relativePosition = relativePosition;
		this.active = active;
	}
	
	public MovableStateWrapper clone() {
		return new MovableStateWrapper(relativePosition.clone(), ghost, active);
		
	}
}