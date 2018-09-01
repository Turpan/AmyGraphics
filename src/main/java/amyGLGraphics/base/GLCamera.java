package amyGLGraphics.base;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class GLCamera {
	Vector3f position;
	Vector3f centre;
	Vector3f up;
	
	public GLCamera() {
		setDefault();
	}
	
	public void setDefault() {
		position = new Vector3f(0.0f, 0.0f, 0.0f);
		centre = new Vector3f(0.0f, 0.0f, -1.0f);
		up = new Vector3f(0.0f, 1.0f, 0.0f);
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public void setCentre(Vector3f centre) {
		this.centre = centre;
	}
	
	public void setUp(Vector3f up) {
		this.up = up;
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getCentre() {
		return centre;
	}

	public Vector3f getUp() {
		return up;
	}

	public Matrix4f getCameraMatrix() {
		Vector3f result = new Vector3f();
		centre.add(position, result);
		return new Matrix4f().lookAt(position, result, up);
	}
}
