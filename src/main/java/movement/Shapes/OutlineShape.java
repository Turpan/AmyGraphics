package movement.Shapes;

import movement.mathDS.Vector;

public abstract class OutlineShape {
	private double[] dimensions;
	private double[] rotationAxis = {1,0,0};		
	private double angle = 0;
	private double[] centreOfRotation = {0,0,0};
	
	public OutlineShape() {}
	public OutlineShape(OutlineShape outlineShape) {
		setDimensions(dimensions.clone());
		setRotationAxis(outlineShape.getRotationAxis().clone());
		setAngle(outlineShape.getAngle());
		setCentreOfRotation(outlineShape.getCentreOfRotation().clone());
	}
	
	
	public void setDimensions(double[] dimensions) {
		for (double dim : dimensions) {
			if (dim <0) {dim = 0;}
		}
		if (dimensions.length != Vector.DIMENSIONS) {
			this.dimensions = new double[Vector.DIMENSIONS];
			for (int i = 0; i<dimensions.length && i<Vector.DIMENSIONS; i++) {
				this.dimensions[i] = dimensions[i];
			}
		}else {
		this.dimensions= dimensions;
		}
	}
	public double[] getDimensions() {
		return dimensions;
	}
	public void setRotationAxis(double[] rotationAxis) {
		var tmp = rotationAxis.clone();		//don't want to be destructive of direction...
		if (tmp.length < 3) {	//don't need to check for tmp>DIM, as, due to the implementation, this doesn't actually matter.
			var tmp2 = new double[3];
			for(int i = 0; i< tmp.length; i++) {
				tmp2[i] = tmp[i];
			}
			tmp = tmp2;
		}
		double check = 0;
		for (int i = 0; i<3;i++) {
			check += tmp[i]*tmp[i];
		}
		if (check<0.999||check>1.001) {//technically, should equal 1, but slight rounding errors, working with irrational numbers converted to decimal.
			for (int i = 0; i<3;i++) {
				tmp[i] = tmp[i]/check;
			}
		}
		this.rotationAxis = tmp;
	}
	public double[] getRotationAxis() {
		return rotationAxis;
	}
	public double getAngle() {
		return angle;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}
	public double[] getCentreOfRotation() {
		return centreOfRotation;
	}
	public void setCentreOfRotation(double[] centreOfRotation) {
		this.centreOfRotation = centreOfRotation;
	}
	public double[] rotatePoint(double[] positionOnEntity) {
		var cmpnts = new double[Vector.DIMENSIONS];
		for (int i = 0; i< Vector.DIMENSIONS; i++) {
			cmpnts[i] = positionOnEntity[i] - getCentreOfRotation()[i];
		}
		var positionVector = new Vector(cmpnts);
		var rotationVector = new Vector(getRotationAxis());
		var output = Vector.addVectors(Vector.scalarMultiply(positionVector, Math.cos(getAngle())), Vector.scalarMultiply(Vector.crossProduct(positionVector, rotationVector), Math.sin(getAngle())), Vector.scalarMultiply(rotationVector, (1 - Math.cos(getAngle())) * Vector.dotProduct(rotationVector, positionVector))).getComponents(); 
		for (int i = 0; i< Vector.DIMENSIONS; i++) {
			output[i] += getCentreOfRotation()[i];
		}
		return output;
	}
	public double[] unrotatePoint(double[] positionOnEntity) {		//feed it a point in the space. This will counteract the rotation around the shape, s.t. you can tell whether it should be in the entity/where.
		var cmpnts = new double[Vector.DIMENSIONS];					
		for (int i = 0; i< Vector.DIMENSIONS; i++) {
			cmpnts[i] = positionOnEntity[i] - getCentreOfRotation()[i];
		}
		var positionVector = new Vector(cmpnts);
		var rotationVector = new Vector(getRotationAxis());
		var output = Vector.addVectors(Vector.scalarMultiply(positionVector, Math.cos(-getAngle())), Vector.scalarMultiply(Vector.crossProduct(positionVector, rotationVector), Math.sin(-getAngle())), Vector.scalarMultiply(rotationVector, (1 - Math.cos(-getAngle())) * Vector.dotProduct(rotationVector, positionVector))).getComponents(); 
		for (int i = 0; i< Vector.DIMENSIONS; i++) {
			output[i] += getCentreOfRotation()[i];
		}
		return output;
	}
	
	public abstract Vector getNormal(double[] position); //normal of the shape at a point.
	public abstract boolean inside(double[] position);
	public abstract double[] exactCollisionPosition(OutlineShape collider, double[] relativePosition);
	public abstract double[] pointOnEdge(double[] position);
	
	public double distanceIn(double[] position) {//returns a negative value for values outside of the shape
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
	
	public abstract OutlineShape clone();
}
