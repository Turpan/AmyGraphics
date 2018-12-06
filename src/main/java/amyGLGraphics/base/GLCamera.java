package amyGLGraphics.base;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import amyGLGraphics.entitys.GLWorldProgram;

public class GLCamera {
	private Vector3f position;
	private Vector3f centre;
	private Vector3f orientation;
	private Vector3f up;
	
	private List<Vector3f> nearPlane;
	private List<Vector3f> farPlane;
	private List<Vector3f> planeNormals;

	public GLCamera() {
		setDefault();
	}

	public void setDefault() {
		position = new Vector3f(0.0f, 0.0f, 0.0f);
		centre = new Vector3f(0.0f, 0.0f, -1.0f);
		orientation = new Vector3f(0.0f, 1.0f, 0.0f);
		up = new Vector3f(0.0f, 1.0f, 0.0f);
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setCentre(Vector3f centre) {
		this.centre = centre;
	}

	public void setOrientation(Vector3f orientation) {
		this.orientation = orientation;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getCentre() {
		return centre;
	}

	public Vector3f getOrientation() {
		return orientation;
	}
	
	public Vector3f getUp() {
		return up;
	}

	public void setUp(Vector3f up) {
		this.up = up;
	}

	public boolean isInFustrum(GLObject object) {
		List<Vector3f> objectBounds = getObjectBoundingBox(object);
		List<Vector3f> origins = getNormalOrigins();
		
		for (int i=0; i<6; i++) {
			int out = 0;
			for (Vector3f point : objectBounds) {
				float distance = getPointDistance(planeNormals.get(i), origins.get(i), point);
				
				if (distance < 0) {
					out ++;
				}
			}
			if (out == 8) {
				return false;
			}
		}
		
		return true;
	}
	
	private float getPointDistance(Vector3f normal, Vector3f origin, Vector3f point) {
		float D = new Vector3f().sub(normal).dot(origin);
		
		float distance = normal.dot(point) + D;
		
		return distance;
	}
	
	private List<Vector3f> getObjectBoundingBox(GLObject object) {
		float left = Integer.MAX_VALUE;
		float right = Integer.MIN_VALUE;
		float top = Integer.MIN_VALUE;
		float bottom = Integer.MAX_VALUE;
		float front = Integer.MIN_VALUE;
		float back = Integer.MAX_VALUE;
		
		for (GLVertex vertex : object.getVertices()) {
			Vector4f position = vertex.xyzwVector();
			Matrix4f model = object.getModelMatrix();
			position.mul(model);
			
			if (position.x < left) left = position.x;
			if (position.x > right) right = position.x;
			if (position.y > top) top = position.y;
			if (position.y < bottom) bottom = position.y;
			if (position.z > front) front = position.z;
			if (position.z < back) back = position.z;
		}
		
		List<Vector3f> points = new ArrayList<Vector3f>();
		
		Vector3f ftl = new Vector3f(left, top, front);
		Vector3f ftr = new Vector3f(right, top, front);
		Vector3f fbl = new Vector3f(left, bottom, front);
		Vector3f fbr = new Vector3f(right, bottom, front);
		
		Vector3f btl = new Vector3f(left, top, back);
		Vector3f btr = new Vector3f(right, top, back);
		Vector3f bbl = new Vector3f(left, bottom, back);
		Vector3f bbr = new Vector3f(right, bottom, back);
		
		points.add(ftl);
		points.add(ftr);
		points.add(fbl);
		points.add(fbr);
		
		points.add(btl);
		points.add(btr);
		points.add(bbl);
		points.add(bbr);
		
		return points;
	}
	
	public void calculatePlane() {
		nearPlane = getNearPlaneBounds();
		farPlane = getFarPlaneBounds();
		planeNormals = getPlaneNormals();
	}
	
	public List<Vector3f> getPlaneNormals() {
		Vector3f normal1 = new Vector3f().sub(GLObject.calculateSurfaceNormal(nearPlane.get(0), nearPlane.get(1), nearPlane.get(2)));
		Vector3f normal2 = new Vector3f().sub(GLObject.calculateSurfaceNormal(nearPlane.get(1), farPlane.get(1), nearPlane.get(3)));
		Vector3f normal3 = new Vector3f().sub(GLObject.calculateSurfaceNormal(farPlane.get(0), nearPlane.get(0), farPlane.get(2)));
		Vector3f normal4 = new Vector3f().sub(GLObject.calculateSurfaceNormal(nearPlane.get(2), nearPlane.get(3), farPlane.get(2)));
		Vector3f normal5 = new Vector3f().sub(GLObject.calculateSurfaceNormal(nearPlane.get(1), nearPlane.get(0), farPlane.get(0)));
		Vector3f normal6 = new Vector3f().sub(GLObject.calculateSurfaceNormal(farPlane.get(1), farPlane.get(0), farPlane.get(3)));
		
		List<Vector3f> normals = new ArrayList<Vector3f>();
		
		normals.add(normal1);
		normals.add(normal2);
		normals.add(normal3);
		normals.add(normal4);
		normals.add(normal5);
		normals.add(normal6);
		
		return normals;
	}
	
	private List<Vector3f> getNormalOrigins() {
		List<Vector3f> origins = new ArrayList<Vector3f>();
		
		origins.add(nearPlane.get(0));
		origins.add(nearPlane.get(1));
		origins.add(farPlane.get(0));
		origins.add(nearPlane.get(2));
		origins.add(nearPlane.get(1));
		origins.add(farPlane.get(1));
		
		return origins;
	}
	
	public List<Vector3f> getPlaneBounds(float distance) {
		float nearHeight = (float) (2 * Math.tan(GLWorldProgram.FOV / 2) * distance);
		float nearWidth = nearHeight * GLWorldProgram.RATIO;
		
		/*Vector3f centreDirection = new Vector3f();
		centre.add(position, centreDirection);
		centreDirection.sub(centre);*/
		Vector3f centreDirection = new Vector3f(centre);
		centreDirection.normalize();
		
		Vector3f centre = new Vector3f();
		centre = centreDirection.mul(distance, centre).add(position);
		
		Vector3f left = new Vector3f();
		left = centreDirection.cross(up, left);
		left.normalize();
		
		Vector3f topLeft = new Vector3f();
		topLeft = centre.add(up.mul(nearHeight / 2, new Vector3f()), new Vector3f())
				.sub(left.mul(nearWidth / 2, new Vector3f()), new Vector3f());
		Vector3f topRight = new Vector3f();
		topRight = centre.add(up.mul(nearHeight / 2, new Vector3f()), new Vector3f())
				.add(left.mul(nearWidth / 2, new Vector3f()), new Vector3f());
		Vector3f bottomLeft = new Vector3f();
		bottomLeft = centre.sub(up.mul(nearHeight / 2, new Vector3f()), new Vector3f())
				.sub(left.mul(nearWidth / 2, new Vector3f()), new Vector3f());
		Vector3f bottomRight = new Vector3f();
		bottomRight = centre.sub(up.mul(nearHeight / 2, new Vector3f()), new Vector3f())
				.add(left.mul(nearWidth / 2, new Vector3f()), new Vector3f());
		
		List<Vector3f> points = new ArrayList<Vector3f>();
		
		points.add(topLeft);
		points.add(topRight);
		points.add(bottomLeft);
		points.add(bottomRight);
		
		return points;
	}
	
	public List<Vector3f> getNearPlaneBounds() {
		return getPlaneBounds(GLWorldProgram.NEAR);
	}
	
	public List<Vector3f> getFarPlaneBounds() {
		return getPlaneBounds(GLWorldProgram.FAR);
	}

	public Matrix4f getCameraMatrix() {
		Vector3f result = new Vector3f();
		centre.add(position, result);
		return new Matrix4f().lookAt(position, result, orientation);
	}
}
