package movement.Shapes;

import movement.mathDS.Vector;

public class Triangle extends StandardShape {
	double[] dimensions;

	public Triangle (double[] dimensions) {
		setDimensions(dimensions);
	}
	protected Triangle(Triangle triangle) {
		super(triangle);
	}
	public Vector getNormal(double[] position) {
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
	protected boolean inCollisionNet(double[] point) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public StandardShape clone() {
		// TODO Auto-generated method stub
		return null;
	}
}
