package movement.Shapes;

import java.util.ArrayList;

import movement.mathDS.Vector;

public abstract class NormalShape implements OutlineShape {
	final static int collisionDetectionGranularity = 2 * 10;	//MUST BE EVEN! (Being even means that the divisions along all axis will, 
	private ArrayList<float[]> collisionNet;				//amongst other places, contain the middle line. This helps define the edges of, for example, spheres.
	double[] dimensions;

	public NormalShape(double[] dimensions) {
		setDimensions(dimensions);
		initialiseCollisionNet();
	}
	public void setDimensions(double[] dimensions) {
		this.dimensions= dimensions;
	}
	public double[] getDimensions() {
		return dimensions;
	}
	public ArrayList<float[]> getCollisionNet(){
		return collisionNet;
	}
	
	public void initialiseCollisionNet() {
		collisionNet = new ArrayList<float[]>();
		float [][] possibleCoords = new float[Vector.DIMENSIONS][];

		for (int i = 0; i<Vector.DIMENSIONS;i++) {
			float grainSize = (float) (getDimensions()[i]/collisionDetectionGranularity);
			possibleCoords[i] = new float[collisionDetectionGranularity + 1];
			for (int j = 0; j<=collisionDetectionGranularity ; j++) {
				possibleCoords[i][j] = grainSize * j;
			}
		}
		float[][] points = new float[(int) Math.pow(collisionDetectionGranularity+1,Vector.DIMENSIONS)][Vector.DIMENSIONS] ;
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
		for (float[] point : points) {
				if (inCollisionNet(point)) {
					collisionNet.add(point);
				}
		}
	}

	
	protected abstract boolean inCollisionNet(float[] point);
	
	@Override
	public float[] exactCollisionPosition(OutlineShape collidee, float[] relativePositionCollideeToCollider) {
		var net = getCollisionNet();
		
		float[] sum = new float[Vector.DIMENSIONS];
		int numPointsInside = 0;
		float[] point = new float[Vector.DIMENSIONS];
		
		for (float[] f : net) {
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
}
