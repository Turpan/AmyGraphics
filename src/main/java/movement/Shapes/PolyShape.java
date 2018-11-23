package movement.Shapes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import movement.mathDS.Vector;

public class PolyShape extends StandardShape{		//essentially, a way of cobining basic shapes.
	
	private Map<StandardShape, double[]> componentShapes;		//each shape maps to it's relative position in the overall shape. This is over
														//parametererised. Initialise such that no shapes have negative relative positions
														//and at least one shape is 0 in each dimension. I.E, as close to the 0 corner as 
														//possible without going negative for any shape. So checkBoundsCollision() works.
	public PolyShape(Map<StandardShape,double[]> componentShapes) {
		setComponentShapes(componentShapes);
		double[] relativePosition;
		double[] dimensions = new double[Vector.DIMENSIONS];
		double tmpDim;
		for(OutlineShape component : getComponentShapes().keySet()) {
			relativePosition = getComponentShapes().get(component);
			for (int i = 0; i<Vector.DIMENSIONS;i++) {
				tmpDim = component.getDimensions()[i] + relativePosition[i];
				if (dimensions[i] < tmpDim) {
					dimensions[i] = tmpDim;
				}
			}
		}
		setDimensions(dimensions);
		initialiseCollisionNet();
	}
	protected PolyShape(PolyShape polyShape) {
		super(polyShape);
		var clonedComponentShapes = new HashMap<StandardShape, double[]>();
		Iterator<StandardShape> it = polyShape.getComponentShapes().keySet().iterator();
		while (it.hasNext()) {
			var nextShape = it.next();
			clonedComponentShapes.put(nextShape.clone(), polyShape.getComponentShapes().get(nextShape).clone());
		}
		setComponentShapes(clonedComponentShapes);
	}
	
	private Map<StandardShape, double[]> getComponentShapes(){
		return componentShapes;
	}
	private void setComponentShapes(Map<StandardShape, double[]> componentShapes) {
		this.componentShapes = componentShapes;
	}
	
	@Override
	public Vector getNormal(double[] position) {
		double minimumDistFromSurface = Double.MAX_VALUE;
		double tmpDist;
		double[] positionInShape;
		double[] relativePosition;
		StandardShape surfaceShape = null;		//the shape whose surface is closest to the point;
		for (StandardShape component : getComponentShapes().keySet()) {
			positionInShape = position;
			relativePosition =  getComponentShapes().get(component);
			for (int i = 0; i<Vector.DIMENSIONS; i++) {
				positionInShape[i] -= relativePosition[i];
			}
			tmpDist = component.distanceIn(positionInShape);
			if (Math.abs(tmpDist)<Math.abs(minimumDistFromSurface)) {
				minimumDistFromSurface = tmpDist;
				surfaceShape = component;
			}
		}
		positionInShape = position;
		relativePosition =  getComponentShapes().get(surfaceShape);
		
		for (int i = 0; i<Vector.DIMENSIONS; i++) {
			positionInShape[i] -= relativePosition[i];
		}
		return surfaceShape.getNormal(positionInShape);
	}
	@Override
	public boolean inside(double[] position) {
		double[] positionInShape;
		double[] relativePosition;
		boolean output = false;
		StandardShape component;
		Iterator<StandardShape> it = getComponentShapes().keySet().iterator();
		while(!output && it.hasNext()) {
			component = it.next();
			positionInShape = position;
			relativePosition =  getComponentShapes().get(component);
			for (int j = 0; j<Vector.DIMENSIONS; j++) {
				positionInShape[j] -= relativePosition[j];
			}
			output = component.inside(positionInShape);
		}
		return output;
	}
	@Override
	public double[] pointOnEdge(double[] position) {
		double minimumDistFromSurface = Double.MAX_VALUE;
		double tmpDist;
		double[] positionInShape;
		double[] relativePosition;
		double[] output;
		StandardShape surfaceShape = null;		//the shape whose surface is closest to the point;
		for (StandardShape component : getComponentShapes().keySet()) {
			positionInShape = position;
			relativePosition =  getComponentShapes().get(component);
			for (int i = 0; i<Vector.DIMENSIONS; i++) {
				positionInShape[i] -= relativePosition[i];
			}
			tmpDist = component.distanceIn(positionInShape);
			if (Math.abs(tmpDist)<Math.abs(minimumDistFromSurface)) {
				minimumDistFromSurface = tmpDist;
				surfaceShape = component;
			}
		}
		positionInShape = position;
		relativePosition =  getComponentShapes().get(surfaceShape);
		
		for (int i = 0; i<Vector.DIMENSIONS; i++) {
			positionInShape[i] -= relativePosition[i];
		}
		
		output = surfaceShape.pointOnEdge(positionInShape);
		
		for (int i = 0; i<Vector.DIMENSIONS; i++) {
			output[i] += relativePosition[i];
		}
		return output;
	}

	@Override
	public double distanceIn(double[] position) {
		double maximumDist = -Double.MAX_VALUE;
		double tmpDist;
		double[] positionInShape;
		double[] relativePosition;
		for (OutlineShape component : getComponentShapes().keySet()) {
			positionInShape = position;
			relativePosition =  getComponentShapes().get(component);
			for (int i = 0; i<Vector.DIMENSIONS; i++) {
				positionInShape[i] -= relativePosition[i];
			}
			tmpDist = component.distanceIn(positionInShape);
			if (tmpDist>maximumDist) {
				maximumDist = tmpDist;
			}
		}
		return maximumDist;
	}
	
	@Override
	public PolyShape clone() {
		return new PolyShape(this);
	}

	@Override
	protected boolean inCollisionNet(double[] point) {		//some points that might traditionally be considered too far inside the shape, so as to save space, 
		double[] pointInShape;								//will end up in the collisionNet. Eh. Not a huge deal.
		double[] relativePosition;
		boolean output = false;
		Iterator<StandardShape> it = getComponentShapes().keySet().iterator();
		StandardShape currentShape;
		while(!output && it.hasNext()) {
			currentShape = it.next();
			pointInShape = point;
			relativePosition =  getComponentShapes().get(currentShape);
			for (int i = 0; i<Vector.DIMENSIONS; i++) {
				pointInShape[i] -= relativePosition[i];
			}
			output = currentShape.inCollisionNet(pointInShape);
		}
		return output;
	}

}
