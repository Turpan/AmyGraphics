package movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import movement.Shapes.OutlineShape;
import movement.Shapes.PolyShape;
import movement.Shapes.StandardShape;
import movement.mathDS.Vector;

public abstract class Movable extends Collidable{

	public final static double  TIMESCALE = 0.1;
	
	private final static double GRAVITY = 9.81;
	private final static int FLOORWINDOW = 5;
	
	private Vector velocity = new Vector();
	private Vector activeForce = new Vector();
	private double mass; 						// don't let this one equal 0.... If you want a default value, go with 1.
	private double coefficientOfDrag;
	private double coefficientOfFriction;
	private List<Map<StandardShape, MovableStateWrapper>> states;
	//oh dear god this is a mess. the index of the Map is the state number. Each Map represents one state. The shapes in the Map are the components of the polyShapes that, ultimately, make up that shapes Outline. (unless there's just one shape in a map)
	//The boolean in the wrapper is the ghost boolean. This represents /not/ being teleported out of other objects.
	private List<StandardShape> shapes;	//the different shapes in each of the states
	private List<StandardShape> corporealShapes; //same as regular shapes, but ignores ghosts.
	
	private double coefficientOfRestitution; 	//Because CoR is kinda a terrible measure, in a collision, this value is averaged with the enemies because physics doesn't actually have any more direct concept of the 'bounciness' of an object in isolation
	private int onFloorTimer = 0;					//Represents an object being on the floor. Or rather, having recently collided with a Floor object.

	private int state = 0;			//for when movables want to swap between a few things.
	
	
	public Movable() {
	}
	protected Movable(Movable movable) {
		super(movable);
		setVelocity(movable.getVelocity().clone());
		setActiveForce(movable.getActiveForce().clone());
		setMass(movable.getMass());
		setCoD(movable.getCoD());
		setCoR(movable.getCoR());
		setCoF(movable.getCoF());
		
		List<Map<StandardShape, MovableStateWrapper>> clonedStates = new ArrayList<Map<StandardShape, MovableStateWrapper>>();
		Map<StandardShape, MovableStateWrapper> stateToAdd;
		Iterator<StandardShape> it;
		StandardShape outline;
		for (Map<StandardShape, MovableStateWrapper> state : movable.getStates()) {
			stateToAdd = new HashMap<StandardShape, MovableStateWrapper>();
			it = state.keySet().iterator();
			while (it.hasNext()) {
				outline = it.next();
				stateToAdd.put(outline.clone(), state.get(outline).clone());
			}			
			clonedStates.add(stateToAdd);
		}
		setStates(clonedStates);
		setState(movable.getState());
	}	
	public void setStates(List<Map<StandardShape, MovableStateWrapper>> states) {
		//I'm currently assuming that every state contains at least one corporeal shape
		
		this.states = states;
		HashMap<StandardShape, double[]> currentStateShapes = new HashMap<StandardShape, double[]>();
		HashMap<StandardShape, double[]> currentStateCorporealShapes = new HashMap<StandardShape, double[]>();
		
		List<StandardShape> finalShapes = new ArrayList<StandardShape>();
		List<StandardShape> finalCorporealShapes = new ArrayList<StandardShape>();
		
		StandardShape shape;
		StandardShape totalStateShape;
		StandardShape totalStateCorporealShape;
		
		double[] dimensions = new double[Vector.DIMENSIONS];
		for (int i = 0; i< states.size(); i++) {
			var it = states.get(i).keySet().iterator();
			if (states.get(i).keySet().size()==1) {
				totalStateShape = it.next();
				totalStateCorporealShape = totalStateShape;
			} else {
				while (it.hasNext()) {
					shape = it.next();
					currentStateShapes.put(shape, states.get(i).get(shape).relativePosition);
					if (!states.get(i).get(shape).ghost) {
						currentStateCorporealShapes.put(shape, states.get(i).get(shape).relativePosition);	
					}
				}
				totalStateShape = new PolyShape(currentStateShapes);
				totalStateCorporealShape = new PolyShape(currentStateCorporealShapes);
			}
			for (int j = 0; j < Vector.DIMENSIONS ; j++) {
				if (totalStateShape.getDimensions()[j] > dimensions[j]) {
					dimensions[j] = totalStateShape.getDimensions()[j];
				}
			} 
			finalShapes.add(i,totalStateShape);
			finalCorporealShapes.add(i, totalStateCorporealShape);
		}
		setShapes(finalShapes);
		setCorporealShapes(finalCorporealShapes);
		setDimensions(dimensions);
	}
	private List<Map<StandardShape, MovableStateWrapper>> getStates() {
		return states;
	}	
	public void setSimpleOutline(StandardShape outline) {
		var states = new ArrayList<Map<StandardShape, MovableStateWrapper>>();
		var state = new HashMap<StandardShape, MovableStateWrapper>();
		state.put(outline, new MovableStateWrapper(new double[] {0,0,0}, true));
		states.add(state);
		setStates(states);
	}

	
	public Vector getActiveForce() {
		return activeForce;
	}
	protected void setActiveForce(Vector activeForce) {
		this.activeForce = activeForce;
	}
	public void setMass(double mass) {
		this.mass = mass;
	}
	public double getMass() {
		return mass;
	}
	public void setCoD(double CoD) { //Coefficient of Drag
		this.coefficientOfDrag = CoD;
	}
	public double getCoD() {
		return coefficientOfDrag;
	}
	public void setCoF(double CoF) { // Coefficient of Friction
		this.coefficientOfFriction = CoF;
	}
	public double getCoF() {
		return coefficientOfFriction;
	}
	public void setCoR(double CoR) { // Coefficient of Friction
		this.coefficientOfRestitution = CoR;
	}
	public double getCoR() {
		return coefficientOfRestitution;
	}
	public Vector getVelocity(){
		return velocity;
	}
	public void setVelocity(Vector v) {
		velocity = v;
	}	
	private List<StandardShape> getShapes(){
		return shapes;
	}
	private List <StandardShape> getCorporealShapes(){
		return corporealShapes;
	}
	private void setShapes(List<StandardShape> shapes) {
		this.shapes = shapes;
	}
	private void setCorporealShapes(List<StandardShape> corporealShapes) {
		this.corporealShapes = corporealShapes;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}	
	@Override
	public OutlineShape getOutline() {
		return getShapes().get(getState());
	}	
	public OutlineShape getCorporealOutline() {
		return getCorporealShapes().get(getState());
	}

