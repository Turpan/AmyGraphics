package movement.Shapes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import movement.mathDS.Vector;

public class PolyShape extends OutlineShape{	//essentially, a way of cobining basic shapes.
	
	private Map<OutlineShape, double[]> componentShapes = new HashMap<OutlineShape, double[]>();	//each shape maps to it's relative position in the overall shape. This is over
																									//parametererised. Initialise such that no shapes have negative relative positions
																									//and at least one shape is 0 in each dimension. I.E, as close to the 0 corner as 
																									//possible without going negative for any shape. So checkBoundsCollision() works.
	public PolyShape(Map<OutlineShape,double[]> componentShapes) {
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
	}
	protected PolyShape(PolyShape polyShape) {
		super(polyShape);
		var clonedComponentShapes = new HashMap<OutlineShape, double[]>();
		Iterator<OutlineShape> it = polyShape.getComponentShapes().keySet().iterator();
		while (it.hasNext()) {
			var nextShape = it.next();
			clonedComponentShapes.put(nextShape.clone(), polyShape.getComponentShapes().get(nextShape).clone());
		}
		setComponentShapes(clonedComponentShapes);
		setRotationAxis(getRotationAxis());
		setAngle(getAngle());
		setCentreOfRotation(getCentreOfRotation());
	}
	
	private Map<OutlineShape, double[]> getComponentShapes(){
		return componentShapes;
	}
	private void setComponentShapes(Map<OutlineShape, double[]> componentShapes) {
		this.componentShapes = componentShapes;
	}
	
	@Override
	public void setCentreOfRotation(double[] centreOfRotation) {
		super.setCentreOfRotation(centreOfRotation);
		double[] tmp;
		double[] relativePosition;
		for(OutlineShape component : getComponentShapes().keySet()) {
			tmp = centreOfRotation.clone();
			relativePosition = getComponentShapes().get(component);
			for (int i = 0;i<3;i++) {
				tmp[i] -= relativePosition[i];
			}
			component.setCentreOfRotation(tmp);
		}
	} 
	@Override
	public void setAngle(double angle) {
		super.setAngle(angle);
		for(OutlineShape component : getComponentShapes().keySet()) {
			component.setAngle(angle);
		}
	} 
	@Override
	public void setRotationAxis(double[] rotationAxis) {
		super.setRotationAxis(rotationAxis);
		for(OutlineShape component : getComponentShapes().keySet()) {
			component.setRotationAxis(rotationAxis);
		}
	} 	
	@Override
	public Vector getNormal(double[] position) {
		double minimumDistFromSurface = Double.MAX_VALUE;
		double tmpDist;
		double[] positionInShape;
		double[] relativePosition;
		OutlineShape surfaceShape = null;		//the shape whose surface is closest to the point;
		for (OutlineShape component : getComponentShapes().keySet()) {
			positionInShape = position.clone();
			relativePosition =  rotatePoint(getComponentShapes().get(component));
			for (int i = 0; i<Vector.DIMENSIONS; i++) {
				positionInShape[i] -= relativePosition[i];
			}
			tmpDist = component.distanceIn(positionInShape);
			if (Math.abs(tmpDist)<Math.abs(minimumDistFromSurface)) {
				minimumDistFromSurface = tmpDist;
				surfaceShape = component;
			}
		}
		positionInShape = position.clone();
		relativePosition =  rotatePoint(getComponentShapes().get(surfaceShape));
		
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
		OutlineShape component;
		Iterator<OutlineShape> it = getComponentShapes().keySet().iterator();
		while(!output && it.hasNext()) {
			component = it.next();
			positionInShape = unrotatePoint(position.clone());
			relativePosition = getComponentShapes().get(component);
			for (int j = 0; j<Vector.DIMENSIONS; j++) {
				positionInShape[j] -= relativePosition[j];
			}
			output = component.inside(component.rotatePoint(positionInShape));
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
		OutlineShape surfaceShape = null;		//the shape whose surface is closest to the point;
		for (OutlineShape component : getComponentShapes().keySet()) {
			positionInShape = unrotatePoint(position.clone());
			relativePosition =  getComponentShapes().get(component);
			for (int i = 0; i<Vector.DIMENSIONS; i++) {
				positionInShape[i] -= relativePosition[i];
			}
			tmpDist = component.distanceIn(component.rotatePoint(positionInShape));
			if (Math.abs(tmpDist)<Math.abs(minimumDistFromSurface)) {
				minimumDistFromSurface = tmpDist;
				surfaceShape = component;
			}
		}
		positionInShape = unrotatePoint(position.clone());
		relativePosition =  getComponentShapes().get(surfaceShape);
		
		for (int i = 0; i<Vector.DIMENSIONS; i++) {
			positionInShape[i] -= relativePosition[i];
		}
		
		output = surfaceShape.pointOnEdge(surfaceShape.rotatePoint(positionInShape));
		for (int i = 0; i<Vector.DIMENSIONS; i++) {
			output[i] += relativePosition[i];
		}
		return output;
	}

	@Override
	public double[] exactCollisionPosition(OutlineShape collider, double[] relativePositionofCollideeToCollider) {	//returns the point nearest the collision, 
		double[] cmpntCentre = new double[3];														//relative to collidees position, BUT in global coords.
		double[] cmpntPos;
		double[] collisionPoint;
		double[] collisionPointRelativeCmpnt = new double[3];
		double[] maxPoint = new double[3];
	
		double tmpDist;
		double maxDist = -Double.MAX_VALUE;
		
		OutlineShape cmpnt;
		var it = getComponentShapes().keySet().iterator();
		while (it.hasNext()) {
			cmpnt = it.next();
			cmpntPos = getComponentShapes().get(cmpnt);
			for (int i = 0; i < 3; i++) {
				cmpntCentre[i] = cmpnt.getDimensions()[i]/2 + cmpntPos[i];
			}
			cmpntCentre = rotatePoint(cmpntCentre);
			for (int i = 0; i < 3; i++) {
				cmpntCentre[i] += relativePositionofCollideeToCollider[i];
			}
			//compare. I need a way to choose a single collision point to output. A single point is taken in by all the related functions, and is all I can work with without intraobject forces, which are a no-go.
			//So I go with the point that's furthest into it's respective cmpnt. Measuring this takes some juggling, but seems like a good solution.
			collisionPoint= collider.pointOnEdge(cmpntCentre);
			cmpntPos = rotatePoint(cmpntPos);
			for (int i = 0; i < 3; i++) {
				collisionPointRelativeCmpnt[i] = collisionPoint[i] - cmpntPos[i] - relativePositionofCollideeToCollider[i];
			}			
			tmpDist = cmpnt.distanceIn(collisionPointRelativeCmpnt);
			if (maxDist < tmpDist) {
				for (int i = 0; i < 3; i++) {
					maxPoint[i] = collisionPoint[i] - relativePositionofCollideeToCollider[i];
				}
				maxDist = tmpDist;
			}
		}
		return maxPoint;
	}
	
	@Override
	public double distanceIn(double[] position) {
		double maximumDist = -Double.MAX_VALUE;
		double tmpDist;
		double[] positionInShape;
		double[] relativePosition;
		for (OutlineShape component : getComponentShapes().keySet()) {
			positionInShape = unrotatePoint(position.clone());
			relativePosition =  getComponentShapes().get(component);
			for (int i = 0; i<Vector.DIMENSIONS; i++) {
				positionInShape[i] -= relativePosition[i];
			}
			tmpDist = component.distanceIn(component.rotatePoint(positionInShape));
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
}
