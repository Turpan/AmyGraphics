package movement.mathDS;


public class Force extends Vector {
	public Force(double magnitude, double[] direction) throws MalformedVectorException {
		super(magnitude,direction);
	}
	public Force() throws MalformedVectorException {
		super();
	}
}