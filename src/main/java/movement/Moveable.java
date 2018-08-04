package movement;

import java.util.ArrayList;
import java.util.Iterator;

import movement.Shapes.OutlineShape;
import movement.mathDS.Force;
import movement.mathDS.Vector;
import movement.mathDS.Velocity;
import movement.mathDS.Vector.MalformedVectorException;

public abstract class Moveable extends Entity implements Collidable{

	private Velocity velocity;
	final public static double  TIMESCALE = 0.1;
	private Force activeForce;
	private double mass; 						// don't let this one equal 0.... If you want a default value, go with 1.
	private OutlineShape outline;
	private double coefficientOfDrag;		
	private double coefficientOfFriction; 	
	private ArrayList<Entity> attachedEntities;		//move when this one moves!
	private double coefficientOfRestitution; 	//Because CoR is kinda a terrible measure, in a collision, this value is averaged with the enemies
										//because physics doesn't actually have any more direct concept of the 'bounciness' of an object in isolation
																				//yet...
	public Moveable () throws MalformedVectorException {
		setVelocity(new Velocity());
	}
	public Force getActiveForce() {
		return activeForce;
	}
	protected void setActiveForce(Force activeForce) {
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
	public Velocity getVelocity(){
		return velocity;
	}
	public void setVelocity(Velocity v) {
		velocity = v;
	}
	
	public void addVelocity(Velocity velocity) throws MalformedVectorException {
		getVelocity().addVector(velocity);
	}
	public boolean isStopped() {
		return (getVelocity().getMagnitude() == 0);
	}
	
	public void setOutline(OutlineShape outline) {
		this.outline = outline;
	}
	public OutlineShape getOutline() {
		return outline;
	}
	public boolean inside(float[] point) {
		return getOutline().inside(point);
	}
	public ArrayList<Entity> getAttachedEntities() {
		return attachedEntities;
	}
	public void setAttachedEntities(ArrayList<Entity> Es) {
		attachedEntities = Es;
	}
	//////////////////////////////////////////////////////////////////////////////////
	protected void moveTick() throws MalformedVectorException {
		move(new Vector(getVelocity().getMagnitude() * TIMESCALE, getVelocity().getDirection()));
	}
	
	protected void applyFriction() throws MalformedVectorException {
		if (!isStopped()){
			applyForce(new Force (getCoF() * getMass()+ getCoD() * Math.pow(getVelocity().getMagnitude(),2),
								  Vector.directionOfReverse(getVelocity())));
		}
	}
	public void applyConstantForces() throws MalformedVectorException {
		applyFriction();
		applyForce(new Force(getMass() * 9.81, new double[] {0,1,0}));
	}
	
	public void stop() throws MalformedVectorException {
		setActiveForce(new Force());
		getVelocity().setMagnitude(0);
	}
	public void applyForce(Force force) throws MalformedVectorException {
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
	public void accelerationTick() throws MalformedVectorException {	//apply accelerations to velocity. This will happen /after/ movetick, technically creating a small 
																		//disconnect, whereby an object will move /before/ it accelerates, but this is very small, and self consistent
		var velocity = getVelocity();
		getActiveForce().setMagnitude(getActiveForce().getMagnitude() * TIMESCALE / getMass());
		velocity.addVector(getActiveForce());
		setActiveForce(new Force());
		
		if (velocity.getMagnitude() < 0.05) {
			stop();
		} else {
			setVelocity(velocity);
		}
	}
	public void tick() throws MalformedVectorException, MalformedEntityException {  //move object/ add forces to velocity. 
		accelerationTick();															//ORDER OF USE : Apply all your forces -> CollisionEngine -> tick();
		moveTick();
	}
}