	public boolean isStopped() {
		return (getVelocity().getMagnitude() == 0);
	}
	public int getOnFloorTimer() {
		return onFloorTimer;
	}
	public void decrementFloorTimer() {
		onFloorTimer -= 1;
	}
	public void touchFloor() {
		onFloorTimer = FLOORWINDOW;
	}
	@Override
	public abstract Movable clone();	
	
	//////////////////////////////////////////////////////////////////////////////////
	
	public abstract void collision(Obstacle o);			//analagous to the similar method for Movables in Collidable. Only movables can collide with obstacles, see.
	protected void moveTick() {
		move(Vector.scalarMultiply(getVelocity(), TIMESCALE));
	}

	protected void applyFriction() {
		if (!isStopped()){
			applyForce(new Vector (getCoF() * getMass()+ getCoD() * Math.pow(getVelocity().getMagnitude(),2),
					Vector.directionOfReverse(getVelocity())));
		}
	}
	public void applyConstantForces() {
		applyFriction();
		applyForce(new Vector(getMass() * GRAVITY, new double[] {0,-1,0}));
	}

	public void stop(){
		setActiveForce(new Vector());
		getVelocity().setMagnitude(0);
	}
	public void applyForce(Vector force) {
		setActiveForce(Vector.addVectors(getActiveForce(),force));
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	
	public void accelerationTick() {	//apply accelerations to velocity. This will happen /after/ movetick, technically creating a small
		//disconnect, whereby an object will move /before/ it accelerates, but this is very small, and self consistent
		var velocity = Vector.addVectors(getVelocity(),Vector.scalarMultiply(getActiveForce(),TIMESCALE/getMass()));
		setActiveForce(new Vector());

		if (velocity.getMagnitude() < 0.05) {		//to stop asymptotic approaches to 0 speed.
			stop();
		} else {
			setVelocity(velocity);
		}
	}
	public void tick() {  //move object/ add forces to velocity.
		accelerationTick();															//ORDER OF USE : Apply all your forces -> CollisionEngine -> tick();
		moveTick();
		decrementFloorTimer();
	}
}