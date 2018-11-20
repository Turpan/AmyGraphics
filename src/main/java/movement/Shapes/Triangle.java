package movement.Shapes;

import movement.mathDS.Vector;
import movement.mathDS.Vector.MalformedVectorException;

public class Triangle implements OutlineShape {
	double[] dimensions;

	public Triangle (double[] dimensions) {
		setDimensions(dimensions);
	}
	@Override
	public void setDimensions(double[] dimensions) {
		this.dimensions= dimensions;
	}
	@Override
	public double[] getDimensions() {
		return dimensions;
	}
	@Override
	public Vector getNormal(double[] position) throws MalformedVectorException {
		return new Vector();
	}
	@Override
	public boolean inside(double[] position) {
		return false;
	}
	public void initialiseCollisionNet() {

	}
	@Override
	public double[] pointOnEdge(double[] position) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public double distanceIn(double[] position) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double[] exactCollisionPosition(OutlineShape collider, double[] relativePosition) {
		// TODO Auto-generated method stub
		return null;
	}
}
