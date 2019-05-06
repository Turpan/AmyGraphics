package movement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import movement.Shapes.OutlineShape;
import movement.Shapes.PolyShape;
import movement.mathDS.Vector;

public abstract class Movable extends Collidable{
	//Movables are collidables that have the ability to move. They have the capacity to change
	//their outlineShapes. They require a mass, and defined states. Within any one state
	//They have a regular outline (which acts for determining what sort of bouncing and collision
	//occurs), a corporealOutline (which can be affected by extraCollisionEffects, and are
	//pushed out of other objects), and an activeOutline (which transmits extraCollisionEffects).
	//These variants on the outline are just subsets of the regular outline, determined by booleans
	//in the wrapper class for each shape in a state.
	
	//Pure movables that don't inherit this classes subclasses include things like:
	//arrows and other projectiles.
	
	public final static double  TIMESCALE = 0.1;
	
	private final static double GRAVITY = 18;
	private final static int FLOORWINDOW = 2;
	
	private Vector velocity = new Vector(new double[] {0,0,0});
	private Vector activeForce = new Vector(new double[] {0,0,0});
	private double mass; 						// don't let this one equal 0.... If you want a default value, go with 1.
	private double coefficientOfDrag;
	private double coefficientOfFriction;
	private List<Map<OutlineShape, MovableStateWrapper>> states;
	//oh dear god this is a mess. the index of the Map is the state number. Each Map represents one state. The shapes in the Map are the components of the polyShapes that, ultimately, make up that shapes Outline. (unless there's just one shape in a map)
	//The boolean in the wrapper is the ghost boolean. This represents /not/ being teleported out of other objects.
	private List<OutlineShape> shapes;	//the different shapes in each of the states
	private List<OutlineShape> corporealShapes; //same as regular shapes, but ignores ghosts.
	private List<OutlineShape> activeShapes;	//apply the collision effects if they hit something else.
	
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
		
