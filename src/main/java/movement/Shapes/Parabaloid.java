package movement.Shapes;

import movement.mathDS.Vector;
import movement.mathDS.Vector.MalformedVectorException;

public class Parabaloid extends NormalShape{
	//creates a parabolic cylinder of a certain thickness. 
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
	@Override
	public Vector getNormal(float[] position) throws MalformedVectorException {
		var dir = new double[Vector.DIMENSIONS];
		dir[getCurvedDimension()] = getDimensions()[getCurvedDimension()] * (position[getCurvedDimension()]/ Math.pow(getDimensions()[getLongDimension()],2) - 1); 
		double magnitude = Math.sqrt(Math.pow(dir[getCurvedDimension()], 2) + 1);
		dir[getCurvedDimension()] *= getParity() ? 1/magnitude:-1/magnitude;
		dir[getLongDimension()] = 1/magnitude;
		Vector output = new Vector(1, dir);
		if (!isConcave(position)) {
			output = Vector.getReverse(output);
		}
		return output;
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
	public boolean inside(float[] position) {
		boolean output = true;
		for (int i = 0; i<Vector.DIMENSIONS; i++) {
			output = output && position[i] >= 0 && position[i] <=  getDimensions()[i];
		} if (output) {
			double k = getDimensions()[getLongDimension()];
			double h = getDimensions()[getCurvedDimension()]/2;
			double tmp;
			if (getParity()) {
				tmp = position[getLongDimension()] + k*Math.pow(position[getCurvedDimension()]/h, 2) - 2*k*position[getCurvedDimension()]/h;
			} else {
				tmp = -1* (position[getLongDimension()] - k*Math.pow(position[getCurvedDimension()]/h, 2) + 2*k*position[getCurvedDimension()]/h + k);
			}
			output = (tmp>= 0 && tmp <= getThickness());
		}
		return output;
	}

	@Override
	public float[] pointOnEdge(float[] position) {
		for (int i = 0; i<Vector.DIMENSIONS; i++) {
			if (!(position[i] >= 0 && position[i] <=  getDimensions()[i])) {
				position[i] = (float) (position[i] >= getDimensions()[i] ? getDimensions()[i] : 0);
			}
		}
		double k = getDimensions()[getLongDimension()];
		double h = getDimensions()[getCurvedDimension()]/2;
		position[getLongDimension()] = (float) (getParity() ?
				isConcave(position) ? -k*Math.pow(position[getCurvedDimension()]/h, 2) + 2*k*position[getCurvedDimension()]/h + getThickness()
									: -k*Math.pow(position[getCurvedDimension()]/h, 2) + 2*k*position[getCurvedDimension()]/h:
				isConcave(position) ? k*Math.pow(position[getCurvedDimension()]/h, 2) - 2*k*position[getCurvedDimension()]/h + k + getThickness()
									: k*Math.pow(position[getCurvedDimension()]/h, 2) - 2*k*position[getCurvedDimension()]/h + k );
		return position;
	}

	@Override
	public double distanceIn(float[] position) {
		var tmp = pointOnEdge(position);
		double sum = 0; 
		for (int i = 0; i<Vector.DIMENSIONS; i++) {
			sum += Math.pow(position[i] - tmp[i] , 2);
		}
		var distance = Math.sqrt(sum);
		if (!inside(position)) {
			distance *= -1;
		}
		return Math.sqrt(distance);
	}
	public boolean isConcave(float[] position) {	//true == concave, false == convex
		double k = getDimensions()[getLongDimension()];
		double h = getDimensions()[getCurvedDimension()]/2;
		double tmp;
		if (getParity()) {
			tmp = position[getLongDimension()] + k*Math.pow(position[getCurvedDimension()]/h, 2) - 2*k*position[getCurvedDimension()]/h;
		} else {
			tmp = -1* (position[getLongDimension()] - k*Math.pow(position[getCurvedDimension()]/h, 2) + 2*k*position[getCurvedDimension()]/h + k);
		}
		return tmp> getThickness()/2;
	}
	@Override
	protected boolean inCollisionNet(float[] point) {
		return inside(point);
	}
}
