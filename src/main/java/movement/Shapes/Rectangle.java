package movement.Shapes;

import movement.mathDS.Vector;

public class Rectangle extends OutlineShape{
	//can't be used for collidables. Gives simplistic collision commands, cannot get a distanceIn.
	//however, it can be arbitrarily large, and smaller things won't be able to go through it.
	//should be fine for anything that doesn't have the capacity to be particularly close to 3 sides at once

	public Rectangle (double[] dimensions) {
		setDimensions(dimensions);
	}
	protected Rectangle (Rectangle bigRectangle) {
		super(bigRectangle);
	}
	@Override
	public Vector getNormal(double[] position){
		var pointRelativeShape = unrotatePoint(position);
		double minimumDist = Double.MAX_VALUE;	//trivial upperbound
		double tmpDist = 0;
		int smallestDimension = 0;
		boolean[] farside = new boolean[3];
		double[] direction = new double[Vector.DIMENSIONS]; //0 == in, 1 == out
		for(int i = 0; i<Vector.DIMENSIONS;i++) {
			farside[i] = pointRelativeShape[i] > getDimensions()[i]/2;			
			tmpDist = (farside[i] ? pointRelativeShape[i] : Math.abs(getDimensions()[i] - pointRelativeShape[i]));
			if (minimumDist > tmpDist ) {
				minimumDist = tmpDist;
				smallestDimension = i;
			}
			if (pointRelativeShape[i] >= getDimensions()[i] || pointRelativeShape[i]<=0) {
				direction[i] = 1;
			}
		}
		
		if (direction[0] + direction[1] + direction[2] == 0) {
			direction[smallestDimension] = 1;
		}
		for (int i = 0; i<3;i++) {
			direction[i] *= farside[i] ? 1 : -1;
		}
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
			if (pointRelativeShape[i] > getDimensions()[i] || pointRelativeShape[i]<0) {	//if point outside Rectangle
				smallestDimension = -1;	//smallest dim now irrelevant
				outside[i] = true;
				totalOutside = true;
			}
			
			farside[i] = pointRelativeShape[i] > getDimensions()[i]/2;
			if (!totalOutside) {	//checks /how/ far in the point is (for this dimension) to ssee if it's the smallest so far.
				tmpDist = (farside[i] ? pointRelativeShape[i] : Math.abs(getDimensions()[i] - pointRelativeShape[i]));
				if (minimumDist > tmpDist ) {
					minimumDist = tmpDist;
					smallestDimension = i;
				}
			}
		}
			
		//if it's inside the shape, the point on the edge is just the nearest point that is on the edge. Being on the edge is just defined as having a value of one of the bounds
		//(0 or the getDimensions value) for one of the dimensions. To get nearest, just find the one with the smallest distance or w/e.
		//else, if it's /outside/ all the dimensions which extend beyond the bounds of the shape must be brought to the domain of the shape, that is, set to 0 or the max for the dim, according to it's farsidedness.
		for (int i = 0; i<Vector.DIMENSIONS;i++) { //it might seem that some values might not get farsidedness checked. This is true, but only occurs for values /inside/ the shape. farsidedness is checked when outsidedness is shown to be true.
			if (outside[i] || smallestDimension == i) {
				pointRelativeShape[i] = (float)(farside[i] ? getDimensions()[i]: 0);
			}
		}
		return rotatePoint(pointRelativeShape);
	}
	@Override
	public Rectangle clone() {
		return new Rectangle(this);
	}

}


