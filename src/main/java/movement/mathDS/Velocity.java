package movement.mathDS;

public class Velocity extends Vector{
	public Velocity(double magnitude, double[] direction) throws MalformedVectorException {
		super(magnitude,direction);
	}
	public Velocity() throws MalformedVectorException {
		super();
	}
}