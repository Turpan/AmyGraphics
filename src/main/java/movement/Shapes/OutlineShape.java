package movement.Shapes;

import movement.mathDS.Vector;

public interface OutlineShape {
	public Vector getNormal(double[] position); //normal of the shape at a point.
	public double[] getDimensions();
	public void setDimensions(double[] dimensions);
	public boolean inside(double[] position);
	public double[] exactCollisionPosition(OutlineShape collider, double[] relativePosition);
	public double[] pointOnEdge(double[] position);
	public double distanceIn(double[] position);	//returns a negative value for values outside of the shape
	public OutlineShape clone();
}
