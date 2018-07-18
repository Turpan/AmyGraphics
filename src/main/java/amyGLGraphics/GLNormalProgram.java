package amyGLGraphics;

public class GLNormalProgram extends GLWorldProgram {
	
	@Override
	protected void createShaders() {
		createVertexShader("shaders/normalvertex.glsl");
		createGeometryShader("shaders/normalgeom.glsl");
		createFragmentShader("shaders/normalfragment.glsl");
	}
}
