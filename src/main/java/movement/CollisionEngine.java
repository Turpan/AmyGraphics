package movement;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import movement.mathDS.Graph;
import movement.mathDS.Vector; 

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;


public class CollisionEngine {
	
	private static final double teleportOutBuffer = 2;
	
	private List<Obstacle> obstacleList;
	private List<Movable> movableList;
	private Graph<Collidable, double[][]> pointsOfContact;	//directed graph of all the points of contact. Edge values are size 2 arrays of collision locations. First location is collisionlocation of source, second is collisionlocation of dest

	public CollisionEngine(List<Entity> objectList) {
		setObjectList(objectList);
	}
	public CollisionEngine() {
		this(new ArrayList<Entity>());
	}

	public void setObjectList(List<Entity> objectList) {
		var movables = new ArrayList<Movable>();
		var obstacles = new ArrayList<Obstacle>();
		for (Entity object : objectList) {
			if (object instanceof Obstacle) {
				obstacles.add((Obstacle) object);
			} else if (object instanceof Movable){
				movables.add((Movable) (object));
			}
		}
		setMovableList(movables);
		setObstacleList(obstacles);
	}
	public void add(Entity object) {
		if (object instanceof Obstacle) {
			obstacleList.add((Obstacle) object);
		}else if(object instanceof Movable) {
			movableList.add((Movable) object);
		}
	}
	public void remove(Entity object) {
		obstacleList.remove(object);
		movableList.remove(object);
	}

	private List<Movable> getMovableList() {
		return movableList;
	}
	private void setMovableList(List<Movable> pushableList) {
		this.movableList = pushableList;
	}
	private List<Obstacle> getObstacleList() {
		return obstacleList;
	}
	private void setObstacleList(List<Obstacle> obstacleList) {
		this.obstacleList = obstacleList;
	}
	public List<Collidable> getTotalList(){
		var totalList = new ArrayList<Collidable>();
		totalList.addAll(getMovableList());
		totalList.addAll(getObstacleList());
		return totalList; 
	}
	private void setPoC(Graph<Collidable, double[][]> PoC) {
		pointsOfContact = PoC;
	}
	private Graph<Collidable, double[][]> getPoC(){
		return pointsOfContact;
	}

	public void checkCollisions() {
		setPoC(new Graph<Collidable, double[][]>());
		getPoC().addVertices(getTotalList());
		for (var object1 : getMovableList()) {
			collisionDetection(object1);
		}
		organiseCollisionOrder();
		applyNormalForces();
		applyVelocityCollisions();
	}
	private void collisionDetection(Movable object1) {
		var eL = getTotalList();
		int start = eL.indexOf(object1) + 1;
		for (int i = start; i<eL.size(); i++) {
			var object2 = eL.get(i);
			if (checkBoundsCollision(object1, object2)) {
				addToCollisionGraph(object1,object2);
				
			}
		}
	}

	private boolean checkBoundsCollision(Movable object1, Collidable object2) {
		return Math.sqrt(Math.pow(object1.getDimensions()[0],2) + Math.pow(object1.getDimensions()[1],2) + Math.pow(object1.getDimensions()[2],2)) +
				Math.sqrt(Math.pow(object2.getDimensions()[0],2) + Math.pow(object2.getDimensions()[1],2) + Math.pow(object2.getDimensions()[2],2)) 
		> Math.sqrt(Math.pow(object1.getPosition()[0] - object2.getPosition()[0],2) + Math.pow(object1.getPosition()[1]-object2.getPosition()[1],2) + Math.pow(object1.getPosition()[2]-object2.getPosition()[2],2));
	}
	private void addToCollisionGraph(Movable object1, Collidable object2) {
		var collisionLocation2in1 = object1.getOutline().exactCollisionPosition(object2.getOutline(), relativeLocation(new double[Vector.DIMENSIONS],object1, object2));
		if (object1.getOutline().inside(collisionLocation2in1)) {
			var collisionLocation1in2 = object2.getOutline().exactCollisionPosition(object1.getOutline(), relativeLocation(new double[Vector.DIMENSIONS],object2, object1));
			if (object2.getOutline().inside(collisionLocation2in1)) {
				double[][] edge1to2 = {collisionLocation2in1, collisionLocation1in2};
				double[][] edge2to1 = {collisionLocation1in2, collisionLocation2in1};
				getPoC().addEdge(object1, object2, edge1to2);
				getPoC().addEdge(object2, object1, edge2to1);
			}
		}
	}

