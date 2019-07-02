package movement.Shapes;

import movement.mathDS.Vector;

public class BigRectangle extends OutlineShape{
	//can't be used for collidables. Gives simplistic collision commands, cannot get a distanceIn.
	//however, it can be arbitrarily large, and smaller things won't be able to go through it.

	public BigRectangle (double[] dimensions) {
		setDimensions(dimensions);
	}
	protected BigRectangle (BigRectangle bigRectangle) {
		super(bigRectangle);
	}
	@Override
	public Vector getNormal(double[] position){
		var pointRelativeShape = unrotatePoint(position);
		double minimumDist = Double.MAX_VALUE;	//trivial upperbound
		double tmpDist = 0;
		int smallestDimension = 0;
		boolean farside = false;
		for(int i = 0; i<Vector.DIMENSIONS;i++) {
			tmpDist = Math.abs(pointRelativeShape[i] - getDimensions()[i]);
			if (minimumDist > tmpDist ) {
				minimumDist = tmpDist;
				smallestDimension = i;
				farside = true;
			}
			tmpDist = Math.abs(pointRelativeShape[i]);
			if (minimumDist >  Math.abs(pointRelativeShape[i]) ) {
				minimumDist = tmpDist;
				smallestDimension = i;
				farside = false;
			}
		}
		double[] direction = new double[Vector.DIMENSIONS];
		direction[smallestDimension] = farside ? 1 : -1;
		var positionVector = new Vector(direction);
		var rotationVector = new Vector(getRotationAxis());
		return Vector.addVectors(Vector.scalarMultiply(positionVector, Math.cos(getAngle())), Vector.scalarMultiply(Vector.crossProduct(positionVector, rotationVector), Math.sin(getAngle())), Vector.scalarMultiply(rotationVector, (1 - Math.cos(getAngle())) * Vector.dotProduct(rotationVector, positionVector)));
	}
	@Override
	public boolean inside(double[] position) {
		var pointRelativeShape = unrotatePoint(position);
		boolean output = true;
		int i = 0;
		while (output && i<Vector.DIMENSIONS) {
			output = (pointRelativeShape[i] >= 0 && pointRelativeShape[i] <= getDimensions()[i]);
			i++;
		}
		return output;
	}
	@Override
	public double[] pointOnEdge(double[] position) {
		//abuses the fact that the shortest distance to a plane is always orthogonal to it, and that the planes are aligned along the axis => other than the axis the plane is orthogonal, the coordinates of the nearest
		//point on the edge remain constnat if within bounds of the rectangle
		var pointRelativeShape = unrotatePoint(position);
		double minimumDist = Double.MAX_VALUE;	//trivial upperbound
		double tmpDist = 0;
		int smallestDimension = 0;
		boolean[] farside = new boolean[Vector.DIMENSIONS];
		boolean[] outside = new boolean[Vector.DIMENSIONS];	//is this dim outside of range
		boolean totalOutside = false;	//is the whole point outside the rectangle. Could use inside(), but slower. This is method runs all the requisite checks for inside() and more, as it needs it for each dim.
		for(int i = 0; i<Vector.DIMENSIONS;i++) {
			if (pointRelativeShape[i] - getDimensions()[i] > 0 || pointRelativeShape[i]<0) {
				smallestDimension = -1;	//smallest dim now irrelevant
				outside[i] = true;
				totalOutside = true;
				farside[i] = pointRelativeShape[i]>0;	//that is, it's on the 0 side iff the position is on the inside for this dimension, that is, greater than 0, given that the position is known to be outside the shape, given that it's inside this if block
			}
			if (!totalOutside) {	//checks /how/ far in the point is (for this dimension) to ssee if it's the smallest so far. Plus does farsides.
				tmpDist = Math.abs(pointRelativeShape[i] - getDimensions()[i]);
				if (minimumDist > tmpDist ) {
					minimumDist = tmpDist;
					smallestDimension = i;
					farside[i] = true;
				}
				tmpDist = Math.abs(pointRelativeShape[i]);
				if (minimumDist >  Math.abs(pointRelativeShape[i]) ) {
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
				pointRelativeShape[i] = (float)(farside[i] ? getDimensions()[i]: 0);
			}
		}
		return rotatePoint(pointRelativeShape);
	}
//	@Override												NOT REMOVING YET, BECAUSE CLEARLY ALOT WENT INTO THIS, BUT IT'S /really/ DIFFERENT FROM pointOnEdge, and I don't know why? Assuming point on Edge is good, it should be /slightly/ slower to get distanceIn now that I've generalised the method, but it's much simpler.
//	public double distanceIn(double[] position) {
//		boolean[] outside = new boolean[Vector.DIMENSIONS];
//		double[] distance = new double[Vector.DIMENSIONS];
//		double minimumDist = Double.MAX_VALUE;	//trivial upperbound
//		double tmpDist = 0;
//		double tmpDistII = 0;
//		double squareSum = 0;
//		for (int i = 0; i<Vector.DIMENSIONS;i++) {
//			tmpDist = Math.abs(position[i] - getDimensions()[i]);
//			tmpDistII = Math.abs(position[i]);
//			distance[i] = tmpDist < tmpDistII ?  tmpDist : tmpDistII;
//			minimumDist = minimumDist < distance[i]?  minimumDist : distance[i];
//			if (position[i] - getDimensions()[i] > 0 || position[i]<0) {
//				outside[i] = true;
//			}
//		}
//		for(int i = 0; i<Vector.DIMENSIONS;i++) {
//			if (outside[i]) {
//				squareSum += Math.pow(distance[i],2);
//			}
//		}
//		if (squareSum == 0) {
//			return minimumDist;
//		} else {
//			return -Math.sqrt(squareSum);
//		}
//	}
	@Override
	public double[] exactCollisionPosition(OutlineShape collider, double[] relativePosition) {
		//This is, technically, a lie. This generates an appropriate point that will output a cogent point for all functions this position could be used for
		//But it's not actually exact, and cannot be used for a distanceIn function accurately.
		//Doesn't need to worry about returning null. checkCollisionBounds is a sufficient condition for some part of the other shape being in here.
		double[] tmp = new double[Vector.DIMENSIONS];
		for (int i = 0; i<Vector.DIMENSIONS; i++) {
			tmp[i] = collider.getCentreOfRotation()[i]/2 - relativePosition[i];		
		//getCentreOfRotation is actually a pretty good placeholder for the position of the object as a whole... I could have used the centre, adjusted for rotation, but honestly, in the cases where the centre/isn't the centre of rotation, I think CoR is better...
		}
		return pointOnEdge(tmp);
	}
	@Override
	public BigRectangle clone() {
		return new BigRectangle(this);
	}

}


