package movement;

public class MovableStateWrapper{
	//contains a boolean ghost, and a double[] relative position. Just makes it a little bit neater?
	boolean ghost;
	double[] relativePosition;
	
	public MovableStateWrapper(double[] relativePosition, boolean ghost) {
		this.ghost = ghost;
		this.relativePosition = relativePosition;
	}
	
	public MovableStateWrapper clone() {
		return new MovableStateWrapper(relativePosition.clone(), ghost);
		
	}
}