	private void moveableCollision(Movable object1, Movable object2, double[][] collisionLocations) {
		
		object1.extraCollisionEffect(object2);
		object2.extraCollisionEffect(object1);
		
		var normal1 = object1.getOutline().getNormal(collisionLocations[0]);
		var normal2 = object2.getOutline().getNormal(collisionLocations[1]);


		var CoR_Effect = (1+(object1.getCoR() * object2.getCoR()))/2; //you gotta average it with 1, because otherwise a value of 0 would violate conservation of momentum. It's actually just how the math works out 2.
		var timeScaleInverse = 1/Movable.TIMESCALE;

		double massRatio = object1.getMass()/object2.getMass();

		var collisionForce1 = new Vector(CoR_Effect *timeScaleInverse * Vector.getComponentParallel(object1.getVelocity(),normal2)* object1.getMass() * (1+((1-massRatio)/(1+massRatio))),
				Vector.vectorMovingWith(object1.getVelocity() , normal2) ? Vector.directionOfReverse(normal2) : normal2.getDirection());
		var collisionForce2 = new Vector(CoR_Effect * timeScaleInverse* Vector.getComponentParallel(object2.getVelocity(),normal1)* object2.getMass() * (1-((1-massRatio)/(1+massRatio))),
				Vector.vectorMovingWith(object2.getVelocity() , normal1) ? Vector.directionOfReverse(normal1) : normal1.getDirection());

		object1.applyForce(Vector.addVectors(collisionForce1, Vector.getReverse(collisionForce2)));
		object2.applyForce(Vector.addVectors(collisionForce2, Vector.getReverse(collisionForce1)));
		//assures that if the object gets in, it gets teleported immediately out
		//ignores 'ghost' components of the movable. 
		var collisionLocation1in2 = object1.getCorporealOutline().exactCollisionPosition(object2.getCorporealOutline(), relativeLocation(new double[Vector.DIMENSIONS],object1, object2));
		if (object1.getCorporealOutline().inside(collisionLocation1in2)) {
			var relativeEdgeLocation1in2 = relativeLocation(object1.getOutline().pointOnEdge(collisionLocation1in2), object1, object2);
			double distance;
			if (object2.getCorporealOutline().inside(relativeEdgeLocation1in2)){
				distance = object2.getCorporealOutline().distanceIn(relativeEdgeLocation1in2);
				if (distance > teleportOutBuffer) {
					var normalInQuestion = object2.getCorporealOutline().getNormal(relativeEdgeLocation1in2);
					object1.move(new Vector((distance - teleportOutBuffer)/2, normalInQuestion.getDirection()));
					object2.move(new Vector((distance - teleportOutBuffer)/2, Vector.directionOfReverse(normalInQuestion)));
				}
			}
		}
	}
	private void obstacleCollision(Obstacle o, Movable m, double[][] collisionLocations) {
		
		var normalObstacle = o.getOutline().getNormal(collisionLocations[0]);
		var outputForce = Vector.vectorMovingWith(m.getVelocity(), normalObstacle) ?
				new Vector(new double[]{0,0,0}) :
				new Vector((1+m.getCoR() * o.getCoR()) *Vector.getComponentParallel(m.getVelocity(), normalObstacle) * (1/Movable.TIMESCALE) *m.getMass()
						,normalObstacle.getDirection());
		m.applyForce(outputForce);
		//assures that if the object gets in, it gets teleported immediately out
		var collisionLocationOinM = m.getCorporealOutline().exactCollisionPosition(o.getOutline(), relativeLocation(new double[Vector.DIMENSIONS],m, o));
		if (m.getCorporealOutline().inside(collisionLocationOinM)) {
			o.collision(m);
			double distanceEdgeIn = m.getOutline().distanceIn(collisionLocationOinM);
			if (distanceEdgeIn>teleportOutBuffer ) {
				m.move(new Vector(distanceEdgeIn-teleportOutBuffer, normalObstacle.getDirection()));
			}
		}
	}

	private void organiseCollisionOrder() {	//sorts the movable list, such that they are in the appropriate order to be processed
		//little bit of terminology. By 'abstraction', I mean the number of objects between an object and a wall.
		Map<Movable,int[]> abstractionMap = new HashMap<Movable, int[]>();	//an object with 0 abstraction, relative a wall, either hasn't been measured yet, or isn't connected to it.
		for (Movable mov : getMovableList()) {
			abstractionMap.put(mov, new int[getObstacleList().size()]);
		}
		for (Obstacle obs : getObstacleList()) {
			OCOObstacleMethod(obs, abstractionMap, getObstacleList().indexOf(obs));
		}
		Map<Movable, Integer> maxAbstractionMap = new HashMap<Movable,Integer>();
		for(Movable mov :getMovableList()) {
			int maxAbstraction = 0;
			for (int i :abstractionMap.get(mov)) {
				if (i > maxAbstraction) { maxAbstraction = i;}
			}
			maxAbstractionMap.put(mov,maxAbstraction);
		}
		getMovableList().sort((mov1,mov2) -> maxAbstractionMap.get(mov1).compareTo(maxAbstractionMap.get(mov1)));
		Collections.reverse(getMovableList());
	}

