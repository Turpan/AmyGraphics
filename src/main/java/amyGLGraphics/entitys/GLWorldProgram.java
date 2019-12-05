package amyGLGraphics.entitys;

import org.joml.Matrix4f;

import amyGLGraphics.base.GLProgram;

public class GLWorldProgram extends GLProgram{

	public static final float RATIO = 16f/9f;
	public static final float NEAR = 0.1f;
	public static final float FAR = 20f;
	public static final float FOV = (float) Math.toRadians(90f);
	
	//private static final Matrix4f perspective
	//= new Matrix4f().perspective(FOV, RATIO, NEAR, FAR);
	
	private static final Matrix4f perspective
	= new Matrix4f().ortho(-RATIO, RATIO, -1, 1, NEAR, FAR);

	private int modelLocation;
	private int viewLocation;
	private int persLocation;

	@Override
	public void createProgram() {
		super.createProgram();
	}

	@Override
	protected void linkProgram() {
		super.linkProgram();
		setUpVars();
	}

	protected void setUpVars() {
		modelLocation = queryVariable("model");
		viewLocation = queryVariable("view");
		persLocation = queryVariable("projection");
		updatePersMatrix(perspective);
	}

	public void updatePersMatrix(Matrix4f perspective) {
		updateMat4(perspective, persLocation);
	}

	public void updateModelMatrix(Matrix4f model) {
		updateMat4(model, modelLocation);
	}

	public void updateViewMatrix(Matrix4f view) {
		updateMat4(view, viewLocation);
	}

	@Override
	protected void createShaders() {
		createVertexShader("shaders/worldvertex.glsl");
		createFragmentShader("shaders/worldfragment.glsl");
	}
}
