package movement.Shapes;

import java.util.Arrays;

import movement.mathDS.Vector;

public class Ellipse extends OutlineShape{
 
	public Ellipse (double[] dimensions) {
		super(dimensions);

	}
	protected Ellipse(Ellipse ellipse) {
		super(ellipse);
	}
	@Override
	public Vector getNormal(double[] position) {
		//takes the multivariate derivative, which gives a linear function equivalent at the point,
		//generating a plane (or dimensional equiv), and outputting the coefficient vector. Which /is/ the normal. Wild.
		var positionRelativeShape = unrotatePoint(position);
		double[] cmpnts = new double[Vector.DIMENSIONS];
		double[] dims =getDimensions();
		for (int i =0; i<Vector.DIMENSIONS;i++) {
			cmpnts[i] = (positionRelativeShape[i]-dims[i]/2)/(dims[i]*dims[i]);
		}//alright, watch this boys, it'll blow your mind
		var positionVector = new Vector(cmpnts);
		var rotationVector = new Vector(getRotationAxis());		
		return Vector.addVectors(Vector.scalarMultiply(positionVector, Math.cos(getAngle())), Vector.scalarMultiply(Vector.crossProduct(positionVector, rotationVector), Math.sin(getAngle())), Vector.scalarMultiply(rotationVector, (1 - Math.cos(getAngle())) * Vector.dotProduct(rotationVector, positionVector)));
		//I totally stole the algorithm for rotating points from an algorithm for rotating vectors! It's the same thing boys!
		//ok for real I only have like, a 40% confidence in every single individual piece of code I've written for rotations.
	}
	@Override
	public boolean inside(double[] position) {
		var positionRelativeShape = unrotatePoint(position);
		double alteredMagnitude = 0;
		for (int i = 0;i<Vector.DIMENSIONS;i++) {
			alteredMagnitude += Math.pow((positionRelativeShape[i] - getDimensions()[i]/2) / (getDimensions()[i]/2),2);
		}
		return alteredMagnitude<=1;
	}
	@Override
	public double[] pointOnEdge(double[] position) {	//returns the (global) point on the outer edge of the shape inline with this point & the centre
		double alteredMagnitude = 0;
		var positionRelativeShape = unrotatePoint(position);
		for (int i = 0 ; i<Vector.DIMENSIONS;i++) {
			alteredMagnitude += Math.pow((positionRelativeShape[i] - getDimensions()[i]/2) / (getDimensions()[i]/2),2);
		}
		alteredMagnitude = Math.sqrt(alteredMagnitude);
		var output = new double[Vector.DIMENSIONS];
		for (int i = 0 ; i<Vector.DIMENSIONS;i++) {
			output[i] =(positionRelativeShape[i] - getDimensions()[i]/2) / alteredMagnitude + getDimensions()[i]/2 ;
		}
		return rotatePoint(output);
	}
	@Override
	public Ellipse clone() {
		return new Ellipse(this);
	}
}