	private void OCOObstacleMethod(Obstacle obs, Map<Movable, int[]> abstractionMap, int indexOfRootObstacle) {	//this actually has some slight troubles with certain setups that are isomorphic in terms of graph
		Queue<Movable> movableQueue = new ArrayDeque<Movable>();	//connections, but different in terms of which objects are "on top" of the other. This is an edge case, and will be treated somewhate reasonably.
		for(Collidable col:getPoC().getVertexConnections(obs)) {	//there is no way to distinguish these cases using a method like this. The method would be required to somehow be aware of the directions of the
			abstractionMap.get(col)[indexOfRootObstacle] = 1;		//forces acting on things and the direction to the wall in question. This would be i)more complex (read: slower), ii) not /super/ useful,
			movableQueue.add((Movable) col);						// iii) bowing down to specificity
		}
		while (!movableQueue.isEmpty()) {
			OCOMovableMethod(movableQueue.remove(), abstractionMap, indexOfRootObstacle, movableQueue);
		}
	}

	private void OCOMovableMethod(Movable mov, Map<Movable,int[]> abstractionMap, int indexOfRootObstacle, Queue<Movable> movableQueue) {
		for(Collidable col:getPoC().getVertexConnections(mov)) {
			if (getMovableList().contains(col) && !movableQueue.contains(col) && abstractionMap.get(col)[indexOfRootObstacle] == 0) {
				abstractionMap.get(col)[indexOfRootObstacle] = abstractionMap.get(mov)[indexOfRootObstacle] + 1;
				movableQueue.add((Movable) col);
			}
		}
	}

	private void applyNormalForces() {	//should only be run after movable list is organised. Requires that the object that is furthest from a wall has it's velocity collision done first
		var collisionOrder = getMovableList();							//this is because the result of one normal force being applied has an effect on the results of others. Turns out the order of calculation for doing
		Vector collideeNormal;											//this in one pass is to do the objects with the highest "wall abstraction" first.
		Vector colliderNormal;
		for (Movable collider: collisionOrder) {
			for (Collidable collidee : getPoC().getVerticesConnectedTo(collider)) {
				collideeNormal = collidee.getOutline().getNormal(getPoC().getEdgeValue(collidee, collider)[0]);
				if (getObstacleList().contains(collidee)){
					if (!Vector.vectorMovingWith(collider.getActiveForce(), collideeNormal)){
						collider.applyForce(new Vector(Vector.getComponentParallel(collider.getActiveForce(), collideeNormal), collideeNormal.getDirection()));
					}
				}else if (collisionOrder.indexOf(collider)< collisionOrder.indexOf(collidee)) {
					if (!Vector.vectorMovingWith(collider.getActiveForce(), collideeNormal)){
						collider.applyForce( new Vector(Vector.getComponentParallel(collider.getActiveForce(), collideeNormal), collideeNormal.getDirection()));
						((Movable) collidee).applyForce(new Vector(Vector.getComponentParallel(collider.getActiveForce(), collideeNormal), Vector.directionOfReverse(collideeNormal)));
					}
					colliderNormal = collider.getOutline().getNormal(getPoC().getEdgeValue(collidee, collider)[1]);
					if (!Vector.vectorMovingWith(((Movable)collidee).getActiveForce()  ,colliderNormal)){
						((Movable) collidee).applyForce( new Vector(Vector.getComponentParallel(((Movable) collidee).getActiveForce(), colliderNormal), colliderNormal.getDirection()));
						collider.applyForce( new Vector(Vector.getComponentParallel(((Movable) collidee).getActiveForce(), colliderNormal), Vector.directionOfReverse(colliderNormal)));
					}
				}
			}
		}
	}

	private void applyVelocityCollisions() {	//doesn't care about order. This is because velocity values don't change immediately after a velocity collision, rather, a force is a applied
		var collisionOrder = getMovableList();									// which alters the velocity later. In the snapshot of time which this set of collisions takes place in, velocity is constant.
		for (Movable collider: collisionOrder) {
			for (Collidable collidee : getPoC().getVerticesConnectedTo(collider)) {
				if (getObstacleList().contains(collidee)) {
					obstacleCollision((Obstacle)collidee, collider, getPoC().getEdgeValue(collidee, collider));
				} else if (collisionOrder.indexOf(collider) < collisionOrder.indexOf(collidee)){	//to avoid double collisions (counting A hitting B & B hitting A, essentially doing one collision twice)
					moveableCollision(collider, (Movable)collidee, getPoC().getEdgeValue(collider, collidee));
				}
			}
		}
	}


	static private double[] relativeLocation(double[] locationRelative1, Entity object1, Entity object2) {
		double[] locationRelative2 = new double[Vector.DIMENSIONS];
		for (int i = 0; i<Vector.DIMENSIONS; i++) {
			locationRelative2[i] = locationRelative1[i] + object1.getPosition()[i] - object2.getPosition()[i];
		}
		return locationRelative2;
	}
}
