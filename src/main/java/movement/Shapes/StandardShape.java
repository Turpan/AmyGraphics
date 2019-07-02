package movement.Shapes;

import java.util.ArrayList;

import movement.mathDS.Vector;

public abstract class StandardShape extends OutlineShape {
	final static int collisionDetectionGranularity = 2 * 10;	//Leave this EVEN! (Being even means that the divisions along all axis will,
	private ArrayList<double[]> collisionNet;					//amongst other places, contain the middle linee. This helps define the edges of, for example, spheres.	This just naturally cleans everything up for a 
																//bunch of shapes with positive curvature at... the middle points. In general, there /should/ be a collision net point at the peak of every shape with a
																//positive curvature if you want it to sit /nicely/ for a force moving it into that direction. Making it even just solves this problem for spheres and parabaloids
	protected StandardShape() {}
	public StandardShape(double[] dimensions) {
		setDimensions(dimensions);
		initialiseCollisionNet();
	}
	protected StandardShape(StandardShape standardShape) {
		super(standardShape);
		var collisionNet = new ArrayList<double[]>();
		for (double[] point : standardShape.getCollisionNet()) {
			collisionNet.add(point.clone());
		}
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
	public double[] exactCollisionPosition(OutlineShape collidee, double[] relativePositionCollideeToCollider) {	//returns the collision position relative to the "real world". Accounts for rotation in answer
		var net = getCollisionNet();

		double[] sum = new double[Vector.DIMENSIONS];
		int numPointsInside = 0;
		double[] point = new double[Vector.DIMENSIONS];

		for (double[] f : net) {		
			point = rotatePoint(f);		
			for (int i = 0; i<Vector.DIMENSIONS;i++) {
				point[i] += relativePositionCollideeToCollider[i];
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
		return rotatePoint(sum);
	}

	public abstract StandardShape clone();
}
