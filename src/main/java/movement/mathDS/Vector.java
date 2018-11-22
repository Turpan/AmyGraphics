package movement.mathDS;


public class Vector {
	// Creates a generic, n (see DIMENSIONS) dimensional vector, expressed internally in components, for ease of operations. 
	public static final int DIMENSIONS = 3;		// NOTE: WHEN CHANGING THIS MAKE SURE TO MAKE THE BELOW VECTORS OF APPROPRIATE LENGTH!
	private static final double[] SIMPLEVECTOR = new double[] {1,0,0};	//This is just called everytime a vector is called using the emptyconstructor, and constructing it everytime takes a for loop and junk.
	
	private double[] components;

	public Vector(double magnitude, double[] direction) {
		double[] cmpnts = new double[DIMENSIONS];
		for (int i = 0; i < DIMENSIONS; i++) {
			cmpnts[i] = Math.abs(magnitude) * direction[i];
		}
		setComponents(cmpnts);
	}
	public Vector(){
		this(0, SIMPLEVECTOR);
	}
	public void setComponents(double[] components){
		if (components.length != DIMENSIONS) {
			this.components = new double[DIMENSIONS];
			for (int i = 0; i<components.length && i<DIMENSIONS; i++) {
				this.components[i] = components[i];
			}
		}else {
			this.components = components;
		}
	}
	public double[] getComponents() {
		return components;
	}

	@Override
	public Vector clone() {
		var output = new Vector();
		output.setComponents(getComponents().clone());
		return output;
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
	public void setDirection(double[] direction){
		var tmp = direction.clone();		//don't want to be destructive of direction...
		if (tmp.length < DIMENSIONS) {	//don't need to check for tmp>DIM, as, due to the implementation, this doesn't actually matter.
			var tmp2 = new double[DIMENSIONS];
			for(int i = 0; i< tmp.length; i++) {
				tmp2[i] = tmp[i];
			}
			tmp = tmp2;
		}
		double check = 0;
		double magnitude = getMagnitude();
		double[] output = new double[DIMENSIONS];
		for (int i = 0; i<DIMENSIONS;i++) {
			check += tmp[i]*tmp[i];
			output[i] = magnitude * tmp[i];
		}
		if (check<0.98||check>1.02) {//technically, should equal 1, but slight rounding errors, working with irrational numbers converted to decimal.
			
			for (int i = 0; i<DIMENSIONS;i++) {
				tmp[i] = tmp[i]/check;
			}
			setDirection(tmp);
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
	public void setMagnitude(double magnitude) { //WARNING: If you lower magnitude to 0, then increase it you /MUST/ set direction anew. Direction can't be stored when mag = 0.
		double oldMagnitude = getMagnitude();
		var oldCmpnts = getComponents();
		double[] cmpnts = new double[DIMENSIONS];
		for (int i = 0; i < DIMENSIONS; i++) {
			cmpnts[i] = Math.abs(magnitude/oldMagnitude) * oldCmpnts[i];
		}
		setComponents(cmpnts);
	}

	///////////////////////////////////////////////////////////////////////////////

	public void addVector (Vector vToAdd) {
		double[] cmpnts = getComponents();
		double[] toAdd = vToAdd.getComponents();
		for (int i = 0; i<DIMENSIONS;i++) {
			cmpnts[i] += toAdd[i];
		}
		setComponents(cmpnts);
	}

	public static double[] directionOfReverse(Vector v){
		return Vector.getReverse(v).getDirection();
	}
	public static Vector getReverse(Vector v){
		return Vector.scalarProduct(v,-1);
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

	public static boolean vectorMovingWith(Vector v, Vector comparator){
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
	public static Vector scalarProduct(Vector v, double scalar) {
		var inCmpnts = v.getComponents();
		var outCmpnts = new double[Vector.DIMENSIONS];
		for (int i = 0; i<Vector.DIMENSIONS;i++){
			outCmpnts[i] =inCmpnts[i] * scalar;
		}
		var output = new Vector();
		output.setComponents(outCmpnts);
		return output;
	}
}