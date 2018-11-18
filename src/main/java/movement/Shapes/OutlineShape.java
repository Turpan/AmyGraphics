package movement.Shapes;

import movement.mathDS.Vector;
import movement.mathDS.Vector.MalformedVectorException;

public interface OutlineShape {
	public Vector getNormal(float[] position) throws MalformedVectorException; //normal of the shape at a point.
	public double[] getDimensions();
	public void setDimensions(double[] dimensions);
	public boolean inside(float[] position);
	public float[] exactCollisionPosition(OutlineShape collider, float[] relativePosition);
	public float[] pointOnEdge(float[] position);
	public double distanceIn(float[] position);	//returns a negative value for values outside of the shape
}
