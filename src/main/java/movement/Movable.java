package movement;

import java.util.ArrayList;
import movement.Shapes.OutlineShape;
import movement.mathDS.Vector;

public abstract class Movable extends Collidable{

	private Vector velocity = new Vector();
	final public static double  TIMESCALE = 0.1;
	final public static double GRAVITY = 9.81;
	final public static int FLOORWINDOW = 5;
	private Vector activeForce = new Vector();
	private double mass; 						// don't let this one equal 0.... If you want a default value, go with 1.
	private OutlineShape outline;
	private double coefficientOfDrag;
	private double coefficientOfFriction;
	private ArrayList<Entity> attachedEntities = new ArrayList<Entity>();	//move when this one moves!
	private double coefficientOfRestitution; 	//Because CoR is kinda a terrible measure, in a collision, this value is averaged with the enemies because physics doesn't actually have any more direct concept of the 'bounciness' of an object in isolation
	private int onFloorTimer;					//Represents an object being on the floor. Or rather, having recently collided with a Floor object.

	public Movable() {
	}
	protected Movable(Movable movable) {
		super(movable);
		setVelocity(movable.getVelocity().clone());
		setActiveForce(movable.getActiveForce().clone());
		for (Entity attached : movable.getAttachedEntities()) {
			attachEntity(attached.clone());
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

	public void addVelocity(Vector velocity) {
		getVelocity().addVector(velocity);
	}
	public boolean isStopped() {
		return (getVelocity().getMagnitude() == 0);
	}

	@Override
	public void setOutline(OutlineShape outline) {
		this.outline = outline;
	}
	@Override
	public OutlineShape getOutline() {
		return outline;
	}
	public ArrayList<Entity> getAttachedEntities() {
		return attachedEntities;
	}
	public void attachEntity(Entity entity) {
		getAttachedEntities().add(entity);
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
		move(Vector.scalarProduct(getVelocity(), TIMESCALE));
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
		getActiveForce().addVector(force);
	}

	@Override
	public void move(Vector movement) {
		super.move(movement);
		for (Entity e : getAttachedEntities()) {
			e.move(movement);
		}
	}
	//////////////////////////////////////////////////////////////////////////////////
	public void accelerationTick() {	//apply accelerations to velocity. This will happen /after/ movetick, technically creating a small
		//disconnect, whereby an object will move /before/ it accelerates, but this is very small, and self consistent
		var velocity = getVelocity();
		velocity.addVector(Vector.scalarProduct(getActiveForce(),TIMESCALE/getMass()));
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