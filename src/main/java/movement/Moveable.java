package movement;

import java.util.ArrayList;
import java.util.Iterator;

import movement.Shapes.OutlineShape;
import movement.Vectors.Acceleration;
import movement.Vectors.Force;
import movement.Vectors.Vector;
import movement.Vectors.Vector.MalformedVectorException;
import movement.Vectors.Velocity;

public abstract class Moveable extends Entity implements Collidable{

	private Velocity velocity;
	final public static double  TIMESCALE = 0.1;
	private ArrayList<Acceleration> accelerations = new ArrayList<Acceleration>();
	private double mass; 						// don't let this one equal 0.... If you want a default value, go with 1.
	private OutlineShape outline;
	private double coefficientOfDrag;		
	private double coefficientOfFriction; 	
	private double coefficientOfRestitution; 	//Because CoR is kinda a terrible measure, in a collision, this value is averaged with the enemies
										//because physics doesn't actually have any more direct concept of the 'bounciness' of an object in isolation
																				//yet...
	public Moveable () throws MalformedVectorException {
		setVelocity(new Velocity());
	}
	public ArrayList<Acceleration> getAccelerations() {
		return accelerations;
	}
	public void addAcceleration(Acceleration acceleration) {
		getAccelerations().add(acceleration);
	}
	public void removeAcceleration(Acceleration acceleration) {
		getAccelerations().remove(acceleration);
	}
	protected void setAccelerations(ArrayList<Acceleration> accelerations) {
		this.accelerations = accelerations;
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
	//////////////////////////////////////////////////////////////////////////////////
	protected void move() throws MalformedEntityException {
		float[] newPosition = new float[Vector.DIMENSIONS];
		float[] currentPosition = getPosition();
		double[] moveCmpnts = getVelocity().getComponents();
		for (int i=0;i<Vector.DIMENSIONS;i++) {
			newPosition[i] = (float) (currentPosition[i] + moveCmpnts[i]*TIMESCALE);
		}
		setPosition(newPosition);
	}
	protected void applyFriction() throws MalformedVectorException {
		if (!isStopped()){
			applyForce(new Force (getCoF() * getMass()+ getCoD() * Math.pow(getVelocity().getMagnitude(),2),
								  Vector.directionOfReverse(getVelocity())));
		}
	}	
	
	public void stop() throws MalformedVectorException {
		setAccelerations(new ArrayList<Acceleration>());
		getVelocity().setMagnitude(0);
	}
	public void applyForce(Force force) throws MalformedVectorException {
		addAcceleration(force.getAcceleration(getMass()));
	}
	public void accelerate(Acceleration acceleration) throws MalformedVectorException {
		var velocity = getVelocity();
		acceleration.setMagnitude(acceleration.getMagnitude() * TIMESCALE);
		velocity.addVector(acceleration);
		setVelocity(velocity);
		
	}
	public void teleport(Vector movement) {
		float[] newPosition = new float[Vector.DIMENSIONS];
		float[] currentPosition = getPosition();
		double[] moveCmpnts = movement.getComponents();
		for (int i=0;i<Vector.DIMENSIONS;i++) {
			newPosition[i] = (float) (currentPosition[i] + moveCmpnts[i]);
		}
		try {
			setPosition(newPosition);
		} catch (MalformedEntityException e) {
			e.printStackTrace();
		}
		
	}
	//////////////////////////////////////////////////////////////////////////////////
	public void accelerationTick() throws MalformedVectorException {	//apply accelerations to velocity. This will happen /after/ movetick, technically creating a small 
										//disconnect, whereby an object will move /before/ it accelerates, but this is very small, and self consistent
		Iterator<Acceleration> iter = getAccelerations().iterator();
		Acceleration acc = new Acceleration();
		Acceleration tmp;
		while (iter.hasNext()) {	
			tmp = iter.next();
			acc.addVector(tmp);
			iter.remove();
//			if (tmp.active()) { 
//				acc.addVector(tmp);
//				tmp.deactivate();
//			}else {
//				iter.remove();
//			}
		}
		accelerate(acc);
		if (getVelocity().getMagnitude() < 0.05) {
			stop();
		}
	}
	public void tick() throws MalformedVectorException, MalformedEntityException { //apply all forces that are applied per tick, move object 
		applyForce(new Force(getMass() * 9.81, new double[] {0,1,0}));
		applyFriction();
		accelerationTick();
		move();
	}
}