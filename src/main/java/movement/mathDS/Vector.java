package movement.mathDS;


public class Vector {
/** Creates a generic, 2 dimensional vector, expressed in x and y components. Has commands for getting speed/direction and adding**/
	public static final int DIMENSIONS = 3;		// NOTE: WHEN CHANGING THIS MAKE SURE TO MAKE THE BELOW VECTORS OF APPROPRIATE LENGTH!
	private static final double[] SIMPLEVECTOR = new double[] {1,0,0};	//This is just called everytime a vector is called using the mag/dir constructor, and constructing it everytime takes a for loop and junk. 
	
	private double[] components;
	
	public Vector(double magnitude, double[] direction) throws MalformedVectorException {
		double[] cmpnts = new double[DIMENSIONS];
		for (int i = 0; i < DIMENSIONS; i++) {
			cmpnts[i] = Math.abs(magnitude) * direction[i];
		}
		setComponents(cmpnts);
	}
	public Vector() throws MalformedVectorException {
		this(0, SIMPLEVECTOR);
	}
	public void setComponents(double[] components) throws MalformedVectorException {
		if (components.length != DIMENSIONS) {
			throw new MalformedVectorException("Vector created with wrong dimensions");
		}
		this.components = components;
	}
	public double[] getComponents() {
		return components;
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	
	public double[] getDirection() {
		double magnitude = getMagnitude();
		
		double[] output = new double[DIMENSIONS];
		double[] cmpnts = getComponents();
		if (magnitude == 0) {//a zero vector has no direction/an arbitrary direction so set to the simplest unit vector
			return SIMPLEVECTOR;
		}
		for (int i = 0; i<DIMENSIONS;i++) {
			output[i]=cmpnts[i]/magnitude;
		}
		return output;
	}
	public void setDirection(double[] direction) throws MalformedVectorException {
		double check = 0;
		double magnitude = getMagnitude();
		double[] output = new double[DIMENSIONS];
		for (int i = 0; i<DIMENSIONS;i++) {
			check += direction[i]*direction[i];
			output[i] = magnitude * direction[i];
		}
		if (check<0.98||check>1.02) {//technically, should equal 1, but slight rounding errors, working with irrational numbers.
			throw new MalformedVectorException("attempt to set direction to non-unit vector");
		}
		setComponents(output);
	}
	public double getMagnitude() {
		double sum =0;
		for (double cmpnt:getComponents()) {
			sum += cmpnt * cmpnt;
		}
		return Math.sqrt(sum);
	}
	public void setMagnitude(double magnitude) throws MalformedVectorException { //WARNING: If you lower speed to 0, then start moving again in terms of speed you /MUST/ set direction anew. Direction can't be stored when still.
		double[] direction = getDirection();
		double[] cmpnts = new double[DIMENSIONS];
		for (int i = 0; i < DIMENSIONS; i++) {
			cmpnts[i] = Math.abs(magnitude) * direction[i];
		}
		setComponents(cmpnts);
	}
	
	///////////////////////////////////////////////////////////////////////////////
	
	public void addVector (Vector vToAdd) throws MalformedVectorException {
		double[] cmpnts = getComponents();
		double[] toAdd = vToAdd.getComponents();
		for (int i = 0; i<DIMENSIONS;i++) {
			cmpnts[i] += toAdd[i];
		}
		setComponents(cmpnts);
	}
	
	public static double[] directionOfReverse(Vector v) throws MalformedVectorException {
		double[] direction = v.getDirection();
		for (int i = 0;i<DIMENSIONS; i++) {
			direction[i] *= -1;
		}
		return direction;
	}
	public static Vector getReverse(Vector v) throws MalformedVectorException {
		return new Vector(v.getMagnitude(),directionOfReverse(v));
	}
	public static double dotProduct(Vector v1, Vector v2) { //standard vector operation, gives cos(angle)*|v1|*|v2| 
		double[] cmpnts1 = v1.getComponents();
		double[] cmpnts2 = v2.getComponents();
		double sum = 0;
		for (int i =0;i<DIMENSIONS;i++) {
			sum += cmpnts1[i] * cmpnts2[i];
		}
		return sum;
	}
	public static double angleBetween (Vector v1, Vector v2) {
		double mag1 = v1.getMagnitude();
		double mag2 = v2.getMagnitude();
		if (mag1 == 0|| mag2 == 0) {return 0;}	// technically, the question is meaningless if either v==0, but it shouldn't ever be a problem? And I don't want things to break
		return Math.acos(dotProduct(v1,v2)/(mag1*mag2));
	}
	
	public static boolean vectorMovingWith(Vector v, Vector comparator) throws MalformedVectorException { 
		//If Comparator is a norm /out/ of a surface -> checks if a vector is pushing away
		//Checks if v is moving in the same general direction as comparator
				
		double angle1= angleBetween(v,comparator);
		double angle2= angleBetween(v,Vector.getReverse(comparator));
		return angle1 <= angle2;
		
	}
	public static double getComponentParallel(Vector v , Vector comparator) {//if comparator a normal -> gets the component vector perpendicular to an angled surface
		//comparator can't be a 0 vector. /generally/ shouldn't be something where we need to /use/ such an answer, but defaults to 0 if it comes up.
		double tmp = dotProduct(v, comparator);
		if (tmp == 0) {return 0;}
		return tmp/comparator.getMagnitude();
	}
	
	
	public class MalformedVectorException extends Exception {
		private static final long serialVersionUID = 1L;

		public MalformedVectorException (String message) {
	        super (message);
	    }
	}
	
}