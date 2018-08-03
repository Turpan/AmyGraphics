package movement;

import java.util.List;

import movement.mathDS.Force;
import movement.mathDS.Graph;
import movement.mathDS.Vector;
import movement.mathDS.Vector.MalformedVectorException;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class CollisionEngine {
	private List<Entity> entityList;
	private Graph<Entity, Vector> pointsOfContact;	//directed graph of all the points of contact. Edge values are normal Vectors running /from/ from, /to/ to
	
	public CollisionEngine(List<Entity> entityList) {
		setEntityList(entityList);
	}
	public void setEntityList(List<Entity> entityList) {
		Collections.sort(entityList, new Comparator<Entity>() {	//just so that walls and other non-moving things get sorted last. This enables us to safely check collisions by moving through the list of entities and then checking collisions for all /later/ entities for collisions;
			@Override
			public int compare(Entity e1, Entity e2) {	//Note: this comparator imposes orderings that are inconsistent with equals.
				if ((e1 instanceof Collidable && !(e2 instanceof Collidable))) {
					return -1;
				} else if ((e2 instanceof Collidable && !(e1 instanceof Collidable))) {
					return 1;
				} return 0;
			}
		});
		this.entityList = entityList;
	}
	public List<Entity> getEntityList(){
		return entityList;
	}
	private void setPoC(Graph<Entity, Vector> PoC) {
		pointsOfContact = PoC;
	}
	private Graph<Entity, Vector> getPoC(){
		return pointsOfContact;
	}
	
	public void checkCollision() throws MalformedVectorException {
		setPoC(new Graph<Entity, Vector>());
		getPoC().addVertices(getEntityList());
		for (var entity1 : getEntityList()) {
			if (entity1 instanceof Collidable) {
				collisionDetection(entity1);
			}
		}
		simplifyWallPoCChains();	//This graph needs to be made to get the order that the normal force needs to be calculated to get an accurate, stable, stochastic calculation in one pass.
		getPoC().removeCycles();
		applyNormalForces();
	}
	private void collisionDetection(Entity entity1) throws MalformedVectorException {
		var eL = getEntityList();
		int entity = eL.indexOf(entity1) + 1;
		for (int i = entity; i<eL.size(); i++) {
			var entity2 = eL.get(i);
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
	private void moveableCollision(Moveable object1, Moveable object2, float[] collisionLocation1, float[] collisionLocation2) throws MalformedVectorException {
		var outputForce1 = new Force();
		var outputForce2 = new Force();
		
		var normal1 = object1.getOutline().getNormal(collisionLocation1);
		var normal2 = object2.getOutline().getNormal(collisionLocation2);
		
		getPoC().addEdge(object1, object2, normal1);
		getPoC().addEdge(object2, object1, normal2);
		
		var CoR_Effect = (1+(object1.getCoR() * object2.getCoR()))/2; //you gotta average it with 1, because otherwise a value of 0 would violate conservation of momentum. To get objects that didn't pass through things (barring normal force / teleporting out), you'd need a total CoR_Effect of -1)
		var timeScaleInverse = 1/Moveable.TIMESCALE;
		
		double massRatio = object1.getMass()/object2.getMass();
		var collisionForce1 = new Force(CoR_Effect *timeScaleInverse * Vector.getComponentParallel(object1.getVelocity(),normal2)* object1.getMass() * (1+((1-massRatio)/(1+massRatio))),
										Vector.vectorMovingWith(object1.getVelocity() , normal2) ? Vector.directionOfReverse(normal2) : normal2.getDirection());
		var collisionForce2 = new Force(CoR_Effect * timeScaleInverse* Vector.getComponentParallel(object2.getVelocity(),normal1)* object2.getMass() * (1-((1-massRatio)/(1+massRatio))),
										Vector.vectorMovingWith(object2.getVelocity() , normal1) ? Vector.directionOfReverse(normal1) : normal1.getDirection());

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
			object1.move(new Vector(object2.getOutline().getDistanceIn(relativeCollisionLocation1), normal2.getDirection()));
		}
		for (Entity ent : getEntityList()) {
			if (ent instanceof Wall) {
				if (checkBoundsCollision(object1, ent)) {
					var moveVector = new Vector(object2.getOutline().getDistanceIn(relativeCollisionLocation1), Vector.directionOfReverse(normal2));
					object1.move(moveVector);
					object2.move(moveVector);
				}
			}
		}
		for (int i = 0; i<Vector.DIMENSIONS; i++) {									
			relativeCollisionLocation2[i] = (float) (collisionLocation2[i] + object2.getPosition()[i]- object1.getPosition()[i]); 
		}
		if (object1.getOutline().inside(relativeCollisionLocation2)){
			object2.move(new Vector(object1.getOutline().getDistanceIn(relativeCollisionLocation2), normal1.getDirection()));
		}
		outputForce1.setMagnitude(outputForce1.getMagnitude());
		outputForce2.setMagnitude(outputForce2.getMagnitude());
		object1.applyForce(outputForce1);
		object2.applyForce(outputForce2);
	}
	private void wallCollision(Wall w, Moveable m, float[] collisionLocation) throws MalformedVectorException {
		getPoC().addEdge(w, m, w.getNormal());
		var outputForce = Vector.vectorMovingWith(m.getVelocity(), w.getNormal()) ? 
							new Force() :
							new Force((1+m.getCoR() * w.getBounciness()) *Vector.getComponentParallel(m.getVelocity(), w.getNormal()) * (1/Moveable.TIMESCALE) *m.getMass()
									  ,w.getNormal().getDirection());
		
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
			m.move(new Vector(Vector.getComponentParallel(vectorToPoint, w.getNormal())-0.25, w.getNormal().getDirection()));
			
		}			
		m.applyForce(outputForce);
	}
	private void simplifyWallPoCChains() {
		boolean doubleBondCleaned[] = new boolean[getEntityList().size()];
		ArrayDeque<Entity> toClean = new ArrayDeque<Entity>();
		for (Entity v : getEntityList()) {
			if (v instanceof Wall && !doubleBondCleaned[getEntityList().indexOf(v)]){
				toClean.add(v);
				while (!toClean.isEmpty()) {
					
					cleanDoubleBonds(toClean.pop(), doubleBondCleaned, toClean);
				}
			}
		}
		
	}
	private void cleanDoubleBonds(Entity vertex, boolean[] doubleBondCleaned, ArrayDeque<Entity> toClean) {
		
		doubleBondCleaned[getEntityList().indexOf(vertex)] = true;
		var it = getPoC().getVertexLinkedList(vertex).listIterator(1);
		Entity v;
		while (it.hasNext()) {
			v = it.next();
			if (!toClean.contains(v)) {
				if (getPoC().isDoubleBonded(vertex, v)) {
					getPoC().removeEdge(v, vertex);
				}if (!doubleBondCleaned[getEntityList().indexOf(v)]) { 
					toClean.add(v);
				}
			}
		}
	}
	private void applyNormalForces() throws MalformedVectorException {
		var normalOrder = getPoC().getSortedVertices();
		Iterator<Entity> it;
		Entity normalObject;
		Vector normal;
		Force normalForce;
		for (Entity ent: normalOrder) {
			if (ent instanceof Moveable) {
				ent = (Moveable) ent;
				it = getPoC().getVertexLinkedList(ent).listIterator();
				while (it.hasNext()){
					normalObject = it.next();
					normal = getPoC().getEdgeValue(normalObject, ent);
					normalForce = new Force(Vector.getComponentParallel(((Moveable) ent).getActiveForce(), normal), normal.getDirection());
					((Moveable) ent).applyForce(normalForce);
					if (normalObject instanceof Moveable) {
						((Moveable) normalObject).applyForce((Force) Vector.getReverse(normalForce));
					}
				}
			}
		}
	}
}
