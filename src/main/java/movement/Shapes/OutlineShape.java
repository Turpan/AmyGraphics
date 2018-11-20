package movement.Shapes;

import movement.mathDS.Vector;
import movement.mathDS.Vector.MalformedVectorException;

public interface OutlineShape {
	public Vector getNormal(double[] position) throws MalformedVectorException; //normal of the shape at a point.
	public double[] getDimensions();
	public void setDimensions(double[] dimensions);
	public boolean inside(double[] position);
	public double[] exactCollisionPosition(OutlineShape collider, double[] relativePosition);
	public double[] pointOnEdge(double[] position);
	public double distanceIn(double[] position);	//returns a negative value for values outside of the shape
}
