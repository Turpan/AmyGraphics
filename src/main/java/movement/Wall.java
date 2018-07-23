package movement;

import movement.Vectors.Vector;
import movement.Vectors.Vector.MalformedVectorException;

public abstract class Wall extends Entity implements Bouncy {
	private double bounciness;
	private Vector normal; //A /unit/ vector pushing /out/ of the wall. Determines which way things bounce!
	
	public void setBounciness(double percentage) {
		bounciness = percentage;
	}
	public double getBounciness() {
		return bounciness;
	}
	public void setNormal(Vector normal) {
		this.normal = normal;
	}
	public Vector getNormal() {
		return normal;
	}
	public boolean inside(float[] point) {
		Vector vectorToPoint;
		boolean output = false;
		try {
			double width = getWidth();
			vectorToPoint = new Vector();	//from one of the corners of the wall
			double[] cmpnts = new double[Vector.DIMENSIONS];
			Vector dimensionsVector = new Vector();
			dimensionsVector.setComponents(getDimensions());
			if (Vector.vectorMovingWith(dimensionsVector, getNormal())){
				for (int i = 0; i<Vector.DIMENSIONS;i++) {
					cmpnts[i] = point[i] - getNormal().getComponents()[i] * width;
				}
			}else {
				for (int i = 0; i<Vector.DIMENSIONS;i++) {
					cmpnts[i] = point[i];
				}
			}
			vectorToPoint.setComponents(cmpnts);
			output = !Vector.vectorMovingWith(vectorToPoint, getNormal());
		} catch (MalformedVectorException e) {
			e.printStackTrace();
		}
		return output;
	}
	public double getWidth() throws MalformedVectorException {
		Vector dimensionsVector = new Vector();
		dimensionsVector.setComponents(getDimensions());
		return Math.abs(Vector.getComponentParallel(dimensionsVector, getNormal())); 
	}
}