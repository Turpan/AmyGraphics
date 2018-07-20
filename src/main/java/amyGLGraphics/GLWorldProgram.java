package amyGLGraphics;

import org.joml.Matrix4f;

public class GLWorldProgram extends GLProgram{
	
	private static final Matrix4f perspective
	= new Matrix4f().perspective((float) Math.toRadians(90f), 1.77f, 0.1f, 100f);
		
	private static final Matrix4f view
		= new Matrix4f().lookAt(0f, 3f, -3f, 0f, 0f, 0f, 0f, 1f, 0f);
	
	private int modelLocation;
	private int viewLocation;
	private int persLocation;
	
	public void createProgram() {
		super.createProgram();
	}
	
	protected void linkProgram() {
		super.linkProgram();
		setUpVars();
	}
	
	protected void setUpVars() {
		modelLocation = queryVariable("model");
		viewLocation = queryVariable("view");
		persLocation = queryVariable("projection");
		updateViewMatrix(view);
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
