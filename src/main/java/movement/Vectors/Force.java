package movement.Vectors;


public class Force extends Vector {
	public Force(double magnitude, double[] direction) throws MalformedVectorException {
		super(magnitude,direction);
	}
	public Force() throws MalformedVectorException {
		super();
	}
	public Acceleration getAcceleration(double mass) throws MalformedVectorException { //don't use mass of 0? Please?
		
		var output = new Acceleration(getMagnitude()/mass,getDirection());	
		return output;
	}
}