		List<Map<OutlineShape, MovableStateWrapper>> clonedStates = new ArrayList<Map<OutlineShape, MovableStateWrapper>>();
		Map<OutlineShape, MovableStateWrapper> stateToAdd;
		Iterator<OutlineShape> it;
		OutlineShape outline;
		for (Map<OutlineShape, MovableStateWrapper> state : movable.getStates()) {
			stateToAdd = new HashMap<OutlineShape, MovableStateWrapper>();
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
	public void setStates(List<Map<OutlineShape, MovableStateWrapper>> states) {
		//I'm currently assuming that every state contains at least one corporeal shape
		
		this.states = states;
		HashMap<OutlineShape, double[]> currentStateShapes = new HashMap<OutlineShape, double[]>();
		HashMap<OutlineShape, double[]> currentStateCorporealShapes = new HashMap<OutlineShape, double[]>();
		HashMap<OutlineShape, double[]> currentStateActiveShapes = new HashMap<OutlineShape, double[]>();

		List<OutlineShape> finalShapes = new ArrayList<OutlineShape>();
		List<OutlineShape> finalCorporealShapes = new ArrayList<OutlineShape>();
		List<OutlineShape> finalActiveShapes = new ArrayList<OutlineShape>();

		OutlineShape shape;
		OutlineShape totalStateShape;
		OutlineShape totalStateCorporealShape;		
		OutlineShape totalStateActiveShape;

		
		double[] dimensions = new double[Vector.DIMENSIONS];
		for (int i = 0; i< states.size(); i++) {
			var it = states.get(i).keySet().iterator();
			if (states.get(i).keySet().size()==1) {
				totalStateShape = it.next();
				totalStateCorporealShape = totalStateShape;
				totalStateActiveShape = totalStateShape;
			} else {
				while (it.hasNext()) {
					shape = it.next();
					currentStateShapes.put(shape, states.get(i).get(shape).relativePosition);
					if (!states.get(i).get(shape).ghost) {
						currentStateCorporealShapes.put(shape, states.get(i).get(shape).relativePosition);	
					}if (states.get(i).get(shape).active) {
						currentStateActiveShapes.put(shape, states.get(i).get(shape).relativePosition);	
					}
				}
				totalStateShape = new PolyShape(currentStateShapes);
				totalStateCorporealShape = new PolyShape(currentStateCorporealShapes);
				totalStateActiveShape = new PolyShape(currentStateActiveShapes);
			}
			for (int j = 0; j < Vector.DIMENSIONS ; j++) {
				if (totalStateShape.getDimensions()[j] > dimensions[j]) {
					dimensions[j] = totalStateShape.getDimensions()[j];
				}
			} 			
			finalShapes.add(i,totalStateShape);
			finalCorporealShapes.add(i, totalStateCorporealShape);
			finalActiveShapes.add(i, totalStateActiveShape);
		}
		setShapes(finalShapes);
		setCorporealShapes(finalCorporealShapes);
		setActiveShapes(finalActiveShapes);
		setDimensions(dimensions);
	}
	private List<Map<OutlineShape, MovableStateWrapper>> getStates() {
		return states;
	}	
	public void setSimpleOutline(OutlineShape outline) {
		var states = new ArrayList<Map<OutlineShape, MovableStateWrapper>>();
		var state = new HashMap<OutlineShape, MovableStateWrapper>();
		state.put(outline, new MovableStateWrapper(new double[] {0,0,0}, true, true));
		states.add(state);
		setStates(states);
	}

	@Override
	public void setRotationAxis(double[] rotationAxis) {
		super.setRotationAxis(rotationAxis);
		for (OutlineShape shape:getShapes()) {
			shape.setRotationAxis(rotationAxis.clone());
		}for (OutlineShape shape:getCorporealShapes()) {
			shape.setRotationAxis(rotationAxis.clone());
		}for (OutlineShape shape:getActiveShapes()) {
			shape.setRotationAxis(rotationAxis.clone());
		}
	}
	@Override
	public void setAngle(double angle) {
		super.setAngle(angle);
		for (OutlineShape shape:getShapes()) {
			shape.setAngle(angle);
		}for (OutlineShape shape:getCorporealShapes()) {
			shape.setAngle(angle);
		}for (OutlineShape shape:getActiveShapes()) {
			shape.setAngle(angle);
		}
	}
	@Override
	public void setCentreOfRotation(double[] centreOfRotation) {
		super.setCentreOfRotation(centreOfRotation);
		for (OutlineShape shape:getShapes()) {
			shape.setCentreOfRotation(centreOfRotation.clone());
		}for (OutlineShape shape:getCorporealShapes()) {
			shape.setCentreOfRotation(centreOfRotation.clone());
		}for (OutlineShape shape:getActiveShapes()) {
			shape.setCentreOfRotation(centreOfRotation.clone());
		}
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
	
	private List<OutlineShape> getShapes(){
		return shapes;
	}
	private List <OutlineShape> getCorporealShapes(){
		return corporealShapes;
	}
	private List <OutlineShape> getActiveShapes(){
		return activeShapes;
	}
	private void setShapes(List<OutlineShape> shapes) {
		this.shapes = shapes;
	}
	private void setCorporealShapes(List<OutlineShape> corporealShapes) {
		this.corporealShapes = corporealShapes;
	}
	private void setActiveShapes(List<OutlineShape> activeShapes) {
		this.activeShapes = activeShapes;
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
	public OutlineShape getActiveOutline() {
		return getActiveShapes().get(getState());
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
	public boolean onFloor() {
		return getOnFloorTimer() > 0;
	}
	@Override
	public abstract Movable clone();	
	
	//////////////////////////////////////////////////////////////////////////////////
	
	public void extraCollisionEffect(Movable m) {
		var relativePosition = new double[3];
		for (int i = 0; i<3;i++) {
			relativePosition[i] = getPosition()[i]-m.getPosition()[i];
		}
		if (m.getOutline().inside(m.getCorporealOutline().exactCollisionPosition(getActiveOutline(), relativePosition))) {
			applyCollisionEffect(m);
		}
	}
	protected abstract void applyCollisionEffect(Movable m);
	
	protected void moveTick() {
		move(Vector.scalarMultiply(getVelocity(), TIMESCALE));
	}

	protected void applyFriction() {
		if (!isStopped()){
			var magnitude =  getCoD() * Math.pow(getVelocity().getMagnitude(),2);
			if (onFloor()) {
				magnitude += getCoF() * getMass();
			}
			applyForce(new Vector(magnitude,Vector.directionOfReverse(getVelocity())));
		}
	}
	public void applyConstantForces() {
		applyFriction();
		applyForce(new Vector(getMass() * GRAVITY, new double[] {0,-1,0}));
	}

	public void stop(){
		setActiveForce(new Vector(new double[] {0,0,0}));
		getVelocity().setMagnitude(0);
	}
	public void applyForce(Vector force) {
		setActiveForce(Vector.addVectors(getActiveForce(),force));
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	
	public void accelerationTick() {	//apply accelerations to velocity. This will happen /after/ movetick, technically creating a small
		//disconnect, whereby an object will move /before/ it accelerates, but this is very small, and self consistent
		var velocity = Vector.addVectors(getVelocity(),Vector.scalarMultiply(getActiveForce(),TIMESCALE/getMass()));
		setActiveForce(new Vector(new double[] {0,0,0}));

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