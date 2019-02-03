package movement.Shapes;

import movement.mathDS.Vector;

public class Parabaloid extends StandardShape{
	//creates a parabolic wedge(?) of a certain thickness.	Think windscreen, not cone
	//this can, naturally, run along any one of the axis, so this has to be defined in the constructor.

	private int longDimension;
	private int curvedDimension;
	private double thickness;
	private boolean parity;

	public Parabaloid(double[] dimensions, int longDimension, int curvedDimension, double thickness, boolean parity) {
		super(dimensions);
		setLongDimension(longDimension);
		setCurvedDimension(curvedDimension);
		setThickness(thickness);
		setParity(parity);	//Flips the bit, reverses the parabaloid;
	}
	protected Parabaloid (Parabaloid parabaloid) {
		super(parabaloid);
		setLongDimension(parabaloid.getLongDimension());
		setCurvedDimension(parabaloid.getCurvedDimension());
		setThickness(parabaloid.getThickness());
		setParity(parabaloid.getParity());	
	}
	@Override
	public Vector getNormal(double[] position) {
		var positionRelativeShape = unrotatePoint(position);
		var dir = new double[Vector.DIMENSIONS];
		dir[getCurvedDimension()] = getDimensions()[getCurvedDimension()] * (positionRelativeShape[getCurvedDimension()]/ Math.pow(getDimensions()[getLongDimension()],2) - 1);
		double magnitude = Math.sqrt(Math.pow(dir[getCurvedDimension()], 2) + 1);
		dir[getCurvedDimension()] *= getParity() ? 1/magnitude:-1/magnitude;
		dir[getLongDimension()] = 1/magnitude;
		Vector output = new Vector(dir);
		if (!isConcave(position)) {
			output = Vector.getReverse(output);
		}
		var rotationVector = new Vector(getRotationAxis());		
		return Vector.addVectors(Vector.scalarMultiply(output, Math.cos(getAngle())), Vector.scalarMultiply(Vector.crossProduct(output, rotationVector), Math.sin(getAngle())), Vector.scalarMultiply(rotationVector, (1 - Math.cos(getAngle())) * Vector.dotProduct(rotationVector, output)));
	}
	private int getLongDimension() {
		return longDimension;
	}
	private void setLongDimension(int ld) {
		longDimension = ld;
	}
	private int getCurvedDimension() {
		return curvedDimension;
	}
	private void setCurvedDimension(int cd) {
		curvedDimension = cd;
	}
	private double getThickness() {
		return thickness;
	}
	private void setThickness(double thicc) {
		thickness = Math.abs(thicc);
	}
	private boolean getParity() {
		return parity;
	}
	private void setParity(boolean parity) {
		this.parity = parity;
	}


	@Override
	public boolean inside(double[] position) {
		var positionRelativeShape = unrotatePoint(position);
		boolean output = true;
		for (int i = 0; i<Vector.DIMENSIONS; i++) {
			output = output && positionRelativeShape[i] >= 0 && positionRelativeShape[i] <=  getDimensions()[i];
		} if (output) {
			double k = getDimensions()[getLongDimension()];
			double h = getDimensions()[getCurvedDimension()]/2;
			double tmp;
			if (getParity()) {
				tmp = positionRelativeShape[getLongDimension()] + k*Math.pow(positionRelativeShape[getCurvedDimension()]/h, 2) - 2*k*positionRelativeShape[getCurvedDimension()]/h;
			} else {
				tmp = -1* (positionRelativeShape[getLongDimension()] - k*Math.pow(positionRelativeShape[getCurvedDimension()]/h, 2) + 2*k*positionRelativeShape[getCurvedDimension()]/h + k);
			}
			output = (tmp>= 0 && tmp <= getThickness());
		}
		return output;
	}

	@Override
	public double[] pointOnEdge(double[] position) {
		var positionRelativeShape = unrotatePoint(position);
		for (int i = 0; i<Vector.DIMENSIONS; i++) {
			if (!(positionRelativeShape[i] >= 0 && positionRelativeShape[i] <=  getDimensions()[i])) {
				positionRelativeShape[i] =(positionRelativeShape[i] >= getDimensions()[i] ? getDimensions()[i] : 0);
			}
		}
		double k = getDimensions()[getLongDimension()];
		double h = getDimensions()[getCurvedDimension()]/2;
		positionRelativeShape[getLongDimension()] = getParity() ?
				isConcave(positionRelativeShape) ? -k*Math.pow(positionRelativeShape[getCurvedDimension()]/h, 2) + 2*k*positionRelativeShape[getCurvedDimension()]/h + getThickness()
				: -k*Math.pow(positionRelativeShape[getCurvedDimension()]/h, 2) + 2*k*positionRelativeShape[getCurvedDimension()]/h:
					isConcave(positionRelativeShape) ? k*Math.pow(positionRelativeShape[getCurvedDimension()]/h, 2) - 2*k*positionRelativeShape[getCurvedDimension()]/h + k + getThickness()
					: k*Math.pow(positionRelativeShape[getCurvedDimension()]/h, 2) - 2*k*positionRelativeShape[getCurvedDimension()]/h + k ;
		return rotatePoint(positionRelativeShape);
	}
	private boolean isConcave(double[] positionRelativeShape) {	//true == concave, false == convex			THIS ONE ACTUALLY, UNLIKE EVERY OTHER SHAPE METHOD, TAKES AS NA ARGUMENT A POINT RELATIVE THE SHAPE!!! Luckily, it's of 0 interest to anything outside this class.
		double k = getDimensions()[getLongDimension()];
		double h = getDimensions()[getCurvedDimension()]/2;
		double tmp;
		if (getParity()) {
			tmp = positionRelativeShape[getLongDimension()] + k*Math.pow(positionRelativeShape[getCurvedDimension()]/h, 2) - 2*k*positionRelativeShape[getCurvedDimension()]/h;
		} else {
			tmp = -1* (positionRelativeShape[getLongDimension()] - k*Math.pow(positionRelativeShape[getCurvedDimension()]/h, 2) + 2*k*positionRelativeShape[getCurvedDimension()]/h + k);
		}
		return tmp> getThickness()/2;
	}
	@Override
	protected boolean inCollisionNet(double[] point) {
		return inside(point);
	}
	@Override
	public Parabaloid clone() {
		return new Parabaloid(this);
	}
}
