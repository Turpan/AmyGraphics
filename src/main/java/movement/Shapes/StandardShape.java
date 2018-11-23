package movement.Shapes;

import java.util.ArrayList;

import movement.mathDS.Vector;

public abstract class StandardShape implements OutlineShape {
	final static int collisionDetectionGranularity = 2 * 10;	//MUST BE EVEN! (Being even means that the divisions along all axis will,
	private ArrayList<double[]> collisionNet;				//amongst other places, contain the middle linee. This helps define the edges of, for example, spheres.
	private double[] dimensions;

	protected StandardShape() {}
	public StandardShape(double[] dimensions) {
		setDimensions(dimensions.clone());
		initialiseCollisionNet();
	}
	protected StandardShape(StandardShape standardShape) {
		setDimensions(standardShape.getDimensions());
		collisionNet = standardShape.getCollisionNet();
	}
	@Override
 	public void setDimensions(double[] dimensions) {
		this.dimensions= dimensions;
	}
	@Override
	public double[] getDimensions() {
		return dimensions;
	}
	public ArrayList<double[]> getCollisionNet(){
		return collisionNet;
	}

	protected void initialiseCollisionNet() {
		collisionNet = new ArrayList<double[]>();
		double [][] possibleCoords = new double[Vector.DIMENSIONS][];

		for (int i = 0; i<Vector.DIMENSIONS;i++) {
			double grainSize = getDimensions()[i]/collisionDetectionGranularity;
			possibleCoords[i] = new double[collisionDetectionGranularity + 1];
			for (int j = 0; j<=collisionDetectionGranularity ; j++) {
				possibleCoords[i][j] = grainSize * j;
			}
		}
		double[][] points = new double[(int) Math.pow(collisionDetectionGranularity+1,Vector.DIMENSIONS)][Vector.DIMENSIONS] ;
		int chunkSize;
		int chunkCounter;
		int chunkLocation;
		for (int i = 0; i<Vector.DIMENSIONS;i++) {
			chunkSize = (int) Math.pow(collisionDetectionGranularity+1,Vector.DIMENSIONS-i-1);
			chunkCounter = 0;
			chunkLocation = 0;
			for (int j = 0; j < Math.pow(collisionDetectionGranularity+1,Vector.DIMENSIONS);j++) {
				points[j][i] = possibleCoords[i][chunkCounter%(collisionDetectionGranularity+1)];
				chunkLocation++;
				if (chunkLocation == chunkSize) {
					chunkLocation = 0;
					chunkCounter++;
				}
			}
		}
		for (double[] point : points) {
			if (inCollisionNet(point)) {
				collisionNet.add(point);
			}
		}
	}


	protected abstract boolean inCollisionNet(double[] point);	//similar to inside, but a little more... discriminating, so as to lower the number of points in 
																//to cycle through in CN
	@Override
	public double[] exactCollisionPosition(OutlineShape collidee, double[] relativePositionCollideeToCollider) {
		var net = getCollisionNet();

		double[] sum = new double[Vector.DIMENSIONS];
		int numPointsInside = 0;
		double[] point = new double[Vector.DIMENSIONS];

		for (double[] f : net) {
			for (int i = 0; i<Vector.DIMENSIONS;i++) {
				point[i] = f[i] + relativePositionCollideeToCollider[i];
			}
			if (collidee.inside(point)){
				numPointsInside++;
				for(int j = 0; j<Vector.DIMENSIONS;j++) {
					sum[j] += f[j];
				}
			}
		}if (numPointsInside == 0) {
			return null;
		}
		for (int i = 0;i<Vector.DIMENSIONS;i++) {
			sum[i] =sum[i]/ numPointsInside;
		}
		return sum;
	}
	
	public abstract StandardShape clone();
}
