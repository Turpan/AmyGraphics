package movement.Shapes;

import java.util.ArrayList;

import movement.Vectors.Vector;
import movement.Vectors.Vector.MalformedVectorException;

public interface OutlineShape {
	final static int collisionDetectionGranularity = 10;
	
	public Vector getNormal(float[] position) throws MalformedVectorException; //normal of the shape at a point.
	public double[] getDimensions();
	public void setDimensions(double[] dimensions);
	public ArrayList<float[]> getCollisionNet();
	public boolean inside(float[] position);
	public void initialiseCollisionNet();
	public float[] getPointOnEdge(float[] position);
	public double getDistanceIn(float[] position);
}
