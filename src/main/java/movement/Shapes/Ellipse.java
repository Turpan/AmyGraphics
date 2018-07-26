package movement.Shapes;

import java.util.ArrayList;

import movement.Vectors.Vector;
import movement.Vectors.Vector.MalformedVectorException;

public class Ellipse implements OutlineShape{
	private double[] dimensions;
	private ArrayList<float[]> collisionNet;
	
	public Ellipse (double[] dimensions) {
		setDimensions(dimensions);
		initialiseCollisionNet();
		
	}
	public void initialiseCollisionNet() {
		collisionNet = new ArrayList<float[]>();
		float [][] possibleCoords = new float[Vector.DIMENSIONS][];

		for (int i = 0; i<Vector.DIMENSIONS;i++) {
			float grainSize = (float) (getDimensions()[i]/OutlineShape.collisionDetectionGranularity);
			possibleCoords[i] = new float[OutlineShape.collisionDetectionGranularity + 1];
			for (int j = 0; j<=OutlineShape.collisionDetectionGranularity ; j++) {
				possibleCoords[i][j] = grainSize * j;
			}
		}
		float[][] points = new float[(int) Math.pow(OutlineShape.collisionDetectionGranularity+1,Vector.DIMENSIONS)][Vector.DIMENSIONS] ;
		int chunkSize;
		int chunkCounter;
		int chunkLocation;
		for (int i = 0; i<Vector.DIMENSIONS;i++) {
			chunkSize = (int) Math.pow(OutlineShape.collisionDetectionGranularity+1,Vector.DIMENSIONS-i-1);
			chunkCounter = 0;
			chunkLocation = 0;
			for (int j = 0; j < Math.pow(OutlineShape.collisionDetectionGranularity+1,Vector.DIMENSIONS);j++) {
				points[j][i] = possibleCoords[i][chunkCounter%(OutlineShape.collisionDetectionGranularity+1)];
				chunkLocation++;
				if (chunkLocation == chunkSize) {
					chunkLocation = 0;
					chunkCounter++;
				}				
			}
		}	
		double sum;
		for (float[] point : points) {
				sum = 0;
				for (int i = 0;i<Vector.DIMENSIONS;i++) {
					sum += Math.pow(point[i] - getDimensions()[i]/2,2) / Math.pow((getDimensions()[i]/2),2);
				}
				if (sum<=1 && sum>=0.5) {
					collisionNet.add(point);
				}
		}
	}
	public void setDimensions(double[] dimensions) {
		this.dimensions= dimensions;
	}
	public double[] getDimensions() {
		return dimensions;
	}
	public Vector getNormal(float[] position) throws MalformedVectorException {	
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
	public ArrayList<float[]> getCollisionNet() {
		return collisionNet;
	}
	public boolean inside(float[] position) {
		double alteredMagnitude = 0;
		for (int i = 0;i<Vector.DIMENSIONS;i++) {
			alteredMagnitude += Math.pow(position[i] - getDimensions()[i]/2,2) / Math.pow((getDimensions()[i]/2),2);
		}
		return alteredMagnitude<=1;
	}
	public float[] getPointOnEdge(float[] position) {	//returns the point on the outer edge of the shape inline with this point & the centre
		double alteredMagnitude = 0;
		for (int i = 0 ; i<Vector.DIMENSIONS;i++) {
			alteredMagnitude += Math.pow(position[i] - getDimensions()[i]/2,2) / Math.pow((getDimensions()[i]/2),2);
		}
		var output = new float[Vector.DIMENSIONS];
		for (int i = 0 ; i<Vector.DIMENSIONS;i++) {
			output[i] = (float) ((position[i] - getDimensions()[i]/2) / alteredMagnitude + getDimensions()[i]/2) ;
		}
		return output;
		
	}
	public double getDistanceIn(float[] position) {
		var edgePosition = getPointOnEdge(position);
		var sum = 0;
		for (int i = 0 ; i<Vector.DIMENSIONS;i++) {
			sum += Math.pow(position[i] - edgePosition[i],2);
		}
		return Math.sqrt(sum);
	}
}
