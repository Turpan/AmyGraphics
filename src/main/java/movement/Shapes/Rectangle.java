package movement.Shapes;

import movement.mathDS.Vector;
import movement.mathDS.Vector.MalformedVectorException;

public class Rectangle extends NormalShape{

	
	public Rectangle (double[] dimensions) {
		super(dimensions);
	}
	public Vector getNormal(float[] position) throws MalformedVectorException {
		double minimumDist = Double.MAX_VALUE;	//trivial upperbound
		double tmpDist = 0;
		int smallestDimension = 0;
		boolean farside = false;
		for(int i = 0; i<Vector.DIMENSIONS;i++) {
			tmpDist = Math.abs(position[i] - getDimensions()[i]);
			if (minimumDist > tmpDist ) {
				minimumDist = tmpDist;
				smallestDimension = i;
				farside = true;
			}
			tmpDist = Math.abs(position[i]);
			if (minimumDist >  Math.abs(position[i]) ) {
				minimumDist = tmpDist;
				smallestDimension = i;
				farside = false;
			}
		}
		double[] direction = new double[Vector.DIMENSIONS];
		direction[smallestDimension] = farside ? -1 : 1;
		return new Vector(1, direction);
	}
	@Override
	public boolean inside(float[] position) {
		boolean output = true;
		int i = 0;
		while (output && i<Vector.DIMENSIONS) {
			output = (position[i] >= 0 && position[i] <= getDimensions()[i]); 
			i++;
		}
		return output;
	}
	@Override
	public float[] pointOnEdge(float[] position) {
		double minimumDist = Double.MAX_VALUE;	//trivial upperbound
		double tmpDist = 0;
		int smallestDimension = 0;
		boolean farside = false;
		for(int i = 0; i<Vector.DIMENSIONS;i++) {
			tmpDist = Math.abs(position[i] - getDimensions()[i]);
			if (minimumDist > tmpDist ) {
				minimumDist = tmpDist;
				smallestDimension = i;
				farside = true;
			}
			tmpDist = Math.abs(position[i]);
			if (minimumDist >  Math.abs(position[i]) ) {
				minimumDist = tmpDist;
				smallestDimension = i;
				farside = false;
			}
		}
		position[smallestDimension] = (float)(farside ? getDimensions()[smallestDimension]: 0);
		return position;
	}
	@Override
	public double distanceIn(float[] position) {
		boolean[] outside = new boolean[Vector.DIMENSIONS];
		double[] distance = new double[Vector.DIMENSIONS]; 
		double minimumDist = Double.MAX_VALUE;	//trivial upperbound
		double tmpDist = 0;
		double tmpDistII = 0;
		double squareSum = 0;
		for (int i = 0; i<Vector.DIMENSIONS;i++) {
			tmpDist = Math.abs(position[i] - getDimensions()[i]);
			tmpDistII = Math.abs(position[i]);
			distance[i] = tmpDist < tmpDistII ?  tmpDist : tmpDistII;
			minimumDist = minimumDist < distance[i]?  minimumDist : distance[i];
			if (position[i] - getDimensions()[i] > 0 || position[i]<0) {
				outside[i] = true;
			}
		}
		for(int i = 0; i<Vector.DIMENSIONS;i++) {
			if (outside[i]) {
				squareSum += Math.pow(distance[i],2);
			}
		}
		if (squareSum == 0) {
			return minimumDist;
		} else {
			return -Math.sqrt(squareSum);
		}
	}
	@Override
	protected boolean inCollisionNet(float[] point) {
		int i = 0;
		boolean acccccceptable = false;
		while (acccccceptable && i<Vector.DIMENSIONS) {
			acccccceptable = (point[i] >= 0 && point[i] < 0.25 * getDimensions()[i] ) || (point[i] > 0.75 * getDimensions()[i] && point[i] <= getDimensions()[i]); 
			i++;
		}
		return acccccceptable;
	}
}

