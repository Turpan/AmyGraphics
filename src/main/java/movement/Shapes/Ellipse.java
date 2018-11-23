package movement.Shapes;

import movement.mathDS.Vector;

public class Ellipse extends StandardShape{

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
		Vector output = new Vector();
		double[] cmpnts = new double[Vector.DIMENSIONS];
		double[] dims =getDimensions();
		for (int i =0; i<Vector.DIMENSIONS;i++) {
			cmpnts[i] = (position[i]-dims[i]/2)/(dims[i]*dims[i]);
		}
		output.setComponents(cmpnts);
		return output;
	}
	@Override
	public boolean inside(double[] position) {
		double alteredMagnitude = 0;
		for (int i = 0;i<Vector.DIMENSIONS;i++) {
			alteredMagnitude += Math.pow((position[i] - getDimensions()[i]/2) / (getDimensions()[i]/2),2);
		}
		return alteredMagnitude<=1;
	}
	@Override
	public double[] pointOnEdge(double[] position) {	//returns the point on the outer edge of the shape inline with this point & the centre
		double alteredMagnitude = 0;
		for (int i = 0 ; i<Vector.DIMENSIONS;i++) {
			alteredMagnitude += Math.pow((position[i] - getDimensions()[i]/2) / (getDimensions()[i]/2),2);
		}
		alteredMagnitude = Math.sqrt(alteredMagnitude);
		var output = new double[Vector.DIMENSIONS];
		for (int i = 0 ; i<Vector.DIMENSIONS;i++) {
			output[i] =(position[i] - getDimensions()[i]/2) / alteredMagnitude + getDimensions()[i]/2 ;
		}
		return output;

	}
	@Override
	public double distanceIn(double[] position) {
		var edgePosition = pointOnEdge(position);
		var sum = 0;
		for (int i = 0 ; i<Vector.DIMENSIONS;i++) {
			sum += Math.pow(position[i] - edgePosition[i],2);
		}
		var distance = Math.sqrt(sum);
		if (!inside(position)){
			distance *= -1;
		}
		return distance;
	}
	@Override
	protected boolean inCollisionNet(double[] point) {
		double sum = 0;
		for (int i = 0;i<Vector.DIMENSIONS;i++) {
			sum += Math.pow(point[i] - getDimensions()[i]/2,2) / Math.pow((getDimensions()[i]/2),2);
		}
		return sum<=1 && sum>=0.5;
	}
	@Override
	public Ellipse clone() {
		return new Ellipse(this);
	}
}
