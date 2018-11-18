package movement.Shapes;

import movement.mathDS.Vector;
import movement.mathDS.Vector.MalformedVectorException;

public class BigRectangle implements OutlineShape{
	//can't be used for collidables. Gives simplistic collision commands, cannot get a distanceIn.
	//however, it can be arbitrarily large, and smaller things won't be able to go through it.
	double[] dimensions;
	
	public BigRectangle (double[] dimensions) {
		setDimensions(dimensions);
	}
	public void setDimensions(double[] dimensions) {
		this.dimensions= dimensions;
	}
	public double[] getDimensions() {
		return dimensions;
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
		direction[smallestDimension] = farside ? 1 : -1;
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
		//abuses the fact that the shortest distance to a plane is always orthogonal to it, and that the planes are aligned along the axis => other than the axis the plane is orthogonal, the coordinates of the nearest
		//point on the edge remain constnat if within bounds of the rectangle
		double minimumDist = Double.MAX_VALUE;	//trivial upperbound
		double tmpDist = 0;
		int smallestDimension = 0;
		boolean[] farside = new boolean[Vector.DIMENSIONS];
		boolean[] outside = new boolean[Vector.DIMENSIONS];	//is this dim outside of range
		boolean totalOutside = false;	//is the whole point outside the rectangle. Could use inside(), but slower. This is method runs all the requisite checks for inside() and more, as it needs it for each dim.
		for(int i = 0; i<Vector.DIMENSIONS;i++) {
			if (position[i] - getDimensions()[i] > 0 || position[i]<0) {
				smallestDimension = -1;	//smallest dim now irrelevant
				outside[i] = true;
				totalOutside = true;
				farside[i] = position[i]>0;	//that is, it's on the 0 side iff the position is on the inside for this dimension, that is, greater than 0, given that the position is known to be outside the shape, given that it's inside this if block
			}
			if (!totalOutside) {	//checks /how/ far in the point is (for this dimension) to ssee if it's the smallest so far. Plus does farsides.
				tmpDist = Math.abs(position[i] - getDimensions()[i]);
				if (minimumDist > tmpDist ) {
					minimumDist = tmpDist;
					smallestDimension = i;
					farside[i] = true;
				}
				tmpDist = Math.abs(position[i]);
				if (minimumDist >  Math.abs(position[i]) ) {
					minimumDist = tmpDist;
					smallestDimension = i;
					farside[i] = false;
				}
			}
		}
		//if it's inside the shape, the point on the edge is just the nearest point that is on the edge. Being on the edge is just defined as having a value of one of the bounds
		//(0 or the getDimensions value) for one of the dimensions. To get nearest, just find the one with the smallest distance or w/e.
		//else, if it's /outside/ all the dimensions which extend beyond the bounds of the shape must be brought to the domain of the shape, that is, set to 0 or the max for the dim, according to it's farsidedness.
		for (int i = 0; i<Vector.DIMENSIONS;i++) { //it might seem that some values might not get farsidedness checked. This is true, but only occurs for values /inside/ the shape. farsidedness is checked when outsidedness is shown to be true.
			if (outside[i] || smallestDimension == i) {		//these conditions are mutually exclusive. Exactly 1 occurs for all points. if any declared outside, no more checked for smallestdim, and SD set to -1, but if none outside, all checked
				position[i] = (float)(farside[i] ? getDimensions()[i]: 0);
			}
		}
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
	public float[] exactCollisionPosition(OutlineShape collider, float[] relativePosition) {
		//This is, technically, a lie. This generates an appropriate point that will output a cogent point for all functions this position could be used for
		//But it's not actually exact, and cannot be used for a distanceIn function accurately.
		//Doesn't need to worry about returning null. checkCollisionBounds is a sufficient condition for some part of the other shape being in here.
		float[] tmp = new float[Vector.DIMENSIONS];
		for (int i = 0; i<Vector.DIMENSIONS; i++) {
			tmp[i] = (float) (collider.getDimensions()[i]/2 - relativePosition[i]);
		}
		return pointOnEdge(tmp);
		}
	
}


