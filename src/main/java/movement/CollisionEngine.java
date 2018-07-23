package movement;

import java.util.List;

import movement.Vectors.Acceleration;
import movement.Vectors.Force;
import movement.Vectors.Vector;
import movement.Vectors.Vector.MalformedVectorException;

public class CollisionEngine {
	public void checkCollision(List<Entity> entityList) throws MalformedVectorException {
		for (var entity1 : entityList) {
			if (entity1 instanceof Collidable) {
				collisionDetection(entityList, entity1);
			}
		}
	}
	private void collisionDetection(List<Entity> entityList, Entity entity1) throws MalformedVectorException {
		int entity = entityList.indexOf(entity1) + 1;
		for (int i = entity; i<entityList.size(); i++) {
			var entity2 = entityList.get(i);
			if (checkBoundsCollision(entity1, entity2)) {
				collision(entity1,entity2);
			}
		}
	}
	
	private boolean checkBoundsCollision(Entity entity1, Entity entity2) {
		double edge1;
		double edge2;
		float[] pos1 = entity1.getPosition();
		float[] pos2 = entity2.getPosition();
		double[] dims1 = entity1.getDimensions();
		double[] dims2 = entity2.getDimensions();
		boolean output = true;
		for (int i= 0; output && i<Vector.DIMENSIONS; i++) {
			edge1 = dims1[i] + pos1[i];
			edge2 = dims2[i] + pos2[i];
			output = pos1[i]<edge2 && edge1 > pos2[i];
		}
		return output;
	}
	private float[] exactCollisionPosition(Moveable collider, Entity collidee) {//collider is the thing hitting the other thing, collidee is the thing being hit
		var net = collider.getOutline().getCollisionNet();
		
		float[] sum = new float[Vector.DIMENSIONS];
		int numPointsInside = 0;
		float[] difference = new float[Vector.DIMENSIONS];
		float[] colliderPos = collider.getPosition();
		float[] collideePos = collidee.getPosition();
		float[] point = new float[Vector.DIMENSIONS];
		
		for (int i = 0; i<Vector.DIMENSIONS;i++) {
			difference[i]= colliderPos[i] -collideePos[i];
		}
		for (float[] f : net) {
			for (int i = 0; i<Vector.DIMENSIONS;i++) {
				point[i] = f[i] + difference[i];
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
	
	
	private void collision(Entity object1, Entity object2) throws MalformedVectorException {
		if (object1 instanceof Moveable && object2 instanceof Moveable) {
			var collisionLocation1 = exactCollisionPosition((Moveable)object1, object2);
			var collisionLocation2 = exactCollisionPosition((Moveable)object2, object1);
			if (collisionLocation1 != null && collisionLocation2 != null) {
					moveableCollision((Moveable)object1, (Moveable)object2, collisionLocation1, collisionLocation2);
			}
		}
		if ((object2 instanceof Wall)&&(object1 instanceof Moveable)||((object1 instanceof Wall) && (object2 instanceof Moveable)) ) {
			if (object1 instanceof Wall) {
				float[] collisionPosition1 =  exactCollisionPosition((Moveable)object2, object1);
				if (collisionPosition1 != null) {
					wallCollision((Wall)object1, (Moveable)object2, collisionPosition1 );
				}
			}else {
				float[] collisionPosition2 =  exactCollisionPosition((Moveable)object1, object2);
				if (collisionPosition2!=null){
					wallCollision((Wall)object2, (Moveable)object1, collisionPosition2);
				}
			}	
		}
	}
	private static void moveableCollision(Moveable object1, Moveable object2, float[] collisionLocation1, float[] collisionLocation2) throws MalformedVectorException {
		var outputForce1 = new Force();
		var outputForce2 = new Force();
		
		var normal1 = object1.getOutline().getNormal(collisionLocation1);
		var normal2 = object2.getOutline().getNormal(collisionLocation2);
		
		var CoR_Effect = (1+(object1.getCoR() * object2.getCoR()))/2; //you gotta average it with 1, because otherwise a value of 0 would violate conservation of momentum. To get objects that didn't pass through things (barring normal force / teleporting out), you'd need a total CoR_Effect of -1)
		var timeScaleInverse = 1/Moveable.TIMESCALE;
		
		double massRatio = object1.getMass()/object2.getMass();
		var collisionForce1 = new Force(CoR_Effect *timeScaleInverse * Vector.getComponentParallel(object1.getVelocity(),normal2)* object1.getMass() * (1+((1-massRatio)/(1+massRatio))),
										Vector.vectorMovingWith(object1.getVelocity() , normal2) ? Vector.directionOfReverse(normal2) : normal2.getDirection());
		var collisionForce2 = new Force(CoR_Effect * timeScaleInverse* Vector.getComponentParallel(object2.getVelocity(),normal1)* object2.getMass() * (1-((1-massRatio)/(1+massRatio))),
										Vector.vectorMovingWith(object2.getVelocity() , normal1) ? Vector.directionOfReverse(normal1) : normal1.getDirection());
		
//		addNormalForces(object1, normal2, collisionForce1);
//		addNormalForces(object2, normal1, collisionForce2);

		outputForce1.addVector(collisionForce1);
		outputForce1.addVector(Vector.getReverse(collisionForce2));
		outputForce2.setComponents(Vector.getReverse(outputForce1).getComponents());
		
		//assures that if the object gets in, it gets teleported immediately out
		var relativeCollisionLocation1 = new float[Vector.DIMENSIONS];
		var relativeCollisionLocation2 = new float[Vector.DIMENSIONS];
		for (int i = 0; i<Vector.DIMENSIONS; i++) {									
			relativeCollisionLocation1[i] = (float) (collisionLocation1[i] + object1.getPosition()[i]- object2.getPosition()[i]); 
		}
		if (object2.getOutline().inside(relativeCollisionLocation1)){
			System.out.println(" aa " );
			object1.teleport(new Vector(object2.getOutline().getDistanceIn(relativeCollisionLocation1), normal2.getDirection()));
		}
		for (int i = 0; i<Vector.DIMENSIONS; i++) {									
			relativeCollisionLocation2[i] = (float) (collisionLocation2[i] + object2.getPosition()[i]- object1.getPosition()[i]); 
		}
		if (object1.getOutline().inside(relativeCollisionLocation2)){
			object2.teleport(new Vector(object1.getOutline().getDistanceIn(relativeCollisionLocation2), normal1.getDirection()));
		}
		outputForce1.setMagnitude(outputForce1.getMagnitude());
		outputForce2.setMagnitude(outputForce2.getMagnitude());
		object1.applyForce(outputForce1);
		object2.applyForce(outputForce2);
	}
	private static void wallCollision(Wall w, Moveable m, float[] collisionLocation) throws MalformedVectorException {

		var outputForce = Vector.vectorMovingWith(m.getVelocity(), w.getNormal()) ? 
							new Force() :
							new Force((1+m.getCoR() * w.getBounciness()) *Vector.getComponentParallel(m.getVelocity(), w.getNormal()) * (1/Moveable.TIMESCALE) *m.getMass()
									  ,w.getNormal().getDirection());
							
//		addNormalForces(m, w.getNormal(), outputForce);
		
		//assures that if the object gets in, it gets teleported immediately out
		var edgeCollisionLocation = m.getOutline().getPointOnEdge(collisionLocation);
		var vectorToPoint = new Vector();	//from the outer corner of the wall
		float[] relativeCollisionLocation = new float[Vector.DIMENSIONS];
		float[] relativeAdjustedCollisionLocation = new float[Vector.DIMENSIONS];
		var dimensionsVector = new Vector();
		
		dimensionsVector.setComponents(w.getDimensions());
		if (Vector.vectorMovingWith(dimensionsVector, w.getNormal())){
			for (int i = 0; i<Vector.DIMENSIONS;i++) {
				relativeAdjustedCollisionLocation[i] = (float) (edgeCollisionLocation[i] + m.getPosition()[i]- w.getPosition()[i] - w.getNormal().getDirection()[i] * w.getWidth());
				relativeCollisionLocation[i] = (float) (collisionLocation[i] + m.getPosition()[i]- w.getPosition()[i] - w.getNormal().getDirection()[i] * w.getWidth());
			}
		}else {
			for (int i = 0; i<Vector.DIMENSIONS;i++) {
				relativeAdjustedCollisionLocation[i] = edgeCollisionLocation[i] + m.getPosition()[i]- w.getPosition()[i];
				relativeCollisionLocation[i] = collisionLocation[i] + m.getPosition()[i]- w.getPosition()[i];
				}
		}
		if (w.inside(relativeAdjustedCollisionLocation) || w.inside(relativeCollisionLocation)){
			var cmpnts = new double[Vector.DIMENSIONS];
			for (int i = 0; i<Vector.DIMENSIONS;i++) {
				cmpnts[i] =(relativeCollisionLocation[i]);
			}
			vectorToPoint.setComponents(cmpnts);
			m.teleport(new Vector(Vector.getComponentParallel(vectorToPoint, w.getNormal())-0.1, w.getNormal().getDirection()));
			
		}			
		m.applyForce(outputForce);
	}
	
//	private static void addNormalForces(Moveable m, Vector normal, Force outputForce) throws MalformedVectorException {
//		var normalForce = new Force();														
//		for (Acceleration a: m.getAccelerations()) {
//			if (!a.active() && !Vector.vectorMovingWith(a, normal)) {
//			normalForce.setMagnitude(m.getMass()*Vector.getComponentParallel(a, normal));
//			normalForce.setDirection(normal.getDirection());
//			outputForce.addVector(normalForce);
//			}
//		}	
//	}